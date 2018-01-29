package com.michaeltroger.datarecording.controller;

import android.view.View;

import com.michaeltroger.datarecording.sensor.SensorUtilities;

/**
 * Handles clicks on the view
 */
public class ClickHandlers {

    /**
     * Handles a start button click
     * by telling the {@link com.michaeltroger.datarecording.sensor.RecordingService RecordingService}
     * to start recording
     * @param view The button, here only used to get its context
     */
    public void start(final View view) {
        SensorUtilities.startRecording(view.getContext());
    }

    /**
     * Handles a stop button click
     * by telling the {@link com.michaeltroger.datarecording.sensor.RecordingService RecordingService}
     * to stop recording
     * @param view The button, here only used to get its context
     */
    public void stop(final View view) {
        SensorUtilities.stopRecording(view.getContext());
    }

}
