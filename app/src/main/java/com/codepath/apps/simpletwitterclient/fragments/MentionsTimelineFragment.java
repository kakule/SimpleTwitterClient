package com.codepath.apps.simpletwitterclient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codepath.apps.simpletwitterclient.application.TwitterApplication;
import com.codepath.apps.simpletwitterclient.clients.TwitterClient;
import com.codepath.apps.simpletwitterclient.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created on 11/5/2016.
 */

public class MentionsTimelineFragment extends TweetListFragment {

    private TwitterClient client;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        client = TwitterApplication.getRestClient(); //singleton client
        populateTimeline(1, 0);
    }

    //Send an API request to get the timeline json
    //Fill the view by creating the tweet object from the json
    private boolean populateTimeline(final long since_id, long max_id) {
        boolean status = true;
        if (client.isInternetAvailable()) {
        client.getMentionsTimeline(since_id, max_id, new JsonHttpResponseHandler() {
            //SUCCESS
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG", response.toString());
                if (since_id == 1) {
                    // Delete a whole table
                    //Delete.table(Tweet.class);
                    //tweets.clear();
                    addAll(Tweet.fromJSONArray(response), true);
                } else {
                    addAll(Tweet.fromJSONArray(response), false);
                }
                swipeContainer.setRefreshing(false);
            }

            //FAILURE
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
                swipeContainer.setRefreshing(false);
            }
        });
        }

        return status;
    }
}
