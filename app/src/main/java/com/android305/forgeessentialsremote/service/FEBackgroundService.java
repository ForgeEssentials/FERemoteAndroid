package com.android305.forgeessentialsremote.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.SparseArray;

import com.android305.forgeessentialsremote.servers.active.Server;

/**
 * Created by Andres on 12/28/2014.
 */
public class FEBackgroundService extends Service {

    public final String COMMAND = "command";

    public final int COMMAND_CONNECT = 1;
    public final int COMMAND_DISCONNECT = 2;
    public final int COMMAND_QUERY_PLAYER = 3;

    private SparseArray<Server> loadedServers = new SparseArray<>();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
