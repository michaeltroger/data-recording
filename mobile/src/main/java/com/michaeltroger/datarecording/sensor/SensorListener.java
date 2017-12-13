package com.michaeltroger.datarecording.sensor;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.michaeltroger.settings.SettingsActivity;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static android.content.Context.SENSOR_SERVICE;

public class SensorListener implements SensorEventListener {

    private static final String TAG = SensorListener.class.getSimpleName();

    private final ConcurrentMap<String, float[]> cachedSensorValues = new ConcurrentHashMap<>();
    private final SensorManager sensorManager;

    public SensorListener(@NonNull final Context context) {
        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);

        final Set<Integer> sensorTypes = SensorUtilities.getSensorTypesToRecord(context);

        for (final Integer sensorType : sensorTypes) {
            final Sensor sensor = sensorManager.getDefaultSensor(sensorType);
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
            Log.d(TAG,"registered" + sensor.toString());
        }
    }

    public ConcurrentMap<String, float[]> getSensorData() {
        return cachedSensorValues;
    }

    public void cancel() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(final SensorEvent sensorEvent) {
        cachedSensorValues.put
                (sensorEvent.sensor.getName(),
                Arrays.copyOf(sensorEvent.values, sensorEvent.values.length));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

}
