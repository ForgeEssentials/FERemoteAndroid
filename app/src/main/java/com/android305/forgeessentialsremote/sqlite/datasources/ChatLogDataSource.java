package com.android305.forgeessentialsremote.sqlite.datasources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android305.forgeessentialsremote.data.ChatLog;
import com.android305.forgeessentialsremote.data.Server;
import com.android305.forgeessentialsremote.sqlite.SQLDatabaseHelper;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatLogDataSource {

    // Database fields
    private SQLiteDatabase database;
    private SQLDatabaseHelper dbHelper;
    private String[] allColumns = {SQLDatabaseHelper.CHAT_LOG_ID,
            SQLDatabaseHelper.CHAT_LOG_TIMESTAMP, SQLDatabaseHelper.CHAT_LOG_SERVERS_ID,
            SQLDatabaseHelper.CHAT_LOG_SENDER_UUID, SQLDatabaseHelper.CHAT_LOG_SENDER,
            SQLDatabaseHelper.CHAT_LOG_MESSAGE};

    public ChatLogDataSource(Context context) {
        dbHelper = new SQLDatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * This method should only be used for server messages. i.e. [CONSOLE] messages
     *
     * @param server   server message belongs to
     * @param username username message belongs to
     * @param msg      message
     * @return identifying log object
     */
    public ChatLog newChatLog(Server server, Timestamp timestamp, String username, String msg) {
        return newChatLog(server, timestamp, null, username, msg);
    }

    public ChatLog newChatLog(Server server, Timestamp timestamp, String uuid, String username, String msg) {
        ContentValues values = new ContentValues();
        values.put(SQLDatabaseHelper.CHAT_LOG_SERVERS_ID, server.getId());
        values.put(SQLDatabaseHelper.CHAT_LOG_SENDER_UUID, uuid);
        values.put(SQLDatabaseHelper.CHAT_LOG_SENDER, username);
        values.put(SQLDatabaseHelper.CHAT_LOG_MESSAGE, msg);
        values.put(SQLDatabaseHelper.CHAT_LOG_TIMESTAMP, timestamp.toString());
        long insertId = database.insert(SQLDatabaseHelper.TABLE_CHAT_LOG, null, values);
        Cursor cursor = database.query(SQLDatabaseHelper.TABLE_CHAT_LOG, allColumns,
                SQLDatabaseHelper.CHAT_LOG_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        ChatLog newChatLog = cursorToChatLog(cursor);
        cursor.close();
        return newChatLog;
    }

    public ChatLog query(String selectionArguments) {
        Cursor cursor = database.query(SQLDatabaseHelper.TABLE_CHAT_LOG, allColumns,
                selectionArguments, null, null, null, null);
        cursor.moveToFirst();
        ChatLog chatLog = cursorToChatLog(cursor);
        cursor.close();
        return chatLog;
    }

    public void deleteServer(ChatLog chatLog) {
        long id = chatLog.getId();
        Log.i("SQLite", "ChatLog deleted with id: " + id);
        database.delete(SQLDatabaseHelper.TABLE_CHAT_LOG, SQLDatabaseHelper.CHAT_LOG_SERVERS_ID +
                " = " + id, null);
    }

    public List<ChatLog> getChatLogs(Server server) {
        ArrayList<ChatLog> chatLog = new ArrayList<>();
        Cursor cursor = database.query(SQLDatabaseHelper.TABLE_CHAT_LOG, allColumns, null, null,
                null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ChatLog c = cursorToChatLog(cursor);
            chatLog.add(c);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return chatLog;
    }

    private ChatLog cursorToChatLog(Cursor cursor) {
        ChatLog chatLog = new ChatLog();
        chatLog.setId(cursor.getInt(0));
        chatLog.setTimestamp(Timestamp.valueOf(cursor.getString(1)));
        chatLog.setServerId(cursor.getInt(2));
        String uuid = cursor.getString(3);
        chatLog.setUUID(uuid == null ? null : UUID.fromString(uuid));
        chatLog.setSender(cursor.getString(4));
        chatLog.setMessage(cursor.getString(5));
        return chatLog;
    }
} 
