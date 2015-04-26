package com.android305.forgeessentialsremote.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.android305.forgeessentialsremote.ActiveServer;
import com.android305.forgeessentialsremote.MainActivity;
import com.android305.forgeessentialsremote.R;
import com.android305.forgeessentialsremote.data.ChatLog;
import com.android305.forgeessentialsremote.data.Server;
import com.android305.forgeessentialsremote.sqlite.datasources.ChatLogDataSource;
import com.android305.forgeessentialsremote.sqlite.datasources.ServersDataSource;
import com.forgeessentials.remote.client.RemoteClient;
import com.forgeessentials.remote.client.RemoteResponse;
import com.forgeessentials.remote.client.network.PushChatHandler;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;

public class FEBackgroundService extends Service {

    public static final String TASK_CONNECT_SERVER = "com.android305.forgeessentialsremote" + "" +
            ".CONNECT_SERVER";

    public static SparseArray<Server> loadedServers = new SparseArray<>();
    private ServersDataSource serversDataSource;
    private ChatLogDataSource chatLogDataSource;
    private Handler handler;
    private int connecting = -1;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        serversDataSource = new ServersDataSource(this);
        serversDataSource.open();
        chatLogDataSource = new ChatLogDataSource(this);
        chatLogDataSource.open();
        new Thread() {
            public void run() {
                List<Server> servers = serversDataSource.getAllServers();
                for (Server server : servers) {
                    if (server.isAutoConnect()) {
                        connect(server, false);
                    }
                }
            }
        }.start();
    }

    private synchronized void connect(final Server server, boolean fromActivity) {
        connecting = server.getId();
        if (loadedServers.get(server.getId()) != null) {
            if (server.getClient() == null || server.getClient().isClosed()) {
                loadedServers.delete(server.getId());
            } else {
                loadedServers.setValueAt(server.getId(), server);
                {
                    Intent i = new Intent(MainActivity.TASK_REFRESH_LIST);
                    if (fromActivity) i.putExtra("serverTriedConnect", true);
                    sendBroadcast(i);
                }
                connecting = -1;
                return;
            }
        }
        try {
            final RemoteClient client = server.connect();
            new Thread() {
                public void run() {
                    while (!client.isClosed()) {
                        RemoteResponse.JsonRemoteResponse response = client.getNextResponse(0);
                        if (response != null) handleResponse(server, client, response);
                    }
                }
            }.start();
            server.queryCapabilities();
            server.setPushChatEnabled(true);
        } catch (SocketTimeoutException e) {
            Log.e("Server", "Couldn't connect", e);
            if (fromActivity) {
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(FEBackgroundService.this, R.string.connection_timeout,
                                Toast.LENGTH_SHORT).show();


                    }
                });
            } else {
                //TODO: notification
            }
        } catch (final ConnectException e) {
            Log.e("Server", "Couldn't connect", e);
            if (fromActivity) {
                handler.post(new Runnable() {
                    public void run() {
                        if (e.getMessage().contains("ECONNREFUSED")) {
                            Toast.makeText(FEBackgroundService.this, R.string.connection_refused,
                                    Toast.LENGTH_SHORT).show();
                        } else if (e.getMessage().contains("ETIMEDOUT")) {
                            Toast.makeText(FEBackgroundService.this, R.string.connection_timeout,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(FEBackgroundService.this,
                                    "Couldn't connect: " + e.getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                //TODO: notification
            }
        } catch (final IOException e) {
            Log.e("Server", "Couldn't connect to " + server.getServerIP() +
                    ":" +
                    server.getPortNumber(), e);
            if (fromActivity) {
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(FEBackgroundService.this,
                                "Couldn't connect: " + e.getLocalizedMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                //TODO: notification
            }
        }
        connecting = -1;
        loadedServers.put(server.getId(), server);
        {
            Intent i = new Intent(MainActivity.TASK_REFRESH_LIST);
            if (fromActivity) i.putExtra("serverTriedConnect", true);
            sendBroadcast(i);
        }
    }

    private void handleResponse(Server server, RemoteClient client, RemoteResponse.JsonRemoteResponse response) {
        switch (response.id) {
            case PushChatHandler.ID: {
                RemoteResponse<PushChatHandler.Response> r = client.transformResponse(response,
                        PushChatHandler.Response.class);
                ChatLog c = chatLogDataSource.newChatLog(server, r.data.username, r.data.message);
                try {
                    Intent i = new Intent(ActiveServer.TASK_PUSH_CHAT);
                    i.putExtra("chatlog.ser", c.serialize());
                    sendBroadcast(i);
                } catch (IOException e) {
                    Log.e("PushChat", "Error serializing chat", e);
                }
                break;
            }
            case "shutdown": {
                client.close();
                break;
            }
            default:
                Log.d("Server", "No response.");
                break;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case TASK_CONNECT_SERVER:
                    int serverId = intent.getIntExtra("server.id", -1);
                    final Server server = serversDataSource.getServer(serverId);
                    if (connecting != serverId) {
                        new Thread() {
                            public void run() {
                                connect(server, true);
                            }
                        }.start();
                    }
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        try {
            serversDataSource.close();
        } catch (Exception e) {
            // ignore all exceptions, we just want to close
        }
        try {
            chatLogDataSource.close();
        } catch (Exception e) {
            // ignore all exceptions, we just want to close
        }
        super.onDestroy();
    }
}
