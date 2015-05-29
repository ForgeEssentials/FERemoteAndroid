package com.android305.forgeessentialsremote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android305.forgeessentialsremote.data.Server;
import com.android305.forgeessentialsremote.dialog.CustomProgressDialog;
import com.android305.forgeessentialsremote.dialog.DialogFragmentInterface;
import com.android305.forgeessentialsremote.dialog.GenericProgressDialog;
import com.android305.forgeessentialsremote.servers.ServerAddFragment;
import com.android305.forgeessentialsremote.servers.ServerListFragment;
import com.android305.forgeessentialsremote.service.FEBackgroundService;
import com.android305.forgeessentialsremote.sqlite.datasources.ServersDataSource;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends ActionBarActivity implements ServerListFragment
        .OnFragmentInteractionListener, ServerAddFragment.OnFragmentInteractionListener {

    public final static String PREFS = "com.android305.forgeessentialsremote";

    public final static String TASK_REFRESH_LIST = "com.android305.forgeesentialsremote" + "" +
            ".REFRESH_LIST";

    public static final Pattern CONNECT_STRING_PATTERN = Pattern.compile("([^@\\n]+)@(ssl:)?" + "" +
            "([^:|\\n]+)(?::(\\d+))?(?:\\|([^\\|\\n]+))?", Pattern.CASE_INSENSITIVE);

    private ServerListFragment listFragment;
    private DataUpdateReceiver dataUpdateReceiver;
    private ServersDataSource serversDataSource;
    private boolean serverTriedConnect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serversDataSource = new ServersDataSource(this);
        serversDataSource.open();
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment f = fragmentManager.findFragmentByTag("server_list_fragment");
        if (f == null || (!f.isVisible() && !f.isAdded())) {
            Log.d("Fragments", "Adding Server List Fragment");
            fragmentManager.beginTransaction().replace(R.id.container, new ServerListFragment(),
                    "server_list_fragment").commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void onFragmentInteraction(final Server server, boolean longClick) {
        if (longClick) {
            showServerScreen(server, true);
        } else {
            if (server.isConnected()) {
                Intent i = new Intent(this, ActiveServer.class);
                i.putExtra("server.id", server.getId());
                startActivity(i);
            } else {
                final Thread thread = new Thread() {
                    public void run() {
                        serverTriedConnect = false;
                        Intent i = new Intent(MainActivity.this, FEBackgroundService.class);
                        i.setAction(FEBackgroundService.TASK_CONNECT_SERVER);
                        i.putExtra("server.id", server.getId());
                        startService(i);
                        while (!serverTriedConnect) try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            // Ignore
                        }
                        serverTriedConnect = false;
                    }
                };
                GenericProgressDialog dialog = GenericProgressDialog.newInstance(this,
                        R.string.connecting, R.string.please_wait_,
                        CustomProgressDialog.BUTTON_NEGATIVE, R.string.cancel);
                dialog.showDialog("dialog_connectingServer",
                        new DialogFragmentInterface<CustomProgressDialog, GenericProgressDialog>() {

                            private void setShow(final CustomProgressDialog dialog,
                                                 final GenericProgressDialog fragment) {
                                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface d) {
                                        Button b = dialog.getButton(CustomProgressDialog
                                                .BUTTON_NEGATIVE);
                                        b.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                server.disconnect();
                                                fragment.dismiss();
                                            }
                                        });
                                    }
                                });
                            }

                            @Override
                            public void dialogClosedByOrientationChange(CustomProgressDialog
                                                                                dialog,
                                                                        GenericProgressDialog
                                                                                fragment) {
                            }

                            @Override
                            public void dialogCreated(CustomProgressDialog dialog,
                                                      final GenericProgressDialog fragment) {
                                setShow(dialog, fragment);
                                if (!thread.isAlive()) thread.start();
                                new Thread() {
                                    @Override
                                    public void run() {
                                        while (thread.isAlive()) try {
                                            Thread.sleep(10);
                                        } catch (InterruptedException e) {
                                            //ignore
                                        }
                                        fragment.dismiss();
                                        if (server.isConnected()) {
                                            Intent i = new Intent(MainActivity.this,
                                                    ActiveServer.class);
                                            i.putExtra("server.id", server.getId());
                                            startActivity(i);
                                        }
                                    }
                                }.start();
                            }

                            @Override
                            public void dialogDismissed(CustomProgressDialog dialog,
                                                        GenericProgressDialog fragment) {
                            }
                        });
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode,
                intent);
        if (scanResult != null) {
            try {
                Matcher m = CONNECT_STRING_PATTERN.matcher(scanResult.getContents());
                if (m.matches()) {
                    String ssl = m.group(2);
                    String port = m.group(4);
                    Server server = new Server();
                    server.setServerName(m.group(3));
                    server.setServerIP(m.group(3));
                    server.setPortNumber(port == null ? 27020 : Integer.parseInt(port));
                    server.setUsername(m.group(1));
                    server.setToken(m.group(5));
                    server.setSSL(ssl != null && ssl.equalsIgnoreCase("ssl"));
                    showServerScreen(server, false);
                } else {
                    Toast.makeText(this, getString(R.string.malformed_qr_code),
                            Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, getString(R.string.malformed_qr_code),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onViewCreated(View view, final FloatingActionsMenu addServerMenu) {
        FloatingActionButton button = (FloatingActionButton) view.findViewById(R.id.normal_plus);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addServerMenu.collapse();
                onServerAddClicked(false);
            }
        });

        FloatingActionButton barcodeButton = (FloatingActionButton) view.findViewById(R.id.barcode_plus);
        barcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addServerMenu.collapse();
                onServerAddClicked(true);
            }
        });
    }

    public void onServerAddClicked(boolean barcode) {
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
            Fragment serverAddFragment;
            if (server == null) {
                serverAddFragment = new ServerAddFragment();
            } else {
                serverAddFragment = ServerAddFragment.newInstance(server, edit);
            }
            fragmentManager.beginTransaction().add(R.id.container, serverAddFragment,
                    "server_add_fragment").addToBackStack(null).commit();
        }
    }

    @Override
    public boolean onServerAction(Server serverToAdd, boolean makeDefault, boolean edit) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment f = fragmentManager.findFragmentByTag("server_add_fragment");
        Server addedServer;
        if (!edit) addedServer = serversDataSource.createServer(serverToAdd);
        else addedServer = serversDataSource.updateServer(serverToAdd);
        if (addedServer != null) {
            addedServer.setDefault(getSharedPreferences(MainActivity.PREFS, MODE_PRIVATE),
                    makeDefault);
            listFragment.refresh();
            fragmentManager.beginTransaction().detach(f).commit();
            return true;
        }
        return false;
    }

    @Override
    public void onAttach(ServerListFragment fragment) {
        listFragment = fragment;
        startService(new Intent(this, FEBackgroundService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dataUpdateReceiver == null) dataUpdateReceiver = new DataUpdateReceiver();
        IntentFilter intentFilter = new IntentFilter(TASK_REFRESH_LIST);
        registerReceiver(dataUpdateReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dataUpdateReceiver != null) unregisterReceiver(dataUpdateReceiver);
    }

    @Override
    public void onDestroy() {
        try {
            serversDataSource.close();
        } catch (Exception e) {
            // ignore all exceptions, we just want to close
        }
        super.onDestroy();
    }

    private class DataUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case TASK_REFRESH_LIST:
                    if (intent.hasExtra("serverTriedConnect"))
                        serverTriedConnect = intent.getBooleanExtra("serverTriedConnect", false);
                    listFragment.refresh();
                    break;
            }
        }
    }
}
