package com.example.leafpiction.Util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class HistoryDatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "HLBScan";
    private static final int DATABASE_VERSION = 2;
    private static HistoryDatabaseHelper sInstance;

    public static synchronized HistoryDatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        if (sInstance == null) {
            sInstance = new HistoryDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public HistoryDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERDATA_TABLE = "CREATE TABLE " + DatabaseTable.TABLE_HISTORY + "("
                + DatabaseTable.HISTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + DatabaseTable.HISTORY_PHOTO + " BLOB,"
                + DatabaseTable.HISTORY_STATUS + " VARCHAR(12),"
                + DatabaseTable.HISTORY_CONFIDENCE + " VARCHAR(12),"
                + DatabaseTable.HISTORY_DATETIME + " VARCHAR(128),"
                + DatabaseTable.HISTORY_FILENAME + " VARCHAR(255),"
                + DatabaseTable.HISTORY_UPLOADED + " INT(1)" + ")";

        db.execSQL(CREATE_USERDATA_TABLE);

    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + DatabaseTable.TABLE_HISTORY);
            onCreate(db);
        }
    }
}
