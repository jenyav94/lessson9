package com.csc.jv.weather.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.csc.jv.weather.BuildConfig;

public class WeatherContentProvider extends ContentProvider {

    private final String LOG_TAG = "myLogs";

    private static final String CONTENT_PATH = "entries";

    public static final String AUTHORITY = "com.csc.jv.weather.contentprovider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri ENTRIES_URI = Uri.withAppendedPath(WeatherContentProvider.CONTENT_URI, CONTENT_PATH);

    public static final int ENTRIES = 1;
    public static final int ENTRIES_ID = 2;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, CONTENT_PATH, ENTRIES);
        uriMatcher.addURI(AUTHORITY, CONTENT_PATH + "/#", ENTRIES_ID);
    }

    private ForecastDatabaseHelper databaseHelper;


    public WeatherContentProvider() {
        databaseHelper = new ForecastDatabaseHelper(getContext());
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "delete, " + uri.toString());
        }
        switch (uriMatcher.match(uri)) {
            case ENTRIES:
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "URI_CONTACTS");
                }
                break;
            case ENTRIES_ID:
                String id = uri.getLastPathSegment();

                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "URI_CONTACTS_ID, " + id);
                }

                if (TextUtils.isEmpty(selection)) {
                    selection = ForecastTable._ID + " = " + id;
                } else {
                    selection = selection + " AND " + ForecastTable._ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int cnt = db.delete(ForecastTable.TABLE_NAME, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);

        return cnt;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "insert, " + uri.toString());
        }

        if (uriMatcher.match(uri) != ENTRIES)
            throw new IllegalArgumentException("Wrong URI: " + uri);

        long rowID = databaseHelper.getWritableDatabase().insert(ForecastTable.TABLE_NAME, null, values);
        Uri resultUri = ContentUris.withAppendedId(uri, rowID);
        getContext().getContentResolver().notifyChange(resultUri, null);

        return resultUri;
    }

    @Override
    public boolean onCreate() {
        databaseHelper = new ForecastDatabaseHelper(getContext());

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "query, " + uri.toString());
        }

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(ForecastTable.TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case ENTRIES:

                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ENTRIES");
                }

//                if (TextUtils.isEmpty(sortOrder)) {
//                    sortOrder = ForecastTable.COLUMN_DONE + " , "
//                            + ForecastTable.COLUMN_DATE + " ASC , "
//                            + ForecastTable.COLUMN_PRIOR + " DESC";
//                }

                break;
            case ENTRIES_ID:
                String id = uri.getLastPathSegment();

                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "URI_CONTACTS_ID, " + id);
                }

                if (TextUtils.isEmpty(selection)) {
                    selection = ForecastTable._ID + " = " + id;
                } else {
                    selection = selection + " AND " + ForecastTable._ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "update, " + uri.toString());
        }

        switch (uriMatcher.match(uri)) {
            case ENTRIES:
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "URI_CONTACTS");
                }
                break;
            case ENTRIES_ID:
                String id = uri.getLastPathSegment();

                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "URI_CONTACTS_ID, " + id);
                }

                if (TextUtils.isEmpty(selection)) {
                    selection = ForecastTable._ID + " = " + id;
                } else {
                    selection = selection + " AND " + ForecastTable._ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int cnt = db.update(ForecastTable.TABLE_NAME, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);

        return cnt;
    }

}
