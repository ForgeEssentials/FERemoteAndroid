package com.android305.forgeessentialsremote;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;

import com.android305.forgeessentialsremote.servers.active.Server;
import com.android305.forgeessentialsremote.servers.ServersDataSource;
import com.android305.forgeessentialsremote.servers.active.fragments.PlayerFragment;


public class ActiveServer extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    private Server activeServer;
    private ServersDataSource dataSource;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_server);
        dataSource = new ServersDataSource(this);
        dataSource.open();
        activeServer = dataSource.getServer(getIntent().getLongExtra("server.id", -1));
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        mNavigationDrawerFragment.load(activeServer.getUsername());
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
            case 0: //Server Information
                break;
            case 1: //Logged in Player
                fragmentManager.beginTransaction()
                        .replace(R.id.container, PlayerFragment.newInstance(activeServer))
                        .commit();
                break;
            case 2: //Player List
                break;
            case 3: //Chat
                break;
            case 4: //Console
                break;
            case 5: //Permissions
                break;

        }
        if (activeServer != null)
            setTitle(activeServer.getServerName());
        /*fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();*/
    }

}
