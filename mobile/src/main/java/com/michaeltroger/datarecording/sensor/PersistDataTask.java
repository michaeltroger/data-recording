package com.michaeltroger.datarecording.sensor;


import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.michaeltroger.sensorvaluelegend.SensorValueLegend;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import static android.content.Context.SENSOR_SERVICE;

// TODO: do the actual implementation
public class PersistDataTask extends AsyncTask<Void, Void, Void> {

    private static final String APP_DIRECTORY = "DataRecording";
    private static final String FILE_EXTENSION = ".csv";
    private static final String TAG = PersistDataTask.class.getSimpleName();
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";
    private final List<String> labels;

    private List<FileWriter> fileWriters;

    private ConcurrentLinkedQueue<Map<String,float[]>> valuesQueue = new ConcurrentLinkedQueue<>();

    public PersistDataTask(final List<String> labels) throws IOException {
        this.labels = labels;
        createFileWriters();
        writeStaticSampleInfosIntoFile();
    }

    private void createFileWriters() throws IOException {
        final File folder = new File( Environment.getExternalStorageDirectory(), APP_DIRECTORY);
        if (!folder.exists()) {
            folder.mkdir();
        }

        final String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();

        fileWriters = new ArrayList<>();

        for (final String label : labels) {
            Log.d(TAG, label);
            final String fileName = label+FILE_EXTENSION;
            final String filePath = baseDir + File.separator + APP_DIRECTORY + File.separator + fileName;

            final File file = new File(filePath);
            final FileWriter fileWriter;
            if (file.exists()) {
                fileWriter = new FileWriter(filePath, true);
            } else {
                fileWriter = new FileWriter(filePath);
            }

            fileWriters.add(fileWriter);

        }
    }


    private void writeStaticSampleInfosIntoFile() {
        for (final FileWriter fileWriter : fileWriters) {
            try {
                // TODO: get data from user input
                fileWriter.append("room");
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append("martina");
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append("hagenberg");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        while (true) {
            if (isCancelled()) {
                return null;
            }

            if (valuesQueue.isEmpty()) {
                continue;
            }

            final Map<String, float[]> valuesMap = valuesQueue.remove();

            int i = 0;
            for (final Map.Entry<String, float[]> entries : valuesMap.entrySet()) {
                for (final float f : entries.getValue()) {
                    try {
                        if (f == 0) { // if Sensor.TYPE_PRESSURE the float[] will have size 3 but only first index has value -> ignore the rest
                            continue;
                        } else if (f == Float.MIN_VALUE) { // if we didn't receive sensor data yet
                            fileWriters.get(i).append(COMMA_DELIMITER);
                            fileWriters.get(i).append("");
                        } else {
                            fileWriters.get(i).append(COMMA_DELIMITER);
                            fileWriters.get(i).append(String.valueOf(f));
                        }

                        i++;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    @Override
    protected void onCancelled() {
        try {
            for (final FileWriter fileWriter : fileWriters) {
                fileWriter.append(NEW_LINE_SEPARATOR);
                fileWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onCancelled();
    }

    public void addDataToPersist(@NonNull final Map<String, float[]> values) {
        valuesQueue.add(values);
    }
}
