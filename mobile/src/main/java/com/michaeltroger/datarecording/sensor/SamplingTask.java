package com.michaeltroger.datarecording.sensor;

import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class SamplingTask extends AsyncTask<Void, Void, Void> {

    private static final long SAMPLING_RATE_NANOS = 20000000; // = 50hz
    private static final String TAG = SamplingTask.class.getSimpleName();
    private final Context context;
    private SensorListener sensorListener;

    public SamplingTask(@NonNull final Context context, @NonNull final List<Integer> sensorTypes) {
        this.context = context;
        sensorListener = new SensorListener(context, sensorTypes);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        // TODO: better variable naming
        long startTime = SystemClock.elapsedRealtimeNanos();
        long startTimeNanos = SystemClock.elapsedRealtimeNanos();

        while (true) {
            if (isCancelled()) {
                return null;
            }

            final long currentTimeNanos = SystemClock.elapsedRealtimeNanos();

            if (currentTimeNanos >= startTimeNanos + SAMPLING_RATE_NANOS) {
                startTimeNanos = currentTimeNanos;
                final float seconds = (currentTimeNanos - startTime) / 1000000000f;

                final ConcurrentMap<String, float[]> sensorData = sensorListener.getSensorData();
                for (Map.Entry<String, float[]> entry : sensorData.entrySet()) {
                    final String key = entry.getKey().toString();
                    final float[] values = entry.getValue();
                    Log.d(TAG,"x:"+values[0] + " y:"+values[1] + " z:"+values[2]);
                }
                // TODO: do something with sampled data
            }
        }
    }

    @Override
    protected void onCancelled() {
        sensorListener.cancel();
        super.onCancelled();
    }
}
