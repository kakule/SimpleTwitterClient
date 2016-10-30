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
    private String name;
    @PrimaryKey
    @Column
    private long uid;
    @Column
    private String screenName;
    @Column
    private String profileImageUrl;

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
}
