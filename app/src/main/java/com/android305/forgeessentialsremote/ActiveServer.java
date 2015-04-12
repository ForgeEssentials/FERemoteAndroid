package com.android305.forgeessentialsremote;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;

import com.android305.forgeessentialsremote.servers.active.Server;
import com.android305.forgeessentialsremote.sqlite.datasources.ServersDataSource;


public class ActiveServer extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    private final static int SERVER_INFO = 0;
    private final static int PLAYER_LIST = 1;
    private final static int CHAT = 2;
    private final static int CONSOLE = 3;
    private final static int PERMISSIONS = 4;
    private Server activeServer;
    private ServersDataSource dataSource;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_server);
        dataSource = new ServersDataSource(this);
        dataSource.open();
        activeServer = dataSource.getServer(getIntent().getIntExtra("server.id", -1));
        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        setTitle(activeServer.getServerName());
    }

    @Override
    protected void onDestroy() {
        dataSource.close();
        super.onDestroy();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            case SERVER_INFO: //Server Information
                break;
            case PLAYER_LIST: //Player List
                break;
            case CHAT: //Chat
                break;
            case CONSOLE: //Console
                break;
            case PERMISSIONS: //Permissions
                break;

        }
        if (activeServer != null)
            setTitle(activeServer.getServerName());
        /*fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();*/
    }

}
