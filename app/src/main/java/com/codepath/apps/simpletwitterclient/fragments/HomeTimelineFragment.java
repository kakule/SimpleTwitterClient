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
import com.raizlabs.android.dbflow.sql.language.Delete;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created on 11/4/2016.
 */

public class HomeTimelineFragment extends TweetListFragment {

    private TwitterClient client;
    boolean scrolltobegin = false;
    ProgressBar pbProgress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = TwitterApplication.getRestClient(); //singleton client
        ArrayList dbTweets = (ArrayList) Tweet.recentItems();
        //get the progress bar
        pbProgress = (ProgressBar) getActivity().findViewById(R.id.toolbar)
                .findViewById(R.id.pbProgressAction);
        if (dbTweets == null || dbTweets.isEmpty()) {
            populateTimeline(1, 0);
        } else {
            addAll(dbTweets, true);
        }

    }


    //Send an API request to get the timeline json
    //Fill the view by creating the tweet object from the json
    private boolean populateTimeline(final long since_id, long max_id) {
        boolean status = true;
        if (client.isInternetAvailable()) {
            pbProgress.setVisibility(ProgressBar.VISIBLE);
            client.getHomeTimeline(since_id, max_id, new JsonHttpResponseHandler() {
                //SUCCESS
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.d("DEBUG", response.toString());
                    if (since_id == 1) {
                        // Delete a whole table
                        Delete.table(Tweet.class);
                        addAll(Tweet.fromJSONArray(response), true);
                    } else {
                        addAll(Tweet.fromJSONArray(response), false);
                    }
                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false);
                    //Remove progressbar after network call completion
                    pbProgress.setVisibility(View.GONE);
                }

                //FAILURE
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false);
                    //Remove progressbar after network call completion
                    pbProgress.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false);
                    //Remove progressbar after network call completion
                    pbProgress.setVisibility(View.GONE);
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

    @Override
    public void postTweet(String tweetStr) {
         client.postTweet(tweetStr, new JsonHttpResponseHandler(){
         //SUCCESS
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

         //deserialize JSON, create models, load the data into listview
         try {
             Tweet postedTweet = Tweet.fromJSON(response);
             tweets.add(0, postedTweet);
             aTweets.notifyItemInserted(0);
             scrolltobegin = true;
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

    @Override
    public void onResume() {
        super.onResume();
        if (scrolltobegin) {
            rvTweets.scrollToPosition(0);
            scrolltobegin = false;
        }
    }

}
