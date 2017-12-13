package com.michaeltroger.settings;

import android.content.Context;
import android.preference.MultiSelectListPreference;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

public class SensorListPreference extends MultiSelectListPreference {


    public SensorListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        final List<CharSequence> entries = new ArrayList<>();
        final List<CharSequence> entryValues = new ArrayList<>();

        entries.add("test");
        entries.add("test1");


        entryValues.add("test");
        entryValues.add("test");

        setEntries(entries.toArray(new CharSequence[]{}));
        setEntryValues(entryValues.toArray(new CharSequence[]{}));
    }
}
