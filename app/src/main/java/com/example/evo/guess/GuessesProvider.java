package com.example.evo.guess;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class GuessesProvider extends ContentProvider{

    private static final String AUTHORITY = "com.example.evo.guess.GuessesProvider";
    private static final String BASE_PATH = "guessees";
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH );

    // Constant to identify the requested operation
    private static final int GUESS = 1;
    private static final int GUESS_ID = 2;

    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    public static final String CONTENT_ITEM_TYPE = "Guess";

    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, GUESS);
        uriMatcher.addURI(AUTHORITY, BASE_PATH +  "/#", GUESS_ID);
    }

    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {

        DBOpenHelper helper = new DBOpenHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        if (uriMatcher.match(uri) == GUESS_ID) {
            selection = DBOpenHelper.GUESS_ID + "=" + uri.getLastPathSegment();
        }

        return database.query(DBOpenHelper.TABLE_GUESSES, DBOpenHelper.ALL_COLUMNS,
                selection, null, null, null,
                DBOpenHelper.GUESS_CREATED + " DESC");
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = database.insert(DBOpenHelper.TABLE_GUESSES,
                null, values);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return database.delete(DBOpenHelper.TABLE_GUESSES, selection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return database.update(DBOpenHelper.TABLE_GUESSES,
                values, selection, selectionArgs);
    }
}
