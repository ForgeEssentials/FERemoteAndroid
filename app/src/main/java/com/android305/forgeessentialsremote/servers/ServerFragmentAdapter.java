package com.android305.forgeessentialsremote.servers;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android305.forgeessentialsremote.MainActivity;
import com.android305.forgeessentialsremote.R;
import com.android305.forgeessentialsremote.servers.active.Server;

import java.util.List;

/**
 * Created by Andres on 12/18/2014.
 */
public class ServerFragmentAdapter extends ArrayAdapter<Server> {

    SparseArray<Server> servers = new SparseArray<>();

    public ServerFragmentAdapter(Context context) {
        super(context, R.layout.fragment_server_list_item);
    }

    public ServerFragmentAdapter(Context context, Server[] objects) {
        super(context, R.layout.fragment_server_list_item, objects);
        for (Server s : objects) {
            servers.put(s.getId(), s);
        }
    }

    public ServerFragmentAdapter(Context context, List<Server> objects) {
        super(context, R.layout.fragment_server_list_item, objects);
        for (Server s : objects) {
            servers.put(s.getId(), s);
        }
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.fragment_server_list_item, parent, false);
        }
        Server server = getItem(position);
        if (server != null) {
            TextView name = (TextView) v.findViewById(android.R.id.text1);
            TextView ip = (TextView) v.findViewById(android.R.id.text2);
            if (name != null) {
                name.setText(server.getServerName());
            }
            if (ip != null) {
                boolean isDefault = server.isDefault(getContext().getSharedPreferences(MainActivity.PREFS, Context.MODE_PRIVATE));
                String ipA = server.getServerIP();
                int port = server.getPortNumber();
                StringBuilder sb = new StringBuilder(ipA);
                sb.append(":");
                sb.append(port);
                if (server.isSSL() || server.isConnected() || server.isAutoConnect() || isDefault) {
                    boolean addComma = false;
                    sb.append(" (");
                    if (server.isSSL()) {
                        sb.append("S");
                        addComma = true;
                    }
                    if (server.isAutoConnect()) {
                        if (addComma)
                            sb.append(", ");
                        addComma = true;
                        sb.append("AC");
                    }
                    if (isDefault) {
                        if (addComma)
                            sb.append(", ");
                        addComma = true;
                        sb.append("Default");
                    }
                    if (server.isConnected()) {
                        if (addComma)
                            sb.append(", ");
                        sb.append("Connected");
                    }
                    sb.append(")");
                }
                ip.setText(sb.toString());
            }
            v.setTag(position);
        }
        return v;
    }
}
