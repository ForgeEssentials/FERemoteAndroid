package com.android305.forgeessentialsremote.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

@SuppressWarnings("unchecked")
public abstract class GenericDialogFragment<E, T> extends DialogFragment {

    private DialogFragmentInterface<E, T> iface;

    protected Activity a;

    public GenericDialogFragment() {
    }

    public void setActivty(Activity a) {
        this.a = a;
    }

    public abstract E createDialog();

    /**
     * Dismisses the dialog. Make sure this method is used and <b>NOT</b> the
     * Dialog's dismiss.
     */
    @Override
    public void dismiss() {
        a.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (getDialog() != null) {
                    FragmentManager manager = a.getFragmentManager();
                    if (!manager.isDestroyed()) {
                        FragmentTransaction ft = manager.beginTransaction();
                        ft.remove(manager.findFragmentByTag(getTag()));
                        ft.commit();
                        manager.executePendingTransactions();
                    }
                }
            }
        });
    }

    public Activity getCorrectActivity() {
        return a;
    }

    @Override
    public final void onCancel(DialogInterface dialog) {
        dismiss();
    }

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public final Dialog onCreateDialog(Bundle savedInstanceState) {
        E pd = createDialog();
        if (iface != null) iface.dialogCreated(pd, (T) this);
        return (Dialog) pd;
    }

    @Override
    public void onDestroy() {
        if (iface != null) iface.dialogDismissed((E) getDialog(), (T) this);
        super.onDestroy();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if ((getDialog() != null) && getRetainInstance()) getDialog().setDismissMessage(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (iface != null) {
            iface.dialogClosedByOrientationChange((E) getDialog(), (T) this);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * This method is no longer used. Use
     * {@link #showDialog(String, DialogFragmentInterface)}.
     *
     * @throws RuntimeException when called
     */
    @Deprecated
    @Override
    public void show(android.app.FragmentManager manager, String tag) {
        throw new RuntimeException("Use showDialog instead.");
    }

    public boolean showDialog(String tag, DialogFragmentInterface<E, T> iface) {
        try {
            FragmentManager manager = a.getFragmentManager();
            this.iface = iface;
            if (manager.isDestroyed()) return false;
            try {
                manager.executePendingTransactions();
            } catch (Exception e) {
            }
            Fragment dialog = manager.findFragmentByTag(tag);
            if ((dialog != null && !dialog.isAdded()) || dialog == null) {
                super.show(manager, tag);
                return true;
            }
        } catch (Exception e) {
            Log.e("Dialog", "Error showing dialog " + tag, e);
        }
        return false;
    }

    @Override
    public void startActivity(Intent i) {
        a.startActivity(i);
    }

    @Override
    public void startActivity(Intent intent, Bundle options) {
        a.startActivity(intent, options);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        a.startActivityForResult(intent, requestCode);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        a.startActivityForResult(intent, requestCode, options);
    }

}