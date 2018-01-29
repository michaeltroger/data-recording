package com.michaeltroger.settings;


import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.michaeltroger.datarecording.R;

/**
 * Default preference fragment with custom
 * preference element injected
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}