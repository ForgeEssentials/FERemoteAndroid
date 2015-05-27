package com.android305.forgeessentialsremote.servers.active.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android305.forgeessentialsremote.R;
import com.android305.forgeessentialsremote.data.Server;
import com.android305.forgeessentialsremote.data.Player;

import java.io.IOException;

/**
 * Created by Andres on 12/20/2014.
 */
public class PlayerListFragment extends ListFragment {

    public interface OnFragmentInteractionListener {
        public void onViewCreated(PlayerListFragment plFragment);

        public void onPlayerClick(Player player);
    }

    private OnFragmentInteractionListener mListener;

    private Server mServer;

    public PlayerListFragment() {
    }

    public static PlayerListFragment newInstance() {
        return new PlayerListFragment();
    }

    @SuppressWarnings("unchecked")
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
        View v = inflater.inflate(R.layout.fragment_active_player_list, container, false);
        mListener.onViewCreated(this);
        //TODO: add ArrayAdapter here and send it to the interface
        return v;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " +
                    "OnFragmentInteractionListener");
        }
        activity.invalidateOptionsMenu();
    }
}
