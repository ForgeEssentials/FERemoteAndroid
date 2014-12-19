package com.android305.forgeessentialsremote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android305.forgeessentialsremote.servers.Server;
import com.android305.forgeessentialsremote.servers.ServerAddFragment;
import com.android305.forgeessentialsremote.servers.ServerListFragment;
import com.android305.forgeessentialsremote.servers.ServersDataSource;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, ServerListFragment.OnFragmentInteractionListener, ServerAddFragment.OnFragmentInteractionListener {

    public final static String PREFS = "com.android305.forgeessentialsremote";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    public NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private int screen = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fragmentManager = getSupportFragmentManager();
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                fragmentManager.findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (screen != position) {
            screen = position;
            invalidateOptionsMenu();
        }
        switch (position) {
            default:
            case 0: //Server List
                mTitle = getString(R.string.title_server_list);
                Fragment f = fragmentManager.findFragmentByTag("server_list_fragment");
                if (f == null || (!f.isVisible() && !f.isAdded())) {
                    Log.d("Fragments", "Adding Server List Fragment");
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, new ServerListFragment(), "server_list_fragment")
                            .commit();
                }
                break;
            case 1: //Settings
                mTitle = getString(R.string.title_settings);
                break;
            case 2: //About
                mTitle = getString(R.string.title_about);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (screen == 3) {
            screen = 0;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Server server, boolean longClick) {
        if (longClick) {
            showServerScreen(server, true);
        } else {
            //TODO: Open server screen
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String[] split = scanResult.getContents().split("@");
            String[] secSplit = split[1].split("|");
            String[] ipSplit = secSplit[0].split(":");
            String username = split[0];
            String host = ipSplit[0];
            int port = Integer.parseInt(ipSplit[1]);
            String token;
            String serverName;
            boolean ssl = false;
            if (secSplit.length == 4) {
                if (secSplit[1].equals("ssl"))
                    ssl = true;
                serverName = secSplit[2];
                token = secSplit[3];
            } else {
                serverName = secSplit[1];
                token = secSplit[2];
            }
            Server server = new Server();
            server.setServerName(serverName);
            server.setServerIP(host);
            server.setPortNumber(port);
            server.setUsername(username);
            server.setToken(token);
            server.setSSL(ssl);
            showServerScreen(server, false);
        }
    }

    @Override
    public void onServerAdd(boolean barcode) {
        if (barcode) {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.initiateScan();
        } else {
            showServerScreen(null, false);
        }
    }

    private void showServerScreen(Server server, boolean edit) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment f = fragmentManager.findFragmentByTag("server_add_fragment");
        if (f == null || (!f.isVisible() && !f.isAdded())) {
            Log.d("Fragments", "Adding Server List Fragment");
            screen = 3;
            Fragment serverAddFragment;
            if (server == null) {
                serverAddFragment = new ServerAddFragment();
            } else {
                serverAddFragment = ServerAddFragment.newInstance(server, edit);
            }
            fragmentManager.beginTransaction().add(R.id.container, serverAddFragment, "server_add_fragment").addToBackStack(null)
                    .commit();
        }
    }


    @Override
    public boolean onServerAction(Server serverToAdd, boolean makeDefault, boolean edit) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ServerListFragment listFragment = (ServerListFragment) fragmentManager.findFragmentByTag("server_list_fragment");
        Fragment f = fragmentManager.findFragmentByTag("server_add_fragment");
        ServersDataSource dataSource = new ServersDataSource(this);
        dataSource.open();
        Server addedServer;
        if (!edit)
            addedServer = dataSource.createServer(serverToAdd);
        else
            addedServer = dataSource.updateServer(serverToAdd);
        if (addedServer != null) {
            addedServer.setDefault(getSharedPreferences(MainActivity.PREFS, MODE_PRIVATE), makeDefault);
            listFragment.refresh();
            fragmentManager.beginTransaction().detach(f).commit();
            dataSource.close();
        } else {
            dataSource.close();
            return false;
        }
        return true;
    }
}
