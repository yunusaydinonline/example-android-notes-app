package com.task.noteapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database extends SQLiteOpenHelper {
    private static final String TAG = "Database";

    private static final String CREATE_NOTE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS " + Constants.TABLE_NAME +
            "(" +
            Constants.COLUMN_ID + " TEXT PRIMARY KEY, " +
            Constants.COLUMN_TITLE + " TEXT NOT NULL, " +
            Constants.COLUMN_CONTENT + " TEXT NOT NULL, " +
            Constants.COLUMN_IMAGE_URL + " TEXT, " +
            Constants.COLUMN_CREATED_AT + " INTEGER NOT NULL DEFAULT -1, " +
            Constants.COLUMN_UPDATED_AT + " INTEGER NOT NULL DEFAULT -1" +
            ")";

    public Database(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
        Log.d(TAG, "created successfully.");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_NOTE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
