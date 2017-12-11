package com.michaeltroger.datarecording.commands;

import android.view.View;

import com.michaeltroger.datarecording.sensor.SensorUtilities;

public class ClickHandlers {

    public void start(final View view) {
        SensorUtilities.startRecording(view.getContext());
    }

    public void stop(final View view) {
        SensorUtilities.stopRecording(view.getContext());
    }

}
