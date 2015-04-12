package com.android305.forgeessentialsremote;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android305.forgeessentialsremote.dialog.CustomProgressDialog;
import com.android305.forgeessentialsremote.dialog.DialogFragmentInterface;
import com.android305.forgeessentialsremote.dialog.GenericProgressDialog;
import com.android305.forgeessentialsremote.servers.ServerAddFragment;
import com.android305.forgeessentialsremote.servers.ServerListFragment;
import com.android305.forgeessentialsremote.servers.active.Server;
import com.android305.forgeessentialsremote.service.FEBackgroundService;
import com.android305.forgeessentialsremote.sqlite.datasources.ServersDataSource;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends ActionBarActivity implements ServerListFragment
        .OnFragmentInteractionListener, ServerAddFragment.OnFragmentInteractionListener {

    public final static String PREFS = "com.android305.forgeessentialsremote";

    public static final Pattern CONNECT_STRING_PATTERN = Pattern.compile("([^@\\n]+)@(ssl:)?" + "" +
            "([^:|\\n]+)(?::(\\d+))?(?:\\|([^\\|\\n]+))?", Pattern.CASE_INSENSITIVE);

    private ServerListFragment listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                        try {
                            server.connect();
                            server.queryCapabilities();
                        } catch (SocketTimeoutException e) {
                            Log.e("Server", "Couldn't connect", e);
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(MainActivity.this,
                                            R.string.connection_timeout, Toast.LENGTH_SHORT).show();


                                }
                            });
                        } catch (final ConnectException e) {
                            Log.e("Server", "Couldn't connect", e);
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    if (e.getMessage().contains("ECONNREFUSED")) {
                                        Toast.makeText(MainActivity.this,
                                                R.string.connection_refused,
                                                Toast.LENGTH_SHORT).show();
                                    } else if (e.getMessage().contains("ETIMEDOUT")) {
                                        Toast.makeText(MainActivity.this,
                                                R.string.connection_timeout,
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MainActivity.this,
                                                "Couldn't connect: " + e.getLocalizedMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } catch (final IOException e) {
                            Log.e("Server", "Couldn't connect to " + server.getServerIP() + ":" +
                                    server.getPortNumber(), e);
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(MainActivity.this,
                                            "Couldn't connect: " + e.getLocalizedMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
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
        ServersDataSource dataSource = new ServersDataSource(this);
        dataSource.open();
        Server addedServer;
        if (!edit) addedServer = dataSource.createServer(serverToAdd);
        else addedServer = dataSource.updateServer(serverToAdd);
        if (addedServer != null) {
            addedServer.setDefault(getSharedPreferences(MainActivity.PREFS, MODE_PRIVATE),
                    makeDefault);
            listFragment.refresh();
            fragmentManager.beginTransaction().detach(f).commit();
            dataSource.close();
        } else {
            dataSource.close();
            return false;
        }
        return true;
    }

    @Override
    public void onAttach(ServerListFragment fragment) {
        listFragment = fragment;
        startService(new Intent(this, FEBackgroundService.class));
    }
}
