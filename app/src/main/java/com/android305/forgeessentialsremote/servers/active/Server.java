package com.android305.forgeessentialsremote.servers.active;

import android.content.SharedPreferences;
import android.util.Log;

import com.forgeessentials.remote.client.RemoteClient;
import com.forgeessentials.remote.client.RemoteRequest;
import com.forgeessentials.remote.client.RemoteResponse;
import com.forgeessentials.remote.client.RequestAuth;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Server implements Serializable {

    private int id;
    private String serverName;
    private String serverIP;
    private int portNumber;
    private boolean ssl;
    private String username;
    private String uuid;
    private String token;
    private boolean autoConnect;
    private int timeout = 15000;

    private RemoteClient client;
    private RequestAuth auth;

    public Server() {
    }

    public Server(int id, String serverName, String serverIP, int portNumber, boolean ssl,
                  String username, String uuid, String token, boolean autoConnect) {
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

    public Server(int id, String serverName, String serverIP, int portNumber, boolean ssl,
                  String username, String uuid, String token, boolean autoConnect, int timeout) {
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

    public static Server getServerFromSerializedBytes(byte[] server) throws IOException,
            ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(server));
        Server o = (Server) ois.readObject();
        ois.close();
        return o;
    }

    public void disconnect() {
        if (client != null) client.close();
    }

    public RemoteClient connect() throws IOException {
        Socket s = new Socket();
        s.connect(new InetSocketAddress(serverIP, portNumber), timeout);
        client = new RemoteClient(s);
        auth = new RequestAuth(uuid != null && !uuid.isEmpty() ? uuid : username, token);
        return client;
    }

    public RemoteResponse.JsonRemoteResponse queryCapabilities() {
        RemoteRequest<Object> request = new RemoteRequest<>("query_remote_capabilities", auth,
                null);
        RemoteResponse.JsonRemoteResponse response = client.sendRequestAndWait(request, timeout);
        if (response == null) {
            Log.d("Server", "Error QueryCap: no response");
            return null;
        }
        if (!response.success) {
            Log.d("Server", "Error QueryCap: " + response.message);
            return null;
        }
        Log.d("Server", "Capabilities: " + response.data.toString());
        return response;
    }

    private boolean enablePushChat(boolean enable) {
        RemoteRequest<RemoteRequest.PushRequestData> request = new RemoteRequest<>("push_chat",
                auth, new RemoteRequest.PushRequestData(enable));
        RemoteResponse.JsonRemoteResponse response = client.sendRequestAndWait(request, timeout);
        if (response == null) {
            Log.d("Server", "Error PushChat: no response");
            return false;
        }
        if (!response.success) {
            Log.d("Server", "Error PushChat: " + response.message);
            return false;
        }
        Log.d("Server", "PushChat: " + (response.data != null ? response.data.toString() :
                response.message));
        return true;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
        return client != null && !client.isClosed();
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
                ", isConnected=" + isConnected() +
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
