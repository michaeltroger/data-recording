package com.michaeltroger.datarecording.controller;

import android.support.annotation.NonNull;
import android.view.View;

import com.michaeltroger.datarecording.messaging.Messaging;

/**
 * Handles clicks on the view
 */
public class ClickHandlers {
    /**
     * The command to start the recording with
     */
    private static final String START_COMMAND = "start";
    /**
     * The command to stop the recording with
     */
    private static final String STOP_COMMAND = "stop";

    /**
     * Responsible for sending the actual command
     */
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
