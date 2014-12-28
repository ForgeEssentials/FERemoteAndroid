package com.android305.forgeessentialsremote.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLDatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_SERVERS = "servers";
    public static final String COLUMN_SERVERS_ID = "_id";
    public static final String COLUMN_SERVERS_NAME = "name";
    public static final String COLUMN_SERVERS_IP = "ip";
    public static final String COLUMN_SERVERS_PORT = "port";
    public static final String COLUMN_SERVERS_SSL = "ssl";
    public static final String COLUMN_SERVERS_USERNAME = "username";
    public static final String COLUMN_SERVERS_UUID = "uuid";
    public static final String COLUMN_SERVERS_TOKEN = "token";
    public static final String COLUMN_SERVERS_AUTO_CONNECT = "auto_connect";
    public static final String COLUMN_SERVERS_TIMEOUT = "timeout";
    public static final String COLUMN_SERVERS_CONNECTED = "connected";

    // Database creation sql statements
    //@formatter:off
    private static final String DATABASE_CREATE = "create table "
            + TABLE_SERVERS + "(" + COLUMN_SERVERS_ID
            + " integer primary key autoincrement, "
            + COLUMN_SERVERS_NAME + " text not null, "
            + COLUMN_SERVERS_IP + " text not null, "
            + COLUMN_SERVERS_PORT + " integer not null, "
            + COLUMN_SERVERS_SSL + " integer not null, "
            + COLUMN_SERVERS_USERNAME + " text, "
            + COLUMN_SERVERS_UUID + " text, "
            + COLUMN_SERVERS_TOKEN + " text not null, "
            + COLUMN_SERVERS_AUTO_CONNECT + " integer not null, "
            + COLUMN_SERVERS_TIMEOUT + " integer not null, "
            + COLUMN_SERVERS_CONNECTED + " integer not null"
            + ");";
    //@formatter:on

    private static final String DATABASE_NAME = "feremote.db";
    private static final int DATABASE_VERSION = 10;

    public SQLDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLDatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVERS);
        onCreate(db);
    }

} 