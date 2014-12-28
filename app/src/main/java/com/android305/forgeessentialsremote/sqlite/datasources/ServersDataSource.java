package com.android305.forgeessentialsremote.sqlite.datasources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android305.forgeessentialsremote.servers.active.Server;
import com.android305.forgeessentialsremote.sqlite.SQLDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ServersDataSource {

    // Database fields
    private SQLiteDatabase database;
    private SQLDatabaseHelper dbHelper;
    private String[] allColumns = {SQLDatabaseHelper.COLUMN_SERVERS_ID,
            SQLDatabaseHelper.COLUMN_SERVERS_NAME, SQLDatabaseHelper.COLUMN_SERVERS_IP,
            SQLDatabaseHelper.COLUMN_SERVERS_PORT, SQLDatabaseHelper.COLUMN_SERVERS_SSL,
            SQLDatabaseHelper.COLUMN_SERVERS_USERNAME, SQLDatabaseHelper.COLUMN_SERVERS_UUID,
            SQLDatabaseHelper.COLUMN_SERVERS_TOKEN, SQLDatabaseHelper.COLUMN_SERVERS_AUTO_CONNECT,
            SQLDatabaseHelper.COLUMN_SERVERS_TIMEOUT, SQLDatabaseHelper.COLUMN_SERVERS_CONNECTED};

    public ServersDataSource(Context context) {
        dbHelper = new SQLDatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Server createServer(Server server) {
        ContentValues values = new ContentValues();
        values.put(SQLDatabaseHelper.COLUMN_SERVERS_NAME, server.getServerName());
        values.put(SQLDatabaseHelper.COLUMN_SERVERS_IP, server.getServerIP());
        values.put(SQLDatabaseHelper.COLUMN_SERVERS_PORT, server.getPortNumber());
        values.put(SQLDatabaseHelper.COLUMN_SERVERS_SSL, server.isSSL() ? 1 : 0);
        values.put(SQLDatabaseHelper.COLUMN_SERVERS_USERNAME, server.getUsername());
        values.put(SQLDatabaseHelper.COLUMN_SERVERS_UUID, server.getUUID());
        values.put(SQLDatabaseHelper.COLUMN_SERVERS_TOKEN, server.getToken());
        values.put(SQLDatabaseHelper.COLUMN_SERVERS_AUTO_CONNECT, server.isAutoConnect() ? 1 : 0);
        values.put(SQLDatabaseHelper.COLUMN_SERVERS_TIMEOUT, server.getTimeout());
        values.put(SQLDatabaseHelper.COLUMN_SERVERS_CONNECTED, 0);
        long insertId = database.insert(SQLDatabaseHelper.TABLE_SERVERS, null,
                values);
        Cursor cursor = database.query(SQLDatabaseHelper.TABLE_SERVERS,
                allColumns, SQLDatabaseHelper.COLUMN_SERVERS_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Server newServer = cursorToServer(cursor);
        cursor.close();
        return newServer;
    }

    public Server updateServer(Server server) {
        ContentValues values = new ContentValues();
        values.put(SQLDatabaseHelper.COLUMN_SERVERS_NAME, server.getServerName());
        values.put(SQLDatabaseHelper.COLUMN_SERVERS_IP, server.getServerIP());
        values.put(SQLDatabaseHelper.COLUMN_SERVERS_PORT, server.getPortNumber());
        values.put(SQLDatabaseHelper.COLUMN_SERVERS_SSL, server.isSSL() ? 1 : 0);
        values.put(SQLDatabaseHelper.COLUMN_SERVERS_USERNAME, server.getUsername());
        values.put(SQLDatabaseHelper.COLUMN_SERVERS_UUID, server.getUUID());
        values.put(SQLDatabaseHelper.COLUMN_SERVERS_TOKEN, server.getToken());
        values.put(SQLDatabaseHelper.COLUMN_SERVERS_AUTO_CONNECT, server.isAutoConnect() ? 1 : 0);
        values.put(SQLDatabaseHelper.COLUMN_SERVERS_TIMEOUT, server.getTimeout());
        values.put(SQLDatabaseHelper.COLUMN_SERVERS_CONNECTED, server.isConnected());
        long id = database.update(SQLDatabaseHelper.TABLE_SERVERS, values, SQLDatabaseHelper.COLUMN_SERVERS_ID + " = " + server.getId(), new String[]{});
        Cursor cursor = database.query(SQLDatabaseHelper.TABLE_SERVERS,
                allColumns, SQLDatabaseHelper.COLUMN_SERVERS_ID + " = " + id, null,
                null, null, null);
        cursor.moveToFirst();
        Server newServer = cursorToServer(cursor);
        cursor.close();
        return newServer;
    }

    public Server getServer(long id) {
        Cursor cursor = database.query(SQLDatabaseHelper.TABLE_SERVERS,
                allColumns, SQLDatabaseHelper.COLUMN_SERVERS_ID + " = " + id, null,
                null, null, null);
        cursor.moveToFirst();
        Server server = cursorToServer(cursor);
        cursor.close();
        return server;
    }

    public void deleteServer(Server server) {
        long id = server.getId();
        Log.i("SQLite", "Server deleted with id: " + id);
        database.delete(SQLDatabaseHelper.TABLE_SERVERS, SQLDatabaseHelper.COLUMN_SERVERS_ID
                + " = " + id, null);
    }

    public List<Server> getAllServers() {
        ArrayList<Server> servers = new ArrayList<>();
        Cursor cursor = database.query(SQLDatabaseHelper.TABLE_SERVERS,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Server server = cursorToServer(cursor);
            servers.add(server);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return servers;
    }

    private Server cursorToServer(Cursor cursor) {
        Server server = new Server();
        server.setId(cursor.getLong(0));
        server.setServerName(cursor.getString(1));
        server.setServerIP(cursor.getString(2));
        server.setPortNumber(cursor.getInt(3));
        server.setSSL(cursor.getInt(4) == 1);
        server.setUsername(cursor.getString(5));
        server.setUUID(cursor.getString(6));
        server.setToken(cursor.getString(7));
        server.setAutoConnect(cursor.getInt(8) == 1);
        server.setTimeout(cursor.getInt(9));
        server.setConnected(cursor.getInt(10) == 1);
        return server;
    }
} 