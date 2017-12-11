package com.michaeltroger.datarecording.sensor;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.Build;
import android.support.annotation.NonNull;

import com.michaeltroger.datarecording.sensor.RecordingService;

import java.util.ArrayList;

public class SensorUtilities {

    public static void startRecording(@NonNull final Context context) {
        final ArrayList<Integer> sensorTypes = new ArrayList<>();
        // TODO: receive wished sensor types from UI
        sensorTypes.add(Sensor.TYPE_ACCELEROMETER);

        final Intent intent =  new Intent(context, RecordingService.class);
        intent.putIntegerArrayListExtra("sensorTypes", sensorTypes);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static void stopRecording(@NonNull final Context context) {
        final Intent intent =  new Intent(context, RecordingService.class);
        context.stopService(intent);
    }
}
