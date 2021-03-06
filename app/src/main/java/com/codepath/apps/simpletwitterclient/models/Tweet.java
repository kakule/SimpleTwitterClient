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
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**

 */
//parse JSON and store data, encapsulate state logic or display logic
@Table(database = TweetDatabase.class)
@Parcel(analyze={Tweet.class})   // add Parceler annotation here
public class Tweet extends BaseModel{
    //list of attributes
    @PrimaryKey
    @Column
    long uid; // unique id for tweet
    @Column
    String createdAt;
    @Column
    int retweetCount;
    //@Column
    //int favoritesCount;
    @Column
    boolean favourited;

    @Column
    boolean retweeted;
    @Column
    String body;
    @Column
    @ForeignKey(saveForeignKeyModel = false)
    User user; //store embedded user object
    @Column
    @ForeignKey(saveForeignKeyModel = false)
    TweetMedia media;

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
            tweet.retweetCount = jsonobject.getInt("retweet_count");
            //tweet.favoritesCount = jsonobject.getInt("favourites_count");
            tweet.retweeted = jsonobject.getBoolean("retweeted");
            tweet.favourited = jsonobject.getBoolean("favorited");
            tweet.user = User.fromJSON(jsonobject.getJSONObject("user"));

            tweet.media = TweetMedia.fromJSON(jsonobject);
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

    public TweetMedia getMedia() {
        return media;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public boolean isFavourited() {
        return favourited;
    }

    public void setFavourited(boolean favourited) {
        this.favourited = favourited;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    //public int getFavoritesCount() {
    //    return favoritesCount;
    //}

    void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }

    //void setFavoritesCount(int favoritesCount) {
    //    this.favoritesCount = favoritesCount;
    //}
    void setUid(long uid) {
        this.uid = uid;
    }

    void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    void setBody(String body) {
        this.body = body;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setTweetMedia(TweetMedia media) {
        this.media = media;
    }

    // Record Finders
    public static Tweet byId(long id) {
        return new Select().from(Tweet.class).where(Tweet_Table.uid.eq(id)).querySingle();
    }

    public static List<Tweet> recentItems() {
        return new Select().from(Tweet.class).orderBy(Tweet_Table.uid, false).limit(300).queryList();
    }
}
