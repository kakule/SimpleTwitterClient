package com.codepath.apps.simpletwitterclient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.codepath.apps.simpletwitterclient.R;
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

public class UserTimelineFragment extends TweetListFragment {
    private TwitterClient client;
    ProgressBar pbProgress;
    String screen_name;

    public static UserTimelineFragment newInstance(String screen_name) {
        UserTimelineFragment usrTimeFrag = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screen_name);
        usrTimeFrag.setArguments(args);
        return usrTimeFrag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient(); //singleton client
        //get the progress bar
        pbProgress = (ProgressBar) getActivity().findViewById(R.id.toolbar_user)
                .findViewById(R.id.pbProgressAction);
        screen_name = getArguments().getString("screen_name");
        populateTimeline(1, 0);
    }

    //Send an API request to get the timeline json
    //Fill the view by creating the tweet object from the json
    private boolean populateTimeline(final long since_id, long max_id) {
        pbProgress.setVisibility(ProgressBar.VISIBLE);
        boolean status = true;
        if (client.isInternetAvailable()) {
            client.getUsersTimeline(since_id, max_id, screen_name, new JsonHttpResponseHandler() {
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
                    pbProgress.setVisibility(View.GONE);
                }

                //FAILURE
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {

                    swipeContainer.setRefreshing(false);
                    pbProgress.setVisibility(View.GONE);
                    Log.d("DEBUG", errorResponse.toString());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                    swipeContainer.setRefreshing(false);
                    pbProgress.setVisibility(View.GONE);
                    Log.d("DEBUG", errorResponse.toString());
                }
            });
        }
        return status;
    }

    @Override
    public void refreshView() {
        if (client.isInternetAvailable())
            populateTimeline(1,0);
    }

    @Override
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
}
