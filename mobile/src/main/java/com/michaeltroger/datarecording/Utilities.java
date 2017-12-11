package com.michaeltroger.datarecording;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.Build;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Utilities {

    public static void startRecording(@NonNull final Context context) {
        final ArrayList<Integer> sensorTypes = new ArrayList<>();
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
