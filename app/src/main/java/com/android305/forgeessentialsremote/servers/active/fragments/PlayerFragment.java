package com.android305.forgeessentialsremote.servers.active.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android305.forgeessentialsremote.R;
import com.android305.forgeessentialsremote.servers.active.Server;

import java.io.IOException;

/**
 * Created by Andres on 12/20/2014.
 */
public class PlayerFragment extends Fragment {

    public PlayerFragment() {
    }

    public static PlayerFragment newInstance(Server server) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        try {
            args.putByteArray("server.ser", server.serialize());
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
        View v = inflater.inflate(R.layout.fragment_active_player, container, false);
        //TODO: Finish view to load the player (coulnd't finish sorry!), olee if you'd like to set up the server connection (which is the hard part, the rest is easy) Do it inside ServerConnection.
        //TODO: These methods should be static and should be protected because only the Server will access ServerConnection. The server gets serialized a lot so make sure that if I call connect on it, the server doesn't make a new socket, just grabs the cached one.
        //TODO: If you can think of a better system, by all means, go ahead.
        // Inflate the layout for this fragment
        return v;
    }
}
