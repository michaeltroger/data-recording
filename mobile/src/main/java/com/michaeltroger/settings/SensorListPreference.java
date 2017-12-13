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

public class SensorListPreference extends MultiSelectListPreference {


    private final SensorManager sensorManager;
    final List<CharSequence> entries;
    final List<CharSequence> entryValues;

    public SensorListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        entries = new ArrayList<>();
        entryValues = new ArrayList<>();

        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);

        addSensorToList(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        addSensorToList(sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION));
        addSensorToList(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
        addSensorToList(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
        addSensorToList(sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE));
        
        setEntries(entries.toArray(new CharSequence[]{}));
        setEntryValues(entryValues.toArray(new CharSequence[]{}));
    }

    private void addSensorToList(@Nullable final Sensor sensor) {
        if (sensor != null) {
            entryValues.add(sensor.getStringType());
            entries.add(sensor.getName());
        }
    }
}
