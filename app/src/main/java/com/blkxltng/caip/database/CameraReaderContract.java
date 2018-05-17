package com.blkxltng.caip.database;

import android.provider.BaseColumns;

public final class CameraReaderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private CameraReaderContract() {
    }

    /* Inner class that defines the table contents */
    public static class CameraEntry implements BaseColumns {
        public static final String TABLE_NAME = "camera_list";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_IPADDRESS = "ipAddress";
        public static final String COLUMN_NAME_RTSP = "rtsp";
        public static final String COLUMN_NAME_HTTP = "http";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_PASSWORD = "password";
    }
}
