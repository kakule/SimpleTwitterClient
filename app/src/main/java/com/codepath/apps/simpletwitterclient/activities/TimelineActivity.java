package com.codepath.apps.simpletwitterclient.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.astuetz.PagerSlidingTabStrip;
import com.bumptech.glide.Glide;
import com.codepath.apps.simpletwitterclient.R;
import com.codepath.apps.simpletwitterclient.adapters.RecycleTweetsAdapter;
import com.codepath.apps.simpletwitterclient.application.TwitterApplication;
import com.codepath.apps.simpletwitterclient.clients.TwitterClient;
import com.codepath.apps.simpletwitterclient.fragments.ComposeDialogFragment;
import com.codepath.apps.simpletwitterclient.fragments.HomeTimelineFragment;
import com.codepath.apps.simpletwitterclient.fragments.MentionsTimelineFragment;
import com.codepath.apps.simpletwitterclient.fragments.TweetListFragment;
import com.codepath.apps.simpletwitterclient.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TimelineActivity extends AppCompatActivity implements
        ComposeDialogFragment.PostTweetDialogListener,
        TweetListFragment.TimelineListener {
    private User me;
    private User otherUser;
    TwitterClient client;
    Toolbar toolBar;
    FloatingActionButton fb;
    ViewPager vpPager;
    ImageView ivToolbarProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        client = TwitterApplication.getRestClient();
        setUpViews();
    }

    View.OnClickListener onProfileView = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (client.isInternetAvailable()) {
                Intent i = new Intent(TimelineActivity.this, ProfileActivity.class);
                i.putExtra("user", Parcels.wrap(me));
                startActivity(i);
            }
        }
    };

    void setUpViews() {
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        // Display icon in the toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // Remove default title text
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //find toolbar item
        ivToolbarProfile = (ImageView) toolBar.findViewById(R.id.iv_toolbar_profile);
        ivToolbarProfile.setOnClickListener(onProfileView);
        //Get viewpager
        vpPager = (ViewPager) findViewById(R.id.viewpager);
        //show and hide fab
        vpPager.addOnPageChangeListener(fabListener);
        //Set the viewpager adapter
        vpPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));
        //Find the sliding tabstrip
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        //Attach the tabstrip to the viewpager
        tabStrip.setViewPager(vpPager);
        // find the floating action button
        fb = (FloatingActionButton) findViewById(R.id.fbCompose);
        // get the user details
        getSelfDetails();
        //check if intent was received
        getImplicitIntent();
    }

    public void getImplicitIntent () {
        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {

                // Make sure to check whether returned data will be null.
                String titleOfPage = intent.getStringExtra(Intent.EXTRA_SUBJECT);
                String urlOfPage = intent.getStringExtra(Intent.EXTRA_TEXT);
                Uri imageUriOfPage = intent.getParcelableExtra(Intent.EXTRA_STREAM);

                //To do
            }
        }
    }


    public void getSelfDetails() {
        if (client.isInternetAvailable()) {
            client.getAuthorisedUser(new JsonHttpResponseHandler() {
                //SUCCESS
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("DEBUG", response.toString());
                    //deserialize JSON, create models, load the data into listview
                    me = User.fromJSON(response);
                    //Also set profile pic
                    Glide.with(TimelineActivity.this).load(me.getProfileImageUrl())
                            .bitmapTransform(new RoundedCornersTransformation(TimelineActivity.this, 3, 3))
                            .into(ivToolbarProfile);
                }

                //FAILURE
                @Override
                public void onFailure(int statusCode, Header[] headers,
                                      Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                }
            });
        }
    }

    public void getUserDetails(String screen_name) {
        if (client.isInternetAvailable()) {
            client.getUserDetails(screen_name, new JsonHttpResponseHandler() {
                //SUCCESS
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("DEBUG", response.toString());
                    //deserialize JSON, create models, load the data into listview
                    //Start intent with user details
                    Intent i = new Intent(TimelineActivity.this, ProfileActivity.class);
                    i.putExtra("user", Parcels.wrap(User.fromJSON(response)));
                    startActivity(i);
                }

                //FAILURE
                @Override
                public void onFailure(int statusCode, Header[] headers,
                                      Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                }
            });
        }
    }

    public void onComposeAction (View v) {
        startComposeFragment("");
    }

    public void startComposeFragment(String screename) {
        if (client.isInternetAvailable()) {
            FragmentManager fm = getSupportFragmentManager();
            ComposeDialogFragment composeFrag = ComposeDialogFragment
                    .newInstance(me.getProfileImageUrl(),
                                 screename);
            composeFrag.show(fm, "composefragment");
        }
    }
    @Override
    public void onFinishDialog(String inputText) {
        HomeTimelineFragment homeTimeline = (HomeTimelineFragment) getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.viewpager + ":" +
                        "0");
        Fragment currentTimeline = getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.viewpager + ":" +
                        vpPager.getCurrentItem());

        if (currentTimeline != null) {
            if (!(currentTimeline instanceof HomeTimelineFragment)) {
                vpPager.setCurrentItem(0);
            }
            homeTimeline.postTweet(inputText);
        }
    }

    //Listener for Hometimeline Fragment
    @Override
    public void onTweetClick(User user, int type) {

        if (type == RecycleTweetsAdapter.PROFILE_VIEW) {
            Intent i = new Intent(TimelineActivity.this, ProfileActivity.class);
            i.putExtra("user", Parcels.wrap(user));
            startActivity(i);
        } else if (type == RecycleTweetsAdapter.REPLY_VIEW) {
            startComposeFragment(user.getScreenName());
        } else if (type == RecycleTweetsAdapter.SCREENNAME_TEXT) {
            getUserDetails(user.getScreenName());
        }
    }

    //Listener for showing / hiding fab
    ViewPager.OnPageChangeListener fabListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    fb.show();
                    break;
                default:
                    fb.hide();
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    //Return the order of fragments in the view pager
    public class TweetsPagerAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = {"HomeView", "Mentions"};

        public TweetsPagerAdapter (FragmentManager fm) {
            super (fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new HomeTimelineFragment();
            } else if (position == 1) {
                return new MentionsTimelineFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

    }
}
