package com.codepath.apps.simpletwitterclient.models;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created on 10/31/2016.
 */

@Database(name = DraftDatabase.NAME, version = DraftDatabase.VERSION)
public class DraftDatabase {
    public static final String NAME = "DraftDatabase";

    public static final int VERSION = 1;
}
