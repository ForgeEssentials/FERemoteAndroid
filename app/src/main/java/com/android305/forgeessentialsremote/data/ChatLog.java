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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class ChatLog implements Serializable {

    private int id;
    private Timestamp timestamp;
    private int serverId;
    private UUID uuid = null;
    private String sender;
    private String message;

    public ChatLog() {
    }

    public ChatLog(int id, Timestamp timestamp, int serverId, String sender, UUID uuid,
                   String message, Server connectedServer) {
        this.id = id;
        this.timestamp = timestamp;
        this.serverId = serverId;
        this.uuid = uuid;
        this.sender = sender;
        this.message = message;
    }

    public Spanned getFormattedMessage() {
        //TODO need to implement a better pushchat to get chat format from FE
        //TODO check if user wants a timestamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        if (sender == null)
            return Html.fromHtml("[<font size=\"10\">" + dateFormat.format(timestamp) +"</font>] " + message);
        else
            return Html.fromHtml("[<font size=\"10\">" + dateFormat.format(timestamp) +"</font>][<font color=\"red\">" + sender + "</font>] " + message);
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

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
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
