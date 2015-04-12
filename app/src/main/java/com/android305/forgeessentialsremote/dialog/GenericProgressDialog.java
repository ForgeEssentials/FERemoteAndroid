package com.android305.forgeessentialsremote.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class GenericProgressDialog extends GenericDialogFragment<CustomProgressDialog,
        GenericProgressDialog> {
    private static final String MSG = "msg";
    private static final String TITLE = "title";
    private static final String BUTTON = "button";
    private static final String BUTTON_DESC = "button_desc";

    public static GenericProgressDialog newInstance(ActionBarActivity a, int title, int msg, int button,
                                                    int buttonDesc) {
        GenericProgressDialog f = new GenericProgressDialog();
        f.setActivty(a);
        Bundle args = new Bundle();
        args.putString(TITLE, a.getString(title));
        args.putString(MSG, a.getString(msg));
        args.putInt(BUTTON, button);
        args.putString(BUTTON_DESC, a.getString(buttonDesc));
        f.setArguments(args);
        return f;
    }

    public static GenericProgressDialog newInstance(ActionBarActivity a, int title, int msg) {
        GenericProgressDialog f = new GenericProgressDialog();
        f.setActivty(a);
        Bundle args = new Bundle();
        args.putString(TITLE, a.getString(title));
        args.putString(MSG, a.getString(msg));
        f.setArguments(args);
        return f;
    }

    private String message;

    public GenericProgressDialog() {
        super();
    }

    public void appendMessage(String string) {
        if (getDialog() != null) getCustomDialog().setMessage(message.concat(string));
    }

    @Override
    public CustomProgressDialog createDialog() {
        CustomProgressDialog pd = new CustomProgressDialog(getActivity());
        pd.setIndeterminate(true);
        Bundle args = getArguments();
        pd.setTitle(args.getString(TITLE));
        pd.setMessage(args.getString(MSG));
        if (args.containsKey(BUTTON))
            pd.setButton(args.getInt(BUTTON), args.getString(BUTTON_DESC),
                    (DialogInterface.OnClickListener) null);
        return pd;
    }

    public CustomProgressDialog getCustomDialog() {
        return ((CustomProgressDialog) getDialog());
    }

    public void postMessage(final String string) {
        a.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setMessage(string);
            }
        });
    }

    public void setMessage(String string) {
        if (getDialog() != null) {
            getCustomDialog().setMessage(string);
            message = string;
        }
    }
}