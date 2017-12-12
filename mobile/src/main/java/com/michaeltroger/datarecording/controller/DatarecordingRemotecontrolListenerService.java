package com.michaeltroger.datarecording.controller;

import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.michaeltroger.datarecording.sensor.SensorUtilities;

public class DatarecordingRemotecontrolListenerService extends WearableListenerService {

    private static final String TAG = DatarecordingRemotecontrolListenerService.class.getSimpleName();

    private static final String START_COMMAND = "start";
    private static final String STOP_COMMAND = "stop";

    @Override
    public void onMessageReceived(final MessageEvent messageEvent) {
        final String command = new String(messageEvent.getData());
        Log.d(TAG, "received " + command + " command from wear");

        switch (command) {
            case START_COMMAND:
                SensorUtilities.startRecording(getApplicationContext());
                break;
            case STOP_COMMAND:
                SensorUtilities.stopRecording(getApplicationContext());
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }
}
