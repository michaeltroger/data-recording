package com.michaeltroger.datarecording;

import android.support.annotation.NonNull;

public interface IView {
    void displayToast(@NonNull final String message);
    void sendCommand(@NonNull final String command);
}