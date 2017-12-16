package com.michaeltroger.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.michaeltroger.datarecording.R;

public class MetaDataPreferenceUtilities {

    @NonNull
    public static String getLocation(@NonNull final Context context) {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(context.getString(R.string.pref_key_location),"");
    }

    @NonNull
    public static String getPerson(@NonNull final Context context) {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(context.getString(R.string.pref_key_person),"");
    }

    @NonNull
    public static String getClassLabel(@NonNull final Context context) {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(context.getString(R.string.pref_key_class_label),"");
    }

    public static int getSampleNr(@NonNull final Context context) {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        final int lastSampleNr = sharedPref.getInt(
                context.getString(R.string.pref_key_sampling_number),
                0
        );

        final int sampleNr = lastSampleNr + 1;
        sharedPref.edit().putInt(
                context.getString(R.string.pref_key_sampling_number),
                sampleNr
        ).apply();

        return sampleNr;
    }
}
