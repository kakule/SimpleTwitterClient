package com.codepath.apps.simpletwitterclient.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created on 10/28/2016.
 */
@Table(database = TweetDatabase.class)
@Parcel(analyze={User.class})   // add Parceler annotation here
public class User extends BaseModel{
    //list attributes
    @Column
    String name;
    @PrimaryKey
    @Column
    long uid;
    @Column
    String screenName;
    @Column
    String profileImageUrl;
    @Column
    String tagLine;

    @Column
    int followersCount;

    @Column
    int friendsCount;


    public User() {
        super();
    }
    //deserialize the user json
    public static User fromJSON(JSONObject json) {
        User u = new User();
        //Extract and fill the values
        try {
            u.name = json.getString("name");
            u.uid = json.getLong("id");
            u.screenName = json.getString("screen_name");
            u.profileImageUrl = json.getString("profile_image_url");
            u.tagLine = json.getString("description");
            u.followersCount = json.getInt("followers_count");
            u.friendsCount = json.getInt("friends_count");
            //save to db
            u.save();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //return the user
        return u;
    }

    public long getUser () {
        return uid;
    }

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getTagLine() {
        return tagLine;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public void setFriendsCount(int followingsCount) {
        this.friendsCount = followingsCount;
    }
}
