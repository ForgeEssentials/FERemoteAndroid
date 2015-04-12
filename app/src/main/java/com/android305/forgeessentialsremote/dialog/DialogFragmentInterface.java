package com.android305.forgeessentialsremote.dialog;

public interface DialogFragmentInterface<E, T> {
    public void dialogClosedByOrientationChange(E dialog, T fragment);

    public void dialogCreated(E dialog, T fragment);

    public void dialogDismissed(E dialog, T fragment);
}
