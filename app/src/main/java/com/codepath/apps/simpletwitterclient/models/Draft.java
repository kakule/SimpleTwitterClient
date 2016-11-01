package com.codepath.apps.simpletwitterclient.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by phoen on 10/31/2016.
 */
@Table(database = DraftDatabase.class)
@Parcel(analyze={Draft.class})   // add Parceler annotation here
public class Draft extends BaseModel {


    @PrimaryKey
    @Column
    String draft; // unique id

    public Draft() {
        super();
    }

    public Draft (String draft) {
        this.draft = draft;
    }

    public String getDraft() {
        return draft;
    }

    public void setDraft(String draft) {
        this.draft = draft;
    }

    public static Draft recentItems() {
        List<Draft> entries = new Select().from(Draft.class)
                .orderBy(Draft_Table.draft, false).limit(5).queryList();
        if (entries.size() > 0) {
            return entries.get(0);
        }
        return null;
    }
}
