package com.michaeltroger.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.michaeltroger.datarecording.R;

/**
 * Utility class for accessing meta data
 * from preferences like the class, the person
 * or the sample number
 */
public final class MetaDataPreferenceUtilities {
    /**
     * Utility class, no instance needed
     */
    private MetaDataPreferenceUtilities(){}

    /**
     * To retrieve the location label entered by the user earlier
     * @param context The context to access preferences from
     * @return The user's input for labeling the location, cached in preferences
     */
    @NonNull
    public static String getLocation(@NonNull final Context context) {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(context.getString(R.string.pref_key_location),"");
    }

    /**
     * To retrieve the person label entered by the user earlier
     * @param context The context to access preferences from
     * @return The user's input for labeling the person, cached in preferences
     */
    @NonNull
    public static String getPerson(@NonNull final Context context) {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(context.getString(R.string.pref_key_person),"");
    }

    /**
     * To retrieve the class label entered by the user earlier
     * @param context The context to access preferences from
     * @return The user's input for class label, cached in preferences
     */
    @NonNull
    public static String getClassLabel(@NonNull final Context context) {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(context.getString(R.string.pref_key_class_label),"");
    }

    /**
     * To retrieve the sample number from preferences
     * @param context The context to access preferences from
     * @return Depending on the app's state the current or next sample number
     */
    public static int getSampleNr(@NonNull final Context context) {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        return Integer.parseInt(sharedPref.getString(
                context.getString(R.string.pref_key_sampling_number),
                "1"
        ));
    }

    /**
     * Increases the sample number in preferences
     * @param context The context to access preferences from
     */
    public static void increaseSampleNr(@NonNull final Context context) {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        final int sampleNr = Integer.parseInt(sharedPref.getString(
                context.getString(R.string.pref_key_sampling_number),
                "1"
        ));

        final int nextSampleNr = sampleNr + 1;
        sharedPref.edit().putString(
                context.getString(R.string.pref_key_sampling_number),
                String.valueOf(nextSampleNr)
        ).apply();
    }
}
