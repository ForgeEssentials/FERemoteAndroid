package com.android305.forgeessentialsremote.servers.active.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
public class PlayerFragment extends Fragment {

    private Server mServer;
    private Player mPlayer;

    private TextView username;
    private TextView uuid;
    private TextView health;
    private TextView armor;
    private TextView hunger;
    private TextView saturation;
    private TextView location;

    public PlayerFragment() {
    }

    public static PlayerFragment newInstance(Server server, Player player) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        try {
            //args.putByteArray("server.ser", server.serialize());
            args.putByteArray("player.ser", player.serialize());
        } catch (IOException e) {
            e.printStackTrace();
        }
        fragment.setArguments(args);
        return fragment;
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
        View v = inflater.inflate(R.layout.fragment_active_player, container, false);
            Bundle b = getArguments();
            //mServer = Server.getServerFromSerializedBytes(b.getByteArray("server.ser"));
            //mPlayer = Player.getPlayerFromSerializedBytes(b.getByteArray("player.ser"));
            username = find(v, R.id.username);
            uuid = find(v, R.id.uuid);
            health = find(v, R.id.health);
            armor = find(v, R.id.armor);
            hunger = find(v, R.id.hunger);
            saturation = find(v, R.id.saturation);
            location = find(v, R.id.location);

            updatePlayer(mPlayer);
        return v;
    }

    public void updatePlayer(Player player) {
        mPlayer = player;
        username.setText(mPlayer.getUsername());
        uuid.setText(mPlayer.getUUID());
        health.setText(Float.toString(mPlayer.getHealth()));
        armor.setText(Float.toString(mPlayer.getArmor()));
        hunger.setText(Float.toString(mPlayer.getHunger()));
        saturation.setText("(S) ");
        saturation.append(Float.toString(mPlayer.getSaturation()));
        StringBuilder sb = new StringBuilder("DIM: ");
        sb.append(mPlayer.getDim());
        sb.append(", X: ");
        sb.append(mPlayer.getX());
        sb.append(", Y: ");
        sb.append(mPlayer.getY());
        sb.append(", Z: ");
        sb.append(mPlayer.getZ());
        location.setText(sb.toString());
    }
}
