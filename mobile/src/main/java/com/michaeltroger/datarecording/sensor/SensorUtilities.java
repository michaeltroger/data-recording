package com.michaeltroger.datarecording.sensor;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.michaeltroger.datarecording.R;

import java.util.HashSet;
import java.util.Set;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Bundling several Utilities about the sensors
 * the most important task is to start and stop
 * recording by communicating with the
 * {@link com.michaeltroger.datarecording.sensor.RecordingService RecordingService}
 */
public final class SensorUtilities {
    /**
     * This is an Utility class. No need for an instance
     */
    private SensorUtilities() {}

    /**
     * Starts the recording
     * @param context the context with which this should happen
     */
    public static void startRecording(@NonNull final Context context) {
        if (!hasWriteExternalStoragePermission(context)) {
            final Handler mHandler = new Handler(context.getMainLooper());
            mHandler.post(() -> Toast.makeText(context.getApplicationContext(), R.string.error_no_filesystem_permission, Toast.LENGTH_SHORT).show());
            return;
        }

        if (isRecordingActive(context)) {
            final Handler mHandler = new Handler(context.getMainLooper());
            mHandler.post(() -> Toast.makeText(context.getApplicationContext(), R.string.error_recording_already_active, Toast.LENGTH_SHORT).show());
            return;
        }

        final Intent intent =  new Intent(context, RecordingService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    /**
     * Stops the recording
     * @param context the context with which this should happen
     */
    public static void stopRecording(@NonNull final Context context) {
        if (!isRecordingActive(context)) {
            return;
        }

        final Intent intent =  new Intent(context, RecordingService.class);
        context.stopService(intent);
    }

    /**
     * Helper method to check whether recording is already active
     * @param context to retrieve running services
     * @return true if recording already active, false otherwise
     */
    public static boolean isRecordingActive(@NonNull final Context context) {
        final ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (final ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (RecordingService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gives a save (all sensors are still available) list of sensors
     * to use for recording chosen by the users
     * @param context to access preferences and retrieve sensors
     * @return a collection of sensors
     * @throws NoSensorChosenException not even a single sensor chosen by the user
     */
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
            int sensorType = Integer.parseInt(selection);
            if (isSensorStillAvailable(sensorManager, sensorType)) {
                selectedSensorTypes.add(sensorType);
            }
        }
        return selectedSensorTypes;
    }

    /**
     * Checks if the sensor selected in preferences earlier
     * is still available. It could also be the case that
     * a user changed the device and settings have been backed up
     * @param sensorManager to retrieve the sensor
     * @param sensorType the type of sensor to check
     * @return true if the sensor is available, false otherwise
     */
    private static boolean isSensorStillAvailable(final SensorManager sensorManager, final int sensorType) {
        return sensorManager.getDefaultSensor(sensorType) != null;
    }

    /**
     * Retrieves the sampling rate in Herz
     * set by the user, or uses a default one
     * @param context to access the preferences
     * @return The current sampling rate
     */
    public static int getSamplingRateInHerz(@NonNull final Context context) {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        final String samplingRate = sharedPref.getString(
                context.getString(R.string.pref_key_sampling_rate),
                context.getString(R.string.pref_default_sampling_rate)
        );

        return Integer.valueOf(samplingRate);
    }

    /**
     * Checks if the user has given permission to write to the external storage
     * @param context the context to check with
     * @return true if permission is granted, false otherwise
     */
    public static boolean hasWriteExternalStoragePermission(@NonNull final Context context) {
        return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;
    }

}
