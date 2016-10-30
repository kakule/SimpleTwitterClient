package com.codepath.apps.simpletwitterclient.models;

import android.text.format.DateUtils;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**

 */
//parse JSON and store data, encapsulate state logic or display logic
@Table(database = TweetDatabase.class)
public class Tweet extends BaseModel{
    //list of attributes
    @PrimaryKey
    @Column
    private long uid; // unique id for tweet
    @Column
    private String createdAt;
    @Column
    private String body;
    @Column
    @ForeignKey(saveForeignKeyModel = false)
    private User user; //store embedded user object

    public Tweet() {
        super();
    }
    //deserialise JSON and build Tweet objects
    //Tweet.fromJSON("{ .. }"} => <Tweet>
    public static Tweet fromJSON(JSONObject jsonobject) throws JSONException {
        Tweet tweet = new Tweet();

        //Extract the values from the json, store them

        try {
            tweet.body = jsonobject.getString("text");
            tweet.uid = jsonobject.getLong("id");
            tweet.createdAt = jsonobject.getString("created_at");
            tweet.user = User.fromJSON(jsonobject.getJSONObject("user"));
            tweet.save();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Return the tweet object
        return tweet;
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonarray) {
        ArrayList<Tweet> tweets = new ArrayList<>();
        //Iterate the json array and create tweets
        for (int i = 0; i < jsonarray.length(); i++) {
            try {
                JSONObject tweetjson = jsonarray.getJSONObject(i);
                Tweet tweet = Tweet.fromJSON((tweetjson));
                if (tweet != null)
                    tweets.add(tweet);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        //return finished list
        return tweets;
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    //public String getRelativeTimeAgo(String rawJsonDate) {
    public String getRelativeTimeAgo( ) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);
        String rawJsonDate = this.createdAt;
        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Record Finders
    public static Tweet byId(long id) {
        return new Select().from(Tweet.class).where(Tweet_Table.uid.eq(id)).querySingle();
    }

    public static List<Tweet> recentItems() {
        return new Select().from(Tweet.class).orderBy(Tweet_Table.uid, false).limit(300).queryList();
    }
}
