package com.michaeltroger.datarecording.sensor;


import android.os.AsyncTask;
import android.support.annotation.NonNull;

// TODO: do the actual implementation
public class PersistDataTask extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... voids) {
        while (true) {
            if (isCancelled()) {
                return null;
            }

        }
    }

    public void addDataToPersist(@NonNull final float[] values) {

    }
}
