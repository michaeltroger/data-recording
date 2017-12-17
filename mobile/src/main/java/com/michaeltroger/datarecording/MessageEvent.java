package com.michaeltroger.datarecording;

import android.support.annotation.NonNull;

public class MessageEvent {

    public final AppState appState;

    public MessageEvent(@NonNull final AppState appState) {
        this.appState = appState;
    }
}