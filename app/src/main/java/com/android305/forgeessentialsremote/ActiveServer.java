package com.android305.forgeessentialsremote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.android305.forgeessentialsremote.data.ChatLog;
import com.android305.forgeessentialsremote.data.Server;
import com.android305.forgeessentialsremote.servers.active.fragments.ChatFragment;
import com.android305.forgeessentialsremote.sqlite.datasources.ChatLogDataSource;
import com.android305.forgeessentialsremote.sqlite.datasources.ServersDataSource;
import com.forgeessentials.remote.client.RemoteMessageID;
import com.forgeessentials.remote.client.RemoteRequest;

import java.io.IOException;


public class ActiveServer extends ActionBarActivity implements NavigationDrawerFragment
        .NavigationDrawerCallbacks, ChatFragment.OnFragmentInteractionListener {
    public final static String TASK_PUSH_CHAT = "com.android305.forgeesentialsremote" + "" +
            ".NEW_PUSH_CHAT";

    private final static int SERVER_INFO = 0;
    private final static int PLAYER_LIST = 1;
    private final static int CHAT = 2;
    private final static int CONSOLE = 3;
    private final static int PERMISSIONS = 4;
    private Server activeServer;
    private ServersDataSource serversDataSource;
    private ChatLogDataSource chatLogDataSource;

    private ChatFragment loadedChat;
    private DataUpdateReceiver dataUpdateReceiver;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_server);
        serversDataSource = new ServersDataSource(this);
        serversDataSource.open();
        chatLogDataSource = new ChatLogDataSource(this);
        chatLogDataSource.open();
        activeServer = serversDataSource.getServer(getIntent().getIntExtra("server.id", -1));
        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id
                .drawer_layout));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        setTitle(activeServer.getServerName());
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Fragment f = null;
        String tag = null;
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            case SERVER_INFO: //Server Information
                loadedChat = null;
                break;
            case PLAYER_LIST: //Player List
                loadedChat = null;
                break;
            case CHAT: //Chat
                f = ChatFragment.newInstance();
                tag = "fragment_chat";
                break;
            case CONSOLE: //Console
                loadedChat = null;
                break;
            case PERMISSIONS: //Permissions
                loadedChat = null;
                break;

        }
        if (activeServer != null) setTitle(activeServer.getServerName());
        if (f != null) {
            fragmentManager.beginTransaction().replace(R.id.container, f, tag).commit();
        }
        /*fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();*/
    }

    @Override
    public String onViewCreated(ChatFragment chat) {
        loadedChat = chat;
        loadedChat.refreshChatLog(chatLogDataSource.getChatLogs(activeServer));
        return activeServer.getUsername();
    }

    @Override
    public void sendChatMessage(String message) {
        try {
            System.out.println(String.format("Sending chat message: %s", message));
            RemoteRequest<String> request = new RemoteRequest<String>(RemoteMessageID.SEND_CHAT, message);
            activeServer.getClient().sendRequest(request);
        } catch (IOException e) {
            activeServer.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dataUpdateReceiver == null) dataUpdateReceiver = new DataUpdateReceiver();
        IntentFilter intentFilter = new IntentFilter(TASK_PUSH_CHAT);
        registerReceiver(dataUpdateReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dataUpdateReceiver != null) unregisterReceiver(dataUpdateReceiver);
    }

    private class DataUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case TASK_PUSH_CHAT:
                    if (loadedChat != null && loadedChat.isVisible()) {
                        if (intent.hasExtra("chatlog.ser")) {
                            try {
                                ChatLog c = ChatLog.getServerFromSerializedBytes(intent
                                        .getByteArrayExtra("chatlog.ser"));
                                if (c.getServerId() == activeServer.getId())
                                    loadedChat.appendToChatLog(c);
                            } catch (IOException | ClassNotFoundException e) {
                                Log.e("PushChat", "something went wrong while getting the chat "
                                        + "log", e);
                            }
                        } else {
                            loadedChat.refreshChatLog(chatLogDataSource.getChatLogs(activeServer));
                        }
                    }
                    break;
            }
        }
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
