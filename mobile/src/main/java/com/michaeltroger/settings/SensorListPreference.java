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
        //addSensorToList(sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)); TODO: causes unknownType10 with Legend Lib
        addSensorToList(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
        addSensorToList(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
        addSensorToList(sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE));

        setEntries(entries.toArray(new CharSequence[]{}));
        setEntryValues(entryValues.toArray(new CharSequence[]{}));
        setDefaultValue(String.valueOf(Sensor.TYPE_ACCELEROMETER));
    }

    private void addSensorToList(@Nullable final Sensor sensor) {
        if (sensor == null) {
            return;
        }
        entryValues.add(String.valueOf(sensor.getType()));
        entries.add(sensor.getName());
    }
}
