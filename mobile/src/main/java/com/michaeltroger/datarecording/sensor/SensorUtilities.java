package com.michaeltroger.datarecording.sensor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.michaeltroger.datarecording.R;
import com.michaeltroger.datarecording.sensor.RecordingService;
import com.michaeltroger.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static android.content.Context.SENSOR_SERVICE;

public class SensorUtilities {

    public static void startRecording(@NonNull final Context context) {
        final Intent intent =  new Intent(context, RecordingService.class);

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


    public static Set<Integer> getSensorTypesToRecord(@NonNull final Context context) {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        final Set<String> selections = sharedPref.getStringSet(context.getString(R.string.pref_key_sensor_list), new HashSet<>());
        final Set<Integer> selectedSensorTypes = new HashSet<>();

        final SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);

        for (final String selection : selections) {
            int sensorType = Integer.valueOf(selection);
            if (isSensorStillAvailable(sensorManager, sensorType)) {
                selectedSensorTypes.add(sensorType);
            }
        }
        return selectedSensorTypes;
    }

    private static boolean isSensorStillAvailable(final SensorManager sensorManager, final int sensorType) {
        return sensorManager.getDefaultSensor(sensorType) != null;
    }
}
