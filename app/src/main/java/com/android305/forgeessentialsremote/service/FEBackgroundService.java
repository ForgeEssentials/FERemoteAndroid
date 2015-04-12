package com.android305.forgeessentialsremote.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;

import com.android305.forgeessentialsremote.servers.active.Server;
import com.android305.forgeessentialsremote.sqlite.datasources.ServersDataSource;

import java.io.IOException;
import java.util.List;

public class FEBackgroundService extends Service {

    public final String COMMAND = "command";

    public final int COMMAND_CONNECT = 1;
    public final int COMMAND_DISCONNECT = 2;
    public final int COMMAND_QUERY_PLAYER = 3;

    private SparseArray<Server> loadedServers = new SparseArray<>();
    private ServersDataSource dataSource;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dataSource = new ServersDataSource(this);
        dataSource.open();
        new Thread() {
            public void run() {
                List<Server> servers = dataSource.getAllServers();
                for (Server server : servers) {
                    if (server.isAutoConnect()) {
                        try {
                            server.connect();
                            server.queryCapabilities();
                            loadedServers.setValueAt(server.getId(), server);
                        } catch (IOException e) {
                            Log.e("Service", "Couldn't connect to " + server.getServerIP() + ":" + server
                                    .getPortNumber(), e);
                            //TODO: send could not connect notification
                        }

                    }
                }
            }
        }.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        try {
            dataSource.close();
        } catch (Exception e) {
            // ignore all exceptions, we just want to close
        }
        super.onDestroy();
    }
}
