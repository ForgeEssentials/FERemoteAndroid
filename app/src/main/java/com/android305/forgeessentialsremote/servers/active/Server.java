package com.android305.forgeessentialsremote.servers.active;

import android.content.SharedPreferences;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by Andres on 12/18/2014.
 */
public class Server implements Serializable {

    private long id;
    private String serverName;
    private String serverIP;
    private int portNumber;
    private boolean ssl;
    private String username;
    private String uuid;
    private String token;
    private boolean autoConnect;
    private int timeout = 15000;

    private boolean isConnected = false;

    public Server() {
    }

    public Server(long id, String serverName, String serverIP, int portNumber, boolean ssl, String username, String uuid, String token, boolean autoConnect) {
        this.id = id;
        this.serverName = serverName;
        this.serverIP = serverIP;
        this.portNumber = portNumber;
        this.ssl = ssl;
        this.username = username;
        this.uuid = uuid;
        this.token = token;
        this.autoConnect = autoConnect;
    }

    public Server(long id, String serverName, String serverIP, int portNumber, boolean ssl, String username, String uuid, String token, boolean autoConnect, int timeout) {
        this.id = id;
        this.serverName = serverName;
        this.serverIP = serverIP;
        this.portNumber = portNumber;
        this.ssl = ssl;
        this.username = username;
        this.uuid = uuid;
        this.token = token;
        this.autoConnect = autoConnect;
        this.timeout = timeout;
    }

    public static Server getServerFromSerializedBytes(byte[] server) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(server));
        Server s = (Server) ois.readObject();
        ois.close();
        return s;
    }

    public boolean connect() {
        //TODO: Connect Session by using the ServerConnection class statically.
        return false;
    }

    public boolean ping() {
        //TODO: PONG by using the ServerConnection class statically.
        return false;
    }

    public boolean isDefault(SharedPreferences manager) {
        return manager.getLong("defaultServer", -1) == getId();
    }

    public void setDefault(SharedPreferences manager, boolean makeDefault) {
        if (makeDefault) {
            manager.edit().putLong("defaultServer", getId()).apply();
        } else if (manager.getLong("defaultServer", -1) == getId()) {
            manager.edit().remove("defaultServer").apply();
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public boolean isSSL() {
        return ssl;
    }

    public void setSSL(boolean ssl) {
        this.ssl = ssl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUUID() {
        return uuid;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isAutoConnect() {
        return autoConnect;
    }

    public void setAutoConnect(boolean autoConnect) {
        this.autoConnect = autoConnect;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public String toString() {
        return "Server{" +
                "id=" + id +
                ", serverName='" + serverName + '\'' +
                ", serverIP='" + serverIP + '\'' +
                ", portNumber=" + portNumber +
                ", ssl=" + ssl +
                ", username='" + username + '\'' +
                ", uuid='" + uuid + '\'' +
                ", token='" + token + '\'' +
                ", autoConnect=" + autoConnect +
                ", timeout=" + timeout +
                ", isConnected=" + isConnected +
                '}';
    }

    public byte[] serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(this);
        oos.close();
        return baos.toByteArray();
    }
}