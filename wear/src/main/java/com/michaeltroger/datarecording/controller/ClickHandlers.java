package com.michaeltroger.datarecording.controller;

import android.support.annotation.NonNull;
import android.view.View;

import com.michaeltroger.datarecording.IView;

public class ClickHandlers {
    private static final String START_COMMAND = "start";
    private static final String STOP_COMMAND = "stop";

    private final IView myView;

    public ClickHandlers(@NonNull final IView view) {
        this.myView = view;
    }

    public void start(@NonNull final View view) {
        myView.sendCommand(START_COMMAND);
    }

    public void stop(@NonNull final View view) {
        myView.sendCommand(STOP_COMMAND);
    }
}
