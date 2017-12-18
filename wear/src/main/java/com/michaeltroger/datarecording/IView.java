package com.michaeltroger.datarecording;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

public interface IView {
    void displayToast(final @StringRes int message);
    void sendCommand(@NonNull final String command);
}
