package com.codepath.apps.simpletwitterclient.clients;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import java.io.IOException;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "v4FsBZirC6Y4lRot9PsRTu8Ku";       // Change this
	public static final String REST_CONSUMER_SECRET = "lF8iOJrxomHHy5C38QbhrPraJ62epaiZwsiJ4dmS5w70v38aWe"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://cpsimpletwitterclient"; // Change this (here and in manifest)

    public static final int REQUEST_NUM_TWEETS = 25;

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

    public void getHomeTimeline (long since_id,
                                 long max_id,
                                 AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", REQUEST_NUM_TWEETS);
        if (max_id != 0) {
            params.put("max_id", max_id - 1);
        }
        if (since_id != 0) {
            params.put("since_id", since_id);
        }
        //Execute the request
        getClient().get(apiUrl, params, handler);
    }

    public void getMentionsTimeline(long since_id,
                                    long max_id,
                                    AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", REQUEST_NUM_TWEETS);
        //Execute the request
        getClient().get(apiUrl, params, handler);
    }

    public void getUsersTimeline(long since_id,
                                 long max_id,
                                 String screenName,
                                 AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        params.put("screen_name", screenName);
        params.put("count", REQUEST_NUM_TWEETS);
        if (max_id != 0) {
            params.put("max_id", max_id - 1);
        }
        if (since_id != 0) {
            params.put("since_id", since_id);
        }
        //Execute the request
        getClient().get(apiUrl, params, handler);
    }

    public void getAuthorisedUser(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("account/verify_credentials.json");

        getClient().get(apiUrl, handler);
    }


    public void postStatusFavorite (boolean status, long Id, AsyncHttpResponseHandler handler) {
        String apiUrl;
        if (status) {
            apiUrl = getApiUrl("favorites/create.json");
        } else {
            apiUrl = getApiUrl("favorites/destroy.json");
        }

        RequestParams params = new RequestParams();
        params.put("id", Id);

        //Execute the request
        getClient().post(apiUrl, params, handler);
    }
    public void postTweet(String status, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", status);

        //Execute the request
        getClient().post(apiUrl, params, handler);
    }

    public void postRetweet (long Id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/retweet/" + Long.toString(Id) + ".json");
        RequestParams params = new RequestParams();
        params.put("trim_user", 1);

        //Execute the request
        getClient().post(apiUrl, params, handler);
    }

    //Internet related checks
    public boolean isInternetAvailable() {

        if (isNetworkAvailable() && isOnline()) {
            return true;
        }
        return informNoInternet();
    }


    private boolean informNoInternet () {
        Toast.makeText(context, "No Internet Connection Available", Toast.LENGTH_SHORT).show();
        return false;
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
    //COMPOSE TWEET

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}
