package com.dario.tiempo.contentProvider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by dario on 2016-02-07.
 */
public class CityProvider extends ContentProvider {

    private static final String TAG = "==>" + CityProvider.class.getSimpleName();

    @Override
    public boolean onCreate() {
        return false;
    }


    /* You must implement this method to search your suggestion data and return a Cursor pointing
     * to the suggestions you deem relevant
     *
     * When the user starts typing into the search dialog or search widget, the system queries your
     * content provider for suggestions by calling query() each time a letter is typed
     * */
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
