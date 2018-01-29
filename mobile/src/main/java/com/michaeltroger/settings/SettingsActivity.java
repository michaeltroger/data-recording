package com.michaeltroger.settings;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * The only purpose is to display
 * the {@link com.michaeltroger.settings.SettingsFragment SettingsFragment}
 */
public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
