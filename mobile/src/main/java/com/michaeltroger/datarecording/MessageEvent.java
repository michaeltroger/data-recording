package com.michaeltroger.datarecording;

import android.support.annotation.NonNull;

/**
 * Used for sending change of app state events
 * across different components
 */
public class MessageEvent {

    /**
     * The updated state of the app
     */
    public final AppState appState;

    /**
     * Creates a MessageEvent by defining the app's state
     * @param appState The app's state, either standby or recording
     */
    public MessageEvent(@NonNull final AppState appState) {
        this.appState = appState;
    }
}