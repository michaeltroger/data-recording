package com.michaeltroger.settings;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.preference.MultiSelectListPreference;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Custom multi select preference
 * in order to programmatically fill
 * it with available sensors tolet the user
 * choose from
 */
public class SensorListPreference extends MultiSelectListPreference {

    /**
     * How the sensors shall be labeled in this view
     */
    private final List<CharSequence> entries;
    /**
     * The keys with with the sensors are stored in preferences
     */
    private final List<CharSequence> entryValues;

    /**
     * Create an instance of this custom preference
     * it's automatically called by the Android framework
     * no need to call it manually
     * @param context The context to retreive the sensor service from
     * @param attrs The attribute set
     */
    public SensorListPreference(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        entries = new ArrayList<>();
        entryValues = new ArrayList<>();

        final SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);

        // calculates keys and labels of sensors available
        addSensorToList(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        addSensorToList(sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION));
        addSensorToList(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
        addSensorToList(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
        addSensorToList(sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE));

        // sets keys and labels in order to be visible in view
        setEntries(entries.toArray(new CharSequence[]{}));
        setEntryValues(entryValues.toArray(new CharSequence[]{}));
    }

    /**
     * Adds sensors to list in case it is available
     * @param sensor The sensor type to add
     */
    private void addSensorToList(@Nullable final Sensor sensor) {
        if (sensor == null) {
            return;
        }
        entryValues.add(String.valueOf(sensor.getType()));
        entries.add(sensor.getName());
    }
}
