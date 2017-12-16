package com.michaeltroger.datarecording.sensor;

import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class SamplingTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = SamplingTask.class.getSimpleName();
    private static final long ONE_SECOND_IN_NANOS = 1000000000;

    private PersistDataTask persistDataTask;
    private SensorListener sensorListener;
    private final long samplingIntervalNanoSeconds;

    public SamplingTask(@NonNull final Context context) throws IOException {
        final int samplingRateInHerz = SensorUtilities.getSamplingRateInHerz(context);

        samplingIntervalNanoSeconds = Math.round((float)ONE_SECOND_IN_NANOS / samplingRateInHerz);
        sensorListener = new SensorListener(context);
        final List<String> labels = sensorListener.getLabels();


        persistDataTask = new PersistDataTask(labels);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        // TODO: better variable naming
        final long startTime = SystemClock.elapsedRealtimeNanos();
        long startTimeNanos = SystemClock.elapsedRealtimeNanos();

        persistDataTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        while (true) {
            if (isCancelled()) {
                return null;
            }

            final long currentTimeNanos = SystemClock.elapsedRealtimeNanos();
            if (currentTimeNanos < startTimeNanos + samplingIntervalNanoSeconds) {
                continue;
            }

            startTimeNanos = currentTimeNanos;
            final float seconds = (currentTimeNanos - startTime) / (float)ONE_SECOND_IN_NANOS;

            final Map<String, float[]> sensorData = sensorListener.getSensorData();
            persistDataTask.addDataToPersist(sensorData);
        }
    }

    @Override
    protected void onCancelled() {
        persistDataTask.cancel(true);
        sensorListener.cancel();
        super.onCancelled();
    }
}
