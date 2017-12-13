package com.michaeltroger.datarecording.sensor;


import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

// TODO: do the actual implementation
public class PersistDataTask extends AsyncTask<Void, Void, Void> {

    private static final String APP_DIRECTORY = "DataRecording";
    private static final String FILE_EXTENSION = ".csv";
    private static final String TAG = PersistDataTask.class.getSimpleName();
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";

    private List<FileWriter> fileWriters;

    private ConcurrentLinkedQueue<float[]> valuesQueue = new ConcurrentLinkedQueue<>();

    public PersistDataTask() throws IOException {
        createFileWriters();
        writeStaticSampleInfosIntoFile();
    }

    private void createFileWriters() throws IOException {
        final File folder = new File( Environment.getExternalStorageDirectory(), APP_DIRECTORY);
        if (!folder.exists()) {
            folder.mkdir();
        }

        final String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();

        List<String> sensors = new ArrayList<>();
        sensors.add("accX");
        sensors.add("accY");

        fileWriters = new ArrayList<>();

        for (final String sensor : sensors) {
            final String fileName = sensor+FILE_EXTENSION;
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
        // TODO: dynamically choose file

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

            final float[] valuesAsFloat = valuesQueue.remove();
            Log.d(TAG, "persisting data");
            for (int i = 0; i < fileWriters.size(); i++) {
                try {
                    fileWriters.get(i).append(COMMA_DELIMITER);
                    fileWriters.get(i).append(String.valueOf(valuesAsFloat[i]));
                } catch (IOException e) {
                    e.printStackTrace();
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

    public void addDataToPersist(@NonNull final float[] values) {
        valuesQueue.add(values);
    }
}
