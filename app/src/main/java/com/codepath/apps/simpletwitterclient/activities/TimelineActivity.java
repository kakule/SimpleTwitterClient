package com.codepath.apps.simpletwitterclient.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.simpletwitterclient.interfaces.EndlessRecyclerViewScrollListener;
import com.codepath.apps.simpletwitterclient.R;
import com.codepath.apps.simpletwitterclient.adapters.RecycleTweetsAdapter;
import com.codepath.apps.simpletwitterclient.adapters.SpacesItemDecoration;
import com.codepath.apps.simpletwitterclient.application.TwitterApplication;
import com.codepath.apps.simpletwitterclient.application.TwitterClient;
import com.codepath.apps.simpletwitterclient.models.Tweet;
import com.codepath.apps.simpletwitterclient.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.Delete;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements
        ComposeDialogFragment.PostTweetDialogListener {
    private User me;
    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private RecycleTweetsAdapter aTweets;
    private RecyclerView rvTweets;
    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;
    Toolbar toolBar;
    private SwipeRefreshLayout swipeContainer;
    //decoration for recycleview
    SpacesItemDecoration decoration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        setUpViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    void setUpViews() {
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        // Display icon in the toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // Remove default title text
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(refreshSwipe);

        rvTweets = (RecyclerView) findViewById(R.id.rvTweets);
        //create the arraylist (data source)
        tweets = new ArrayList<>();
        //construct the adapter from data source
        aTweets = new RecycleTweetsAdapter(this, tweets);
        //connect adapter to recycleview
        rvTweets.setAdapter(aTweets);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        // Set layout manager to position the items
        rvTweets.setLayoutManager(linearLayoutManager);
        decoration = new SpacesItemDecoration(4);
        rvTweets.addItemDecoration(decoration);
        client = TwitterApplication.getRestClient(); //singleton client
        getUserDetails();
        tweets.addAll((ArrayList) Tweet.recentItems());
        if (tweets != null && tweets.isEmpty()) {
            populateTimeline(1, 0);
        } else {
            Log.d("Debug-createview", tweets.toString());
            aTweets.notifyDataSetChanged();
        }
        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                return loadNextDataFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvTweets.addOnScrollListener(scrollListener);
        //add click listener to adapter
        aTweets.setOnItemClickListener(new RecycleTweetsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                FragmentManager fm = getSupportFragmentManager();
                DetailedTweetFragment detailFrag =
                        DetailedTweetFragment.newInstance(tweets.get(position));
                detailFrag.show(fm, "detailfragment");
            }
        });
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
    //Send an API request to get the timeline json
    //Fill the view by creating the tweet object from the json
    private boolean populateTimeline(final long since_id, long max_id) {
        boolean status = true;
        if (isNetworkAvailable() && isOnline()) {
            client.getHomeTimeline(since_id, max_id, new JsonHttpResponseHandler() {
                //SUCCESS
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.d("DEBUG", response.toString());
                    if (since_id == 1) {
                        // Delete a whole table
                        Delete.table(Tweet.class);
                        tweets.clear();
                        // Now we call setRefreshing(false) to signal refresh has finished
                        swipeContainer.setRefreshing(false);
                    }
                    //deserialize JSON, create models, load the data into listview
                    tweets.addAll(Tweet.fromJSONArray(response));
                    aTweets.notifyDataSetChanged();

                }

                //FAILURE
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                }
            });
        } else {
            informNoInternet();
            status = false;
        }
        return status;
    }

    public boolean loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()
        Log.d("DEBUG", "offset is " + offset);
        long id = tweets.get(tweets.size() - 1).getUid();
        Log.d("DEBUG", "tweests size is " + tweets.size() + " and uid of last is " + id);
        //populateTimeline((offset * TwitterClient.NUM_TWEETS_LOADED) + 1);
        return populateTimeline(0, id);
    }

    public void getUserDetails() {
        if (isNetworkAvailable() && isOnline()) {
            client.getAuthorisedUser(new JsonHttpResponseHandler() {
                //SUCCESS
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("DEBUG", response.toString());
                    //deserialize JSON, create models, load the data into listview
                    me = User.fromJSON(response);
                }

                //FAILURE
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                }
            });
        } else {
            informNoInternet();
        }
    }

    public void onComposeAction (View v) {
        if (isNetworkAvailable() && isOnline()) {
            FragmentManager fm = getSupportFragmentManager();
            ComposeDialogFragment composeFrag = ComposeDialogFragment.newInstance(me.getProfileImageUrl());
            composeFrag.show(fm, "composefragment");
        } else {
            informNoInternet();
        }
    }

    @Override
    public void onFinishDialog(String inputText) {
        client.postTweet(inputText, new JsonHttpResponseHandler(){
            //SUCCESS
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                //deserialize JSON, create models, load the data into listview
                try {
                    Tweet postedTweet = Tweet.fromJSON(response);
                    tweets.add(0, postedTweet);
                    aTweets.notifyItemInserted(0);
                    rvTweets.scrollToPosition(0);
                    Log.d("DEBUG", response.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //FAILURE
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    SwipeRefreshLayout.OnRefreshListener refreshSwipe = new SwipeRefreshLayout.OnRefreshListener(){
        @Override
        public void onRefresh() {
            if (isNetworkAvailable() && isOnline()) {
                populateTimeline(1, 0);
            } else {
                informNoInternet();
                swipeContainer.setRefreshing(false);
            }
        }
    };

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }

    private void informNoInternet () {
        Toast.makeText(this, "No Internet Connection Available", Toast.LENGTH_LONG).show();
    }
}
