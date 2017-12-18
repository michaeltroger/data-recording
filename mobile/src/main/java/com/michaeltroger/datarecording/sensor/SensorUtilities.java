package com.michaeltroger.datarecording.sensor;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.michaeltroger.datarecording.R;

import java.util.HashSet;
import java.util.Set;

import static android.content.Context.SENSOR_SERVICE;

public class SensorUtilities {

    public static void startRecording(@NonNull final Context context) {
        if (!hasWriteExternalStoragePermission(context)) {
            Toast.makeText(context, R.string.error_no_filesystem_permission, Toast.LENGTH_SHORT).show();
            return;
        }

        if (isRecordingActive(context)) {
            Toast.makeText(context, R.string.error_recording_already_active, Toast.LENGTH_SHORT).show();
            return;
        }

        final Intent intent =  new Intent(context, RecordingService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static void stopRecording(@NonNull final Context context) {
        if (!isRecordingActive(context)) {
            return;
        }

        final Intent intent =  new Intent(context, RecordingService.class);
        context.stopService(intent);
    }


    public static boolean isRecordingActive(@NonNull final Context context) {
        final ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (final ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (RecordingService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @NonNull
    public static Set<Integer> getSensorTypesToRecord(@NonNull final Context context) throws NoSensorChosenException {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        final Set<String> selections = sharedPref.getStringSet(context.getString(R.string.pref_key_sensor_list), new HashSet<>());
        if (selections.isEmpty()) {
            throw new NoSensorChosenException();
        }

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

    public static int getSamplingRateInHerz(@NonNull final Context context) {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        final String samplingRate = sharedPref.getString(
                context.getString(R.string.pref_key_sampling_rate),
                context.getString(R.string.pref_default_sampling_rate)
        );

        return Integer.valueOf(samplingRate);
    }

    public static boolean hasWriteExternalStoragePermission(@NonNull final Context context) {
        return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;
    }

}
