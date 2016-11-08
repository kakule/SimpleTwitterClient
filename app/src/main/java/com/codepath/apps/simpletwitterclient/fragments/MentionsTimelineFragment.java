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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created on 11/5/2016.
 */

public class MentionsTimelineFragment extends TweetListFragment {

    private TwitterClient client;
    boolean loading = false;
    boolean lastenryReached = false;
    ProgressBar pbProgress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = TwitterApplication.getRestClient(); //singleton client
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        //get the progress bar
        pbProgress = (ProgressBar) getActivity().findViewById(R.id.toolbar)
                .findViewById(R.id.pbProgressAction);

		populateTimeline(1, 0);
    }
    //Send an API request to get the timeline json
    //Fill the view by creating the tweet object from the json
    private void populateTimeline(final long since_id, long max_id) {
        loading = false;
        if (client.isInternetAvailable()) {
            loading = true;
            pbProgress.setVisibility(ProgressBar.VISIBLE);
        	client.getMentionsTimeline(since_id, max_id, new JsonHttpResponseHandler() {
            	//SUCCESS
            	@Override
            	public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                	Log.d("DEBUG", response.toString());
                    ArrayList <Tweet> returnedTweets = Tweet.fromJSONArray(response);
                    //If returned is less than the requested number, then we assume
                    //we've reached end of list
                    if (returnedTweets.size() < TwitterClient.REQUEST_NUM_TWEETS - 1) {
                        lastenryReached = true;
                    } else {
                        lastenryReached = false;
                    }

                	if (since_id == 1) {
                    	addAll(returnedTweets, true);
                	} else {
                    	addAll(returnedTweets, false);
                	}
                	// Now we call setRefreshing(false) to signal refresh has finished
                	swipeContainer.setRefreshing(false);
                	//Remove progressbar after network call completion
                	pbProgress.setVisibility(View.GONE);
                	//we are not loading anymore
                	loading = false;
            	}

            	//FAILURE
            	@Override
            	public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                	Log.d("DEBUG", errorResponse.toString());
                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false);
                    //Remove progressbar after network call completion
                    pbProgress.setVisibility(View.GONE);
                    //we are not loading anymore
                    loading = false;
            	}

            	@Override
            	public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                	Log.d("DEBUG", errorResponse.toString());
                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false);
                    //Remove progressbar after network call completion
                    pbProgress.setVisibility(View.GONE);
                    //we are not loading anymore
                    loading = false;
            	}
        	});
        }
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
        if (!loading && !lastenryReached) {
            Log.d("DEBUG", "offset is " + offset);
            long id = tweets.get(tweets.size() - 1).getUid();
        	Log.d("DEBUG", "LOADING Mentions Timeline tweets size is " + tweets.size() + " and uid of last is " + id);
            //populateTimeline((offset * TwitterClient.NUM_TWEETS_LOADED) + 1);
            populateTimeline(0, id);
        } else {
            Log.d("DEBUG", "NOT LOADING Mentions Timeline tweets size is " + tweets.size());
        }
        return loading;
    }
    @Override
    public void postTweet(String tweetStr) {
         if (client.isInternetAvailable()) {
             client.postTweet(tweetStr, new JsonHttpResponseHandler() {
                 //SUCCESS
                 @Override
                 public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                     //deserialize JSON, create models, load the data into listview
                     try {
                         Tweet postedTweet = Tweet.fromJSON(response);
                         tweets.add(0, postedTweet);
                         aTweets.notifyItemInserted(0);
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

    }


    @Override
    public void postTweetFavorite(Tweet tweet, final int position) {
        if (client.isInternetAvailable()) {
            client.postStatusFavorite(!tweet.isFavourited(), tweet.getUid(),
                    new JsonHttpResponseHandler () {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            //onSuccess update tweet
                            try {
                                Tweet postedTweet = Tweet.fromJSON(response);
                                tweets.set(position, postedTweet);
                                aTweets.notifyItemChanged(position);
                                Log.d("DEBUG", response.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        //FAILURE
                        @Override
                        public void onFailure(int statusCode, Header[] headers,
                                              Throwable throwable, JSONObject errorResponse) {
                            Log.d("DEBUG", errorResponse.toString());
                        }
                    }
            );
        }
    }

    @Override
    public void postRetweet (Long Id) {
        if (client.isInternetAvailable()) {
            client.postRetweet(Id, new JsonHttpResponseHandler () {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            //onSuccess update tweet
                            populateTimeline(1, 0);
                        }
                        //FAILURE
                        @Override
                        public void onFailure(int statusCode, Header[] headers,
                                              Throwable throwable, JSONObject errorResponse) {
                            Log.d("DEBUG", errorResponse.toString());
                        }
                    }
            );
        }
    }

}
