package com.michaeltroger.datarecording.controller;

import android.support.annotation.NonNull;
import android.view.View;

import com.michaeltroger.datarecording.messaging.Messaging;

public class ClickHandlers {
    private static final String START_COMMAND = "start";
    private static final String STOP_COMMAND = "stop";

    private final Messaging messaging;

    public ClickHandlers(@NonNull final Messaging messaging) {
        this.messaging = messaging;
    }

    public void start(@NonNull final View view) {
        messaging.sendCommandToMobile(START_COMMAND);
    }

    public void stop(@NonNull final View view) {
        messaging.sendCommandToMobile(STOP_COMMAND);
    }
}
