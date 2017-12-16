package com.michaeltroger.datarecording.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.michaeltroger.sensorvaluelegend.SensorValueLegend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.content.Context.SENSOR_SERVICE;

public class SensorListener implements SensorEventListener {

    private static final String TAG = SensorListener.class.getSimpleName();

    private final Map<String, float[]> cachedSensorValues = new LinkedHashMap<>();
    private final SensorManager sensorManager;
    private final List<String> labels;

    public SensorListener(@NonNull final Context context) throws NoSensorChosenException {
        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);

        labels = new ArrayList<>();

        final Set<Integer> sensorTypes = SensorUtilities.getSensorTypesToRecord(context);

        for (final Integer sensorType : sensorTypes) {
            final Sensor sensor = sensorManager.getDefaultSensor(sensorType);
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
            Log.d(TAG,"registered" + sensor.toString());

            final String[] legend = SensorValueLegend.getLegend(sensor.getType());
            labels.addAll(Arrays.asList(legend));

            final float[] fl = new float[legend.length];
            for (int i = 0; i < fl.length; i++) {
                fl[i] = Float.MIN_VALUE;
            }
            cachedSensorValues.put(sensor.getName(), fl);
        }
    }

    public List<String> getLabels() {
        return labels;
    }

    public Map<String, float[]> getSensorData() {
        return new LinkedHashMap<>(cachedSensorValues);
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
