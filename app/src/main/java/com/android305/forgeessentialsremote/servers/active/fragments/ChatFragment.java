package com.android305.forgeessentialsremote.servers.active.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android305.forgeessentialsremote.R;
import com.android305.forgeessentialsremote.data.ChatLog;

import java.util.List;

public class ChatFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private ProgressBar progress;

    private TextView chatLog;

    public ChatFragment() {
    }

    public static ChatFragment newInstance() {
        return new ChatFragment();
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
        View v = inflater.inflate(R.layout.fragment_active_chat, container, false);
        TextView username = find(v, R.id.username);
        final EditText chatMsg = find(v, R.id.chat_message);
        chatLog = find(v, R.id.chat_log);
        progress = find(v, R.id.progressBar);
        chatLog.setMovementMethod(new ScrollingMovementMethod());
        chatMsg.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    mListener.sendChatMessage(chatMsg.getText().toString());
                    chatMsg.setText("");
                    return true;
                }
                return false;
            }
        });
        username.setText(mListener.onViewCreated(this) + ": ");
        return v;
    }

    public void appendToChatLog(ChatLog c) {
        chatLog.append(c.getFormattedMessage());
        chatLog.append("\n");
    }

    public void clearChatLog() {
        chatLog.setText("");
    }

    public void refreshChatLog(List<ChatLog> cl) {
        setProgressBarEnabled(true);
        chatLog.setText("");
        for (ChatLog chat : cl) {
            chatLog.append(chat.getFormattedMessage());
            chatLog.append("\n");
        }
        setProgressBarEnabled(false);
    }

    public void setProgressBarEnabled(boolean enabled) {
        progress.setVisibility(enabled ? View.VISIBLE : View.INVISIBLE);
    }

    public interface OnFragmentInteractionListener {
        public String onViewCreated(ChatFragment chat);

        public void sendChatMessage(String message);
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
