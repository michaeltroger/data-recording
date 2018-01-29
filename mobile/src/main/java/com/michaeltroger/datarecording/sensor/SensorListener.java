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

/**
 * Listens for Sensor events and caches the values.
 * Provides an interface for retreiving the cached values
 * at any wished time
 */
public class SensorListener implements SensorEventListener {

    private static final String TAG = SensorListener.class.getSimpleName();

    /**
     * The cached sensor values. Includes one value from each sensor/axis.
     * They are in the exact same order as the are in  {@link #labels}
     */
    private final Map<String, float[]> cachedSensorValues = new LinkedHashMap<>();
    /**
     * To retrieve the Sensor service from
     */
    private final SensorManager sensorManager;
    /**
     * The labels are in the exact same order as they are in {@link #cachedSensorValues}
     */
    private final List<String> labels;

    public SensorListener(@NonNull final Context context) throws NoSensorChosenException {
        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);

        labels = new ArrayList<>();

        final Set<Integer> sensorTypes = SensorUtilities.getSensorTypesToRecord(context);

        for (final Integer sensorType : sensorTypes) {
            final Sensor sensor = sensorManager.getDefaultSensor(sensorType);
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
            Log.d(TAG,"registered" + sensor.toString());

            final String[] legend = SensorValueLegend.getDescriptionsShort(sensor.getType());
            labels.addAll(Arrays.asList(legend));

            // Initialize hash map, in order to keep labels and hash map in the same order
            final float[] fl = new float[legend.length];
            for (int i = 0; i < fl.length; i++) {
                fl[i] = Float.MIN_VALUE;
            }
            cachedSensorValues.put(sensor.getName(), fl);
        }
    }

    /**
     * Provides the sensor labels
     * @return the sensor labels as a list of strings
     */
    public List<String> getLabels() {
        return labels;
    }

    /**
     * The current cached sensor data available at any point in time
     * to external components
     * @return the cached sensor values
     */
    public Map<String, float[]> getSensorData() {
        return new LinkedHashMap<>(cachedSensorValues);
    }

    /**
     * Can be called to stop listening for sensor events
     */
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
