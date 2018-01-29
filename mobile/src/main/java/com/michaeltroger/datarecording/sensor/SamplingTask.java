package com.michaeltroger.datarecording.sensor;

import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * The sampling task asks with the frequency defined in settings
 * for sensor values and tells another Task to persist the data
 */
public class SamplingTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = SamplingTask.class.getSimpleName();
    /**
     * for converting the sampling interval from the user's chosen Herz
     */
    private static final long ONE_SECOND_IN_NANOS = 1000000000;

    /**
     * To persist the sampled sensor data
     */
    private PersistDataTask persistDataTask;
    /**
     * Listens for and caches sensor values in a higher frequency
     * than (usually) specified by the user
     */
    private SensorListener sensorListener;
    /**
     * The interval in nanoseconds to do the sampling with
     */
    private final long samplingIntervalNanoSeconds;

    /**
     * Creates an instance
     * It directly registers the {@link com.michaeltroger.datarecording.sensor.SensorListener SensorListener}
     * for each of the chosen sensors but doesn't yet start persisting
     * The persisting starts when the task is started via {@link #executeOnExecutor(Executor, Object[])}
     * @param context the context needed for registering sensors and to persist data
     * @throws IOException in case data can't be written
     * @throws NoSensorChosenException in case not even a single sensor was chosen by the user
     */
    public SamplingTask(@NonNull final Context context) throws IOException, NoSensorChosenException {
        final int samplingRateInHerz = SensorUtilities.getSamplingRateInHerz(context);

        samplingIntervalNanoSeconds = Math.round((float)ONE_SECOND_IN_NANOS / samplingRateInHerz);
        sensorListener = new SensorListener(context);
        persistDataTask = new PersistDataTask(context, sensorListener.getLabels());
    }

    @Override
    protected Void doInBackground(Void... voids) {
        long lastSamplingTimeNanos = SystemClock.elapsedRealtimeNanos();

        persistDataTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        while (true) {
            if (isCancelled()) {
                return null;
            }

            final long currentTimeNanos = SystemClock.elapsedRealtimeNanos();
            if (currentTimeNanos < lastSamplingTimeNanos + samplingIntervalNanoSeconds) {
                continue;
            }

            lastSamplingTimeNanos = currentTimeNanos;

            // retrieve the cached sensor data from the listener
            // and pass it on to persist it
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
