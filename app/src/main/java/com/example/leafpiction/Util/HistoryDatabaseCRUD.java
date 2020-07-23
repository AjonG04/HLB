package com.example.leafpiction.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.leafpiction.Model.DataModel;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.example.leafpiction.Util.DatabaseTable.HISTORY_ANTHOCYANIN;
import static com.example.leafpiction.Util.DatabaseTable.HISTORY_CAROTENOID;
import static com.example.leafpiction.Util.DatabaseTable.HISTORY_CLOROPHYLL;
import static com.example.leafpiction.Util.DatabaseTable.HISTORY_DATETIME;
import static com.example.leafpiction.Util.DatabaseTable.HISTORY_FILENAME;
import static com.example.leafpiction.Util.DatabaseTable.HISTORY_ID;
import static com.example.leafpiction.Util.DatabaseTable.HISTORY_PHOTO;
import static com.example.leafpiction.Util.DatabaseTable.HISTORY_UPLOADED;

public class HistoryDatabaseCRUD {

    public void addRecord(Context context, DataModel dataModel) {
        // Create and/or open the database for writing
        SQLiteDatabase db = HistoryDatabaseHelper.getInstance(context).getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            // The user might already exist in the database (i.e. the same user created multiple posts).
            ContentValues values = new ContentValues();
//            values.put(HISTORY_ID, dataModel.getId());
            values.put(HISTORY_PHOTO, dataModel.getPhoto());
            values.put(HISTORY_CLOROPHYLL, dataModel.getChlorophyll());
            values.put(HISTORY_CAROTENOID, dataModel.getCarotenoid());
            values.put(HISTORY_ANTHOCYANIN, dataModel.getAnthocyanin());
            values.put(HISTORY_DATETIME, dataModel.getDatetime());
            values.put(HISTORY_FILENAME, dataModel.getFilename());
            values.put(HISTORY_UPLOADED, dataModel.getUploaded());

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insert(DatabaseTable.TABLE_HISTORY, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
    }

    public long addOrUpdateRecord(Context context, DataModel dataModel) {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = HistoryDatabaseHelper.getInstance(context).getWritableDatabase();
        long id = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(HISTORY_PHOTO, dataModel.getPhoto());
            values.put(HISTORY_CLOROPHYLL, dataModel.getChlorophyll());
            values.put(HISTORY_CAROTENOID, dataModel.getCarotenoid());
            values.put(HISTORY_ANTHOCYANIN, dataModel.getAnthocyanin());
            values.put(HISTORY_DATETIME, dataModel.getDatetime());
            values.put(HISTORY_FILENAME, dataModel.getFilename());
            values.put(HISTORY_UPLOADED, dataModel.getUploaded());

            // First try to update the user in case the user already exists in the database
            // This assumes userNames are unique
//            int rows = db.update(DatabaseTable.TABLE_USERDATA, values, USERDATA_USERNAME + "= ?", new String[]{userModel.getUsername()});

            int rows = db.update(DatabaseTable.TABLE_HISTORY, values, HISTORY_ID + "= ?", new String[]{String.valueOf(dataModel.getId())});

            // Check if update succeeded
            if (rows == 1) {
                // Get the primary key of the user we just updated
                String usersSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?",
                        HISTORY_ID, DatabaseTable.TABLE_HISTORY, HISTORY_ID);
                Cursor cursor = db.rawQuery(usersSelectQuery, new String[]{String.valueOf(dataModel.getId())});
                try {
                    if (cursor.moveToFirst()) {
                        id = cursor.getInt(0);
                        db.setTransactionSuccessful();
                    }
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }
            } else {
                // user with this userName did not already exist, so insert new user
                id = db.insertOrThrow(DatabaseTable.TABLE_HISTORY, null, values);
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add or update user");
        } finally {
            db.endTransaction();
        }
        return id;
    }

    public List<DataModel> getAllRecords(Context context) {
        List<DataModel> users = new ArrayList<>();

        // SELECT * FROM POSTS
        // LEFT OUTER JOIN USERS
        // ON POSTS.KEY_POST_USER_ID_FK = USERS.KEY_USER_ID
        String POSTS_SELECT_QUERY =
                String.format("SELECT * FROM %s ",
                        DatabaseTable.TABLE_HISTORY );

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = HistoryDatabaseHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(POSTS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    DataModel newUser = new DataModel();
//                    newUser.userName = cursor.getString(cursor.getColumnIndex(KEY_USER_NAME));
//                    newUser.profilePictureUrl = cursor.getString(cursor.getColumnIndex(KEY_USER_PROFILE_PICTURE_URL));

                    newUser.setId(cursor.getInt(cursor.getColumnIndex(HISTORY_ID)));
                    newUser.setPhoto(cursor.getBlob(cursor.getColumnIndex(HISTORY_PHOTO)));
                    newUser.setChlorophyll(cursor.getFloat(cursor.getColumnIndex(HISTORY_CLOROPHYLL)));
                    newUser.setCarotenoid(cursor.getFloat(cursor.getColumnIndex(HISTORY_CAROTENOID)));
                    newUser.setAnthocyanin(cursor.getFloat(cursor.getColumnIndex(HISTORY_ANTHOCYANIN)));
                    newUser.setDatetime(cursor.getString(cursor.getColumnIndex(HISTORY_DATETIME)));
                    newUser.setFilename(cursor.getString(cursor.getColumnIndex(HISTORY_FILENAME)));
                    newUser.setUploaded(cursor.getInt(cursor.getColumnIndex(HISTORY_UPLOADED)));

                    users.add(newUser);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return users;
    }

//    public int updateUserProfilePicture(Context context, DataModel dataModel) {
//        SQLiteDatabase db = HistoryDatabaseHelper.getInstance(context).getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(USERDATA_PHOTO, dataModel.getPhoto());
//
//        // Updating profile picture url for user with that userName
//        return db.update(DatabaseTable.TABLE_HISTORY, values, USERDATA_USERNAME + " = ?",
//                new String[] { String.valueOf(dataModel.getUsername()) });
//    }

//    public void deleteAllRecords(Context context) {
//        SQLiteDatabase db = HistoryDatabaseHelper.getInstance(context).getWritableDatabase();
//        db.beginTransaction();
//        try {
//            // Order of deletions is important when foreign key relationships exist.
//            db.delete(DatabaseTable.TABLE_HISTORY, null, null);
//            db.setTransactionSuccessful();
//        } catch (Exception e) {
//            Log.d(TAG, "Error while trying to delete all posts and users");
//        } finally {
//            db.endTransaction();
//        }
//    }

}
