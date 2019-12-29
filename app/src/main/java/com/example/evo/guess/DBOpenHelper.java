package com.example.evo.guess;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper{

    //Constants for db name and version
    private static final String DATABASE_NAME = "guess.db";
    private static final int DATABASE_VERSION = 2;

    //Constants for identifying guess table and columns
    public static final String TABLE_GUESSES = "guess";
    public static final String GUESS_ID = "_id";
    public static final String GUESS_TEXT = "guessWordText";
    public static final String GUESS_CREATED = "guessWordCreated";

    public static final String[] ALL_COLUMNS =
            {GUESS_ID, GUESS_TEXT, GUESS_CREATED};

    //SQL to create table
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_GUESSES + " (" +
                    GUESS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    GUESS_TEXT + " TEXT, " +
                    GUESS_CREATED + " TEXT default CURRENT_TIMESTAMP" +
                    ")";



    //Constants for identifying guess table and columns
    public static final String TABLE_SCORES = "score";
    public static final String SCORE_ID = "_id";
    public static final String SCORE = "score";
    public static final String SCORE_CREATED = "scoreCreated";

    public static final String[] ALL_COLUMNS1 =
            {SCORE_ID, SCORE, SCORE_CREATED};

    //SQL to create table
    private static final String TABLE_CREATE_SCORE =
            "CREATE TABLE " + TABLE_SCORES + " (" +
                    SCORE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SCORE + " INT, " +
                    SCORE_CREATED + " TEXT default CURRENT_TIMESTAMP" +
                    ")";
    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        db.execSQL(TABLE_CREATE_SCORE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GUESSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);
        onCreate(db);
    }
}
