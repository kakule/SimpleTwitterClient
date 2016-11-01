package com.codepath.apps.simpletwitterclient.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created on 10/30/2016.
 */
@Table(database = TweetDatabase.class)
@Parcel(analyze={TweetMedia.class})   // add Parceler annotation here
public class TweetMedia extends BaseModel {

    @PrimaryKey
    @Column
    long id;
    @Column
    String tweetPic;
    @Column
    String tweetvid;

    public TweetMedia () {
        super();
    }
    //deserialize the user json
    public static TweetMedia fromJSON(JSONObject json) {
        TweetMedia m = new TweetMedia();
        JSONObject jsonobject;
        JSONArray media;
        JSONObject jsonobjectextended;
        //Extract and fill the values
        try {
            jsonobject = json.getJSONObject("entities");
            media = jsonobject.getJSONArray("media");
            for (int i = 0; i < media.length(); i++) {
                jsonobject = media.getJSONObject(i);
                m.id = jsonobject.getLong("id");
                m.tweetPic = jsonobject.getString("media_url");
                if (m.tweetPic != null) {
                    break;
                }
                //save to db
                //u.save();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            jsonobjectextended = json.getJSONObject("extended_entities");
            media = jsonobjectextended.getJSONArray("media");
            outerloop:
            for (int i = 0; i < media.length(); i++) {
                jsonobject = media.getJSONObject(i);
                String str = jsonobject.getString("type");
                if (str.equals("video")) {
                    JSONArray vidmedia;
                    jsonobject = jsonobject.getJSONObject("video_info");
                    vidmedia = jsonobject.getJSONArray("variants");
                    for (int j = 0; j < vidmedia.length(); j++) {
                        jsonobjectextended = vidmedia.getJSONObject(j);
                        if (jsonobjectextended.getString("content_type").equals("video/mp4")) {
                            m.tweetvid = jsonobjectextended.getString("url");
                            if (m.tweetvid != null && !m.tweetvid.isEmpty())
                                m.tweetPic = null;
                            break outerloop;
                        }
                    }

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //save to db
        m.save();
        //return the user
        return m;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTweetPic() {
        return tweetPic;
    }

    public void setTweetPic(String tweetPic) {
        this.tweetPic = tweetPic;
    }

    public String getTweetvid() {
        return tweetvid;
    }

    public void setTweetvid(String tweetvid) {
        this.tweetvid = tweetvid;
    }
}
