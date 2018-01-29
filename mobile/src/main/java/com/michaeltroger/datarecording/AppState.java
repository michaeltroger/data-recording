package com.michaeltroger.datarecording;

/**
 * The current state of the app
 * is represented by this.
 * Used for messaging. E.g. the
 * {@link com.michaeltroger.datarecording.MainActivity MainActivity}
 * will receive a state change notification when the
 * recording is started/stopped
 */
public enum AppState {
    STANDBY, RECORDING
}
