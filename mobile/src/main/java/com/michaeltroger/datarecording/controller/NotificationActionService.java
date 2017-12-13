package com.michaeltroger.datarecording.controller;


import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.michaeltroger.datarecording.sensor.SensorUtilities;


public class NotificationActionService extends IntentService {
    private static final String TAG = NotificationActionService.class.getSimpleName();

    public static final String NOTIFICATION_ACTION = "command";
    public static final String NOTIFICATION_STOP_COMMAND = "stop";

    public NotificationActionService() {
        super("NotificationAction");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        final String command = intent.getStringExtra(NOTIFICATION_ACTION);
        Log.d(TAG, "Received command "+ command +" from notification action");

        switch (command) {
            case NOTIFICATION_STOP_COMMAND:
                SensorUtilities.stopRecording(getApplicationContext());
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

}
