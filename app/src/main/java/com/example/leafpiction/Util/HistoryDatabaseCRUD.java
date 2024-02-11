package com.example.leafpiction.Util;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.leafpiction.Model.DataModel;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.example.leafpiction.Util.DatabaseTable.HISTORY_CONFIDENCE;
import static com.example.leafpiction.Util.DatabaseTable.HISTORY_STATUS;
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
            values.put(HISTORY_STATUS, dataModel.getStatus());
            values.put(HISTORY_CONFIDENCE, dataModel.getConfidence());
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

    @SuppressLint("Range")
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
                    newUser.setId(cursor.getInt(cursor.getColumnIndex(HISTORY_ID)));
                    newUser.setPhoto(cursor.getBlob(cursor.getColumnIndex(HISTORY_PHOTO)));
                    newUser.setStatus(cursor.getString(cursor.getColumnIndex(HISTORY_STATUS)));
                    newUser.setConfidence(cursor.getString(cursor.getColumnIndex(HISTORY_CONFIDENCE)));
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

    public void deleteRecord(Context context, int id){
        SQLiteDatabase db = HistoryDatabaseHelper.getInstance(context).getWritableDatabase();
        db.beginTransaction();
        try {
            // Order of deletions is important when foreign key relationships exist.
            db.delete(DatabaseTable.TABLE_HISTORY, HISTORY_ID + "= ?", new String[]{String.valueOf(id)});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete record");
        } finally {
            db.endTransaction();
        }
    }

    public void UpdateUploaded(Context context, int id) {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = HistoryDatabaseHelper.getInstance(context).getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(HISTORY_UPLOADED, 1);

            db.update(DatabaseTable.TABLE_HISTORY, values, HISTORY_ID + "= ?", new String[]{String.valueOf(id)});
            db.setTransactionSuccessful();
            Log.d("Updated Record", "Finished update record");
        } catch (Exception e) {
            Log.d("Error Update Record", "Error while trying to update record");
        } finally {
            db.endTransaction();
        }
    }


//    public int getUploaded(Context context, int id) {
//        List<DataModel> users = new ArrayList<>();
//
//        String s = String.valueOf(id);
//
//        String POSTS_SELECT_QUERY =
//                String.format("SELECT * FROM "+ DatabaseTable.TABLE_HISTORY +
//                        " WHERE " + HISTORY_ID + "=" + s );
//
//        SQLiteDatabase db = HistoryDatabaseHelper.getInstance(context).getReadableDatabase();
//        Cursor cursor = db.rawQuery(POSTS_SELECT_QUERY, null);
//        try {
//            if (cursor.moveToFirst()) {
//                do {
//                    DataModel user = new DataModel();
//                    user.setId(cursor.getInt(cursor.getColumnIndex(HISTORY_ID)));
//                    user.setPhoto(cursor.getBlob(cursor.getColumnIndex(HISTORY_PHOTO)));
//                    user.setChlorophyll(cursor.getFloat(cursor.getColumnIndex(HISTORY_CLOROPHYLL)));
//                    user.setCarotenoid(cursor.getFloat(cursor.getColumnIndex(HISTORY_CAROTENOID)));
//                    user.setAnthocyanin(cursor.getFloat(cursor.getColumnIndex(HISTORY_ANTHOCYANIN)));
//                    user.setDatetime(cursor.getString(cursor.getColumnIndex(HISTORY_DATETIME)));
//                    user.setFilename(cursor.getString(cursor.getColumnIndex(HISTORY_FILENAME)));
//                    user.setUploaded(cursor.getInt(cursor.getColumnIndex(HISTORY_UPLOADED)));
//
//                    users.add(user);
//                } while(cursor.moveToNext());
//            }
//        } catch (Exception e) {
//            Log.d(TAG, "Error while trying to get posts from database");
//        } finally {
//            if (cursor != null && !cursor.isClosed()) {
//                cursor.close();
//            }
//        }
//        return users.get(0).getUploaded();
//    }
}
