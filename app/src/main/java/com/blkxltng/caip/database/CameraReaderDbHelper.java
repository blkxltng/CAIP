package com.blkxltng.caip.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.blkxltng.caip.CameraInfo;
import com.blkxltng.caip.database.CameraReaderContract.CameraEntry;

import java.util.ArrayList;
import java.util.List;

public class CameraReaderDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "CameraReaderDbHelper";

    //The Android's default system path of your application database.
    public static String DATABASE_PATH = "/data/data/com.blkxltng.caip/databases/";

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "cameraList.db";

    private SQLiteDatabase mDatabase;

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + CameraEntry.TABLE_NAME + " (" +
                    CameraEntry._ID + " INTEGER PRIMARY KEY," +
                    CameraEntry.COLUMN_NAME_TITLE + " TEXT," +
                    CameraEntry.COLUMN_NAME_IPADDRESS + " TEXT," +
                    CameraEntry.COLUMN_NAME_RTSP + " TEXT," +
                    CameraEntry.COLUMN_NAME_HTTP + " TEXT," +
                    CameraEntry.COLUMN_NAME_USERNAME + " TEXT," +
                    CameraEntry.COLUMN_NAME_PASSWORD + " TEXT," +
                    CameraEntry.COLUMN_NAME_URL + " TEXT," +
                    CameraEntry.COLUMN_NAME_INFO + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + CameraEntry.TABLE_NAME;

    public CameraReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
//        db.execSQL(SQL_DELETE_ENTRIES);
//        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        onUpgrade(db, oldVersion, newVersion);
    }

    public void addCamera(CameraInfo cameraInfo) {

        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(CameraEntry.COLUMN_NAME_TITLE, cameraInfo.getName());
        values.put(CameraEntry.COLUMN_NAME_IPADDRESS, cameraInfo.getIpAddress());
        values.put(CameraEntry.COLUMN_NAME_RTSP, cameraInfo.getRtspPort());
        values.put(CameraEntry.COLUMN_NAME_HTTP, cameraInfo.getHttpPort());
        values.put(CameraEntry.COLUMN_NAME_USERNAME, cameraInfo.getUsername());
        values.put(CameraEntry.COLUMN_NAME_PASSWORD, cameraInfo.getPassword());
        values.put(CameraEntry.COLUMN_NAME_URL, cameraInfo.getUrl());
        values.put(CameraEntry.COLUMN_NAME_INFO, cameraInfo.getDeviceInfo());

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(CameraEntry.TABLE_NAME, null, values);
    }

    public void deleteCamera(CameraInfo cameraInfo) {

        SQLiteDatabase db = this.getWritableDatabase();

        // Define 'where' part of query.
        String selection = CameraEntry.COLUMN_NAME_TITLE + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { cameraInfo.getName() };
        // Issue SQL statement.
        int deletedRows = db.delete(CameraEntry.TABLE_NAME, selection, selectionArgs);
    }

    public List<CameraInfo> getAllCameras() {
        List<CameraInfo> cameraInfoList = new ArrayList<CameraInfo>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                CameraEntry.COLUMN_NAME_TITLE,
                CameraEntry.COLUMN_NAME_IPADDRESS,
                CameraEntry.COLUMN_NAME_RTSP,
                CameraEntry.COLUMN_NAME_HTTP,
                CameraEntry.COLUMN_NAME_USERNAME,
                CameraEntry.COLUMN_NAME_PASSWORD,
                CameraEntry.COLUMN_NAME_URL,
                CameraEntry.COLUMN_NAME_INFO
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = CameraEntry.COLUMN_NAME_TITLE + " = ?";
        String[] selectionArgs = { "My Title" };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = CameraEntry.COLUMN_NAME_TITLE + " DESC";

        CameraInfo cameraInfo = null;

        try {

            Cursor cursor = db.query(
                    CameraEntry.TABLE_NAME,   // The table to query
                    null,             // The array of columns to return (pass null to get all)
                    null,              // The columns for the WHERE clause
                    null,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    sortOrder               // The sort order
            );

            if(cursor == null) return null;

            cursor.moveToFirst();

            do {
                cameraInfo = new CameraInfo();
                cameraInfo.setId(cursor.getInt(cursor.getColumnIndex(CameraEntry._ID)));
                cameraInfo.setName(cursor.getString(cursor.getColumnIndex(CameraEntry.COLUMN_NAME_TITLE)));
                cameraInfo.setIpAddress(cursor.getString(cursor.getColumnIndex(CameraEntry.COLUMN_NAME_IPADDRESS)));
                cameraInfo.setRtspPort(cursor.getString(cursor.getColumnIndex(CameraEntry.COLUMN_NAME_RTSP)));
                cameraInfo.setHttpPort(cursor.getString(cursor.getColumnIndex(CameraEntry.COLUMN_NAME_HTTP)));
                cameraInfo.setUsername(cursor.getString(cursor.getColumnIndex(CameraEntry.COLUMN_NAME_USERNAME)));
                cameraInfo.setPassword(cursor.getString(cursor.getColumnIndex(CameraEntry.COLUMN_NAME_PASSWORD)));
                cameraInfo.setUrl(cursor.getString(cursor.getColumnIndex(CameraEntry.COLUMN_NAME_URL)));
                cameraInfo.setDeviceInfo(cursor.getString(cursor.getColumnIndex(CameraEntry.COLUMN_NAME_INFO)));
                cameraInfoList.add(cameraInfo);
            } while (cursor.moveToNext());
            cursor.close();
        } catch (Exception e) {
            Log.e("blkxltng", e.getMessage());
        }

        return cameraInfoList;
    }

    public boolean checkForCamera(CameraInfo cameraInfo) {
        SQLiteDatabase db = this.getReadableDatabase();
//        String query = "SELECT * FROM " + CameraEntry.TABLE_NAME + " WHERE " + dbfield + " = " + fieldValue;
//        Cursor cursor = db.rawQuery(query, null);

//        if(cameraInfo.getRtspPort() == "") {
//            cameraInfo.setRtspPort(null);
//        }
//        if(cameraInfo.getHttpPort() == "") {
//            cameraInfo.setHttpPort(null);
//        }

        // Filter results WHERE "title" = 'My Title'
        String selection = CameraEntry.COLUMN_NAME_IPADDRESS + " = ?";
        String[] selectionArgs;
        if(cameraInfo.getRtspPort() != null) {
            selection += "AND (" + CameraEntry.COLUMN_NAME_RTSP + " = ?)";
            selectionArgs = new String[] {cameraInfo.getIpAddress(), cameraInfo.getRtspPort()};
        } else if(cameraInfo.getHttpPort() != null) {
            selection += "AND (" + CameraEntry.COLUMN_NAME_HTTP + " = ?)";
            selectionArgs = new String[] {cameraInfo.getIpAddress(), cameraInfo.getHttpPort()};
        } else {
            selectionArgs = new String[] {cameraInfo.getIpAddress()};
        }

//        String[] selectionArgs = {cameraInfo.getIpAddress()};

        Log.d(TAG, "checkForCamera: IP=" + cameraInfo.getIpAddress() + ", RTSP=" + cameraInfo.getRtspPort() + ", HTTP=" + cameraInfo.getHttpPort());
        Log.d(TAG, "checkForCamera: selection=" + selection + ", selectionArgs=" + selectionArgs);

        Cursor cursor = db.query(
                CameraEntry.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
}
