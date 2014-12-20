package com.android305.forgeessentialsremote.servers;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableRow;

import com.android305.forgeessentialsremote.MainActivity;
import com.android305.forgeessentialsremote.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ServerAddFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ServerAddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServerAddFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private EditText serverName;
    private EditText ipAddressAndPort;
    private EditText username;
    private EditText token;
    private EditText timeout;
    private CheckBox ssl;
    private CheckBox autoConnect;
    private CheckBox defaultServer;
    private boolean edit = false;
    private long id = 0;
    private String uuid = null;

    public ServerAddFragment() {
    }

    public static ServerAddFragment newInstance(Server server, boolean edit) {
        ServerAddFragment fragment = new ServerAddFragment();
        Bundle args = new Bundle();
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(server);
            oos.close();
            args.putByteArray("server.ser", baos.toByteArray());
            args.putBoolean("edit", edit);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fragment.setArguments(args);
        return fragment;
    }

    private <E> E find(View view, int id) {
        View v = view.findViewById(id);
        if (v != null) {
            return (E) v;
        }
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_server_add, container, false);
        serverName = find(v, R.id.serverName);
        ipAddressAndPort = find(v, R.id.ipAddress);
        username = find(v, R.id.username);
        token = find(v, R.id.token);
        timeout = find(v, R.id.timeout);
        ssl = find(v, R.id.ssl);
        autoConnect = find(v, R.id.autoConnect);
        defaultServer = find(v, R.id.defaultServer);
        Bundle b = getArguments();
        if (b != null && b.containsKey("server.ser")) {
            try {
                byte[] data = b.getByteArray("server.ser");
                ObjectInputStream ois = new ObjectInputStream(
                        new ByteArrayInputStream(data));
                Server s = (Server) ois.readObject();
                ois.close();
                edit = b.getBoolean("edit");
                serverName.setText(s.getServerName());
                ipAddressAndPort.setText(s.getServerIP() + ":" + s.getPortNumber());
                username.setText(s.getUsername());
                token.setText(s.getToken());
                timeout.setText(Integer.toString(s.getTimeout()));
                ssl.setChecked(s.isSSL());
                autoConnect.setChecked(s.isAutoConnect());
                if (edit) {
                    id = s.getId();
                    uuid = s.getUUID();
                    if (s.isDefault(getActivity().getSharedPreferences(MainActivity.PREFS, Context.MODE_PRIVATE))) {
                        defaultServer.setChecked(true);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (!edit) {
            TableRow row = find(v, R.id.timeout_row);
            row.setVisibility(View.GONE);
        }
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        activity.invalidateOptionsMenu();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.server_add_fragment, menu);
        menu.findItem(R.id.action_settings).setVisible(false);
        if (edit)
            menu.findItem(R.id.action_add).setIcon(R.drawable.ic_action_edit);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId = item.getItemId();
        switch (menuId) {
            case R.id.action_add:
                String serverNameTxt = serverName.getText().toString().trim();
                String ipAddressAndPortTxt = ipAddressAndPort.getText().toString().trim();
                String usernameTxt = username.getText().toString().trim();
                String tokenTxt = token.getText().toString().trim();
                String timeoutTxt = timeout.getText().toString().trim();
                boolean isSSL = ssl.isChecked();
                boolean isAutoConnect = autoConnect.isChecked();
                boolean isDefaultServer = defaultServer.isChecked();
                serverName.setError(null);
                ipAddressAndPort.setError(null);
                username.setError(null);
                token.setError(null);
                timeout.setError(null);
                if (serverNameTxt.length() == 0) {
                    serverName.setError(getActivity().getString(R.string.field_required));
                    serverName.requestFocus();
                } else if (ipAddressAndPortTxt.length() == 0) {
                    ipAddressAndPort.setError(getActivity().getString(R.string.field_required));
                    ipAddressAndPort.requestFocus();
                } else if (usernameTxt.length() == 0) {
                    username.setError(getActivity().getString(R.string.field_required));
                    username.requestFocus();
                } else if (tokenTxt.length() == 0) {
                    token.setError(getActivity().getString(R.string.field_required));
                    token.requestFocus();
                } else if (timeoutTxt.length() == 0) {
                    timeout.setError(getActivity().getString(R.string.field_required));
                    timeout.requestFocus();
                } else {
                    String[] ipSplit = ipAddressAndPortTxt.split(":");
                    String ip = ipSplit[0];
                    int port = 27020;
                    if (ipSplit.length == 2) {
                        try {
                            port = Integer.parseInt(ipSplit[1]);
                        } catch (NumberFormatException e) {
                        }
                    }
                    Server newServer = new Server();
                    newServer.setId(id);
                    newServer.setServerName(serverNameTxt);
                    newServer.setServerIP(ip);
                    newServer.setPortNumber(port);
                    if (usernameTxt.length() > 0)
                        newServer.setUsername(usernameTxt);
                    newServer.setUUID(uuid);
                    newServer.setToken(tokenTxt);
                    try {
                        newServer.setTimeout(Integer.parseInt(timeoutTxt));
                    } catch (NumberFormatException e) {
                        newServer.setTimeout(15000);
                    }
                    newServer.setSSL(isSSL);
                    newServer.setAutoConnect(isAutoConnect);
                    mListener.onServerAction(newServer, isDefaultServer, edit);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public boolean onServerAction(Server serverToAdd, boolean makeDefault, boolean edit);
    }

}
