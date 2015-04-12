package com.android305.forgeessentialsremote.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.KeyEvent;

public class CustomProgressDialog extends ProgressDialog {
    public CustomProgressDialog(Context context) {
        super(context);
    }

    @Deprecated
    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) return super.dispatchKeyEvent(event);
        return true;
    }

    @Deprecated
    @Override
    public void show() {
        super.show();
    }
}
