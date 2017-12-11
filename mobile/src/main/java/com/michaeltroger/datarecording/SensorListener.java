package com.michaeltroger.datarecording;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import static android.content.Context.SENSOR_SERVICE;

public class SensorListener implements SensorEventListener {

    private static final String TAG = SensorListener.class.getSimpleName();

    private final SensorManager sensorManager;

    public SensorListener(@NonNull final Context context, @NonNull final List<Integer> sensorTypes ) {
        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        for (final Integer sensorType : sensorTypes) {
            final Sensor sensor = sensorManager.getDefaultSensor(sensorType);
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    public void cancel() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(final SensorEvent sensorEvent) {
        Log.d(TAG,"Sensor data arrived from "+ sensorEvent.sensor.getName());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}
}
