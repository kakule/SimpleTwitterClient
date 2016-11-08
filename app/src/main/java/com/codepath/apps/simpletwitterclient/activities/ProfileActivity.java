package com.codepath.apps.simpletwitterclient.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.codepath.apps.simpletwitterclient.R;
import com.codepath.apps.simpletwitterclient.fragments.TweetListFragment;
import com.codepath.apps.simpletwitterclient.fragments.UserHeaderFragment;
import com.codepath.apps.simpletwitterclient.fragments.UserTimelineFragment;
import com.codepath.apps.simpletwitterclient.models.User;

import org.parceler.Parcels;

public class ProfileActivity extends AppCompatActivity implements
        TweetListFragment.TimelineListener{

    Toolbar toolBar;
    TextView tvUserScreenName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolBar = (Toolbar) findViewById(R.id.toolbar_user);
        setSupportActionBar(toolBar);
        // Display icon in the toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        // Remove default title text
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //toolBar.findViewById(R.id.pbProgressAction).setVisibility(ProgressBar.VISIBLE);
        //Get the user passed from activity
        User cur_user= Parcels.unwrap(getIntent().getParcelableExtra("user"));
        if (savedInstanceState == null) {
            tvUserScreenName = (TextView) toolBar.findViewById(R.id.tvProfileTitle);
            tvUserScreenName.setText("@" + cur_user.getScreenName());
            //create user timeline fragment
            UserTimelineFragment fragmentUserTimeline = UserTimelineFragment
                    .newInstance(cur_user.getScreenName());
            UserHeaderFragment fragmentUserHeader = UserHeaderFragment
                    .newInstance(cur_user);
            //Display user fragment
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, fragmentUserTimeline);
            ft.commit();
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flUserHeader, fragmentUserHeader);
            ft.commit();
        }
    }

    //Listener for Hometimeline Fragment
    @Override
    public void onTweetClick(User user, int type) {

        // Do nothing for now
    }
}
