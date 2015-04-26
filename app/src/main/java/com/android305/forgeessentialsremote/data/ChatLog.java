package com.android305.forgeessentialsremote.data;

import android.text.Html;
import android.text.Spanned;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Timestamp;

public class ChatLog implements Serializable {

    private int id;
    private Timestamp timestamp;
    private int serverId;
    private String uuid = null;
    private String username;
    private String message;

    public ChatLog() {
    }

    public ChatLog(int id, Timestamp timestamp, int serverId, String uuid, String username,
                   String message, Server connectedServer) {
        this.id = id;
        this.timestamp = timestamp;
        this.serverId = serverId;
        this.uuid = uuid;
        this.username = username;
        this.message = message;
    }

    public Spanned getFormattedMessage() {
        //TODO need to implement a better pushchat to get chat format from FE
        //TODO check if user wants a timestamp
        return Html.fromHtml("[<font size=\"10\">" + timestamp +"</font>][<font color=\"red\">" + username + "</font>] " + message);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public String getUUID() {
        return uuid;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static ChatLog getServerFromSerializedBytes(byte[] chatLog) throws IOException,
            ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(chatLog));
        ChatLog o = (ChatLog) ois.readObject();
        ois.close();
        return o;
    }

    public byte[] serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(this);
        oos.close();
        return baos.toByteArray();
    }

}
