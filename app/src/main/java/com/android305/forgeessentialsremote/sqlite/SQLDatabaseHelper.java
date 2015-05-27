package com.android305.forgeessentialsremote.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLDatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_SERVERS = "servers";
    public static final String SERVERS_ID = "_id";
    public static final String SERVERS_NAME = "name";
    public static final String SERVERS_IP = "ip";
    public static final String SERVERS_PORT = "port";
    public static final String SERVERS_SSL = "ssl";
    public static final String SERVERS_USERNAME = "username";
    public static final String SERVERS_UUID = "uuid";
    public static final String SERVERS_TOKEN = "token";
    public static final String SERVERS_AUTO_CONNECT = "auto_connect";
    public static final String SERVERS_TIMEOUT = "timeout";

    public static final String TABLE_CHAT_LOG = "chat_log";
    public static final String CHAT_LOG_ID = "_id";
    public static final String CHAT_LOG_TIMESTAMP = "timestamp";
    public static final String CHAT_LOG_SERVERS_ID = "server_id";
    public static final String CHAT_LOG_SENDER_UUID = "uuid";
    public static final String CHAT_LOG_SENDER = "sender";
    public static final String CHAT_LOG_MESSAGE = "message";

    // Database creation sql statements
    //@formatter:off
    private static final String DATABASE_CREATE_SERVERS = "create table "
            + TABLE_SERVERS + "(" + SERVERS_ID
            + " integer primary key autoincrement, "
            + SERVERS_NAME + " text not null, "
            + SERVERS_IP + " text not null, "
            + SERVERS_PORT + " integer not null, "
            + SERVERS_SSL + " integer not null, "
            + SERVERS_USERNAME + " text, "
            + SERVERS_UUID + " text, "
            + SERVERS_TOKEN + " text not null, "
            + SERVERS_AUTO_CONNECT + " integer not null, "
            + SERVERS_TIMEOUT + " integer not null "
            + ");";
    private static final String DATABASE_CREATE_CHAT_LOG = "create table "
            + TABLE_CHAT_LOG + "(" + CHAT_LOG_ID
            + " integer primary key autoincrement, "
            + CHAT_LOG_TIMESTAMP + " datetime default CURRENT_TIMESTAMP, "
            + CHAT_LOG_SERVERS_ID + " int not null, "
            + CHAT_LOG_SENDER_UUID + " text, "
            + CHAT_LOG_SENDER + " text, "
            + CHAT_LOG_MESSAGE + " text);";
    //@formatter:on

    private static final String DATABASE_NAME = "feremote.db";
    private static final int DATABASE_VERSION = 2;

    public SQLDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_SERVERS);
        database.execSQL(DATABASE_CREATE_CHAT_LOG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLDatabaseHelper.class.getName(), "Upgrading database from version " + oldVersion
                + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT_LOG);
        onCreate(db);
    }

} 