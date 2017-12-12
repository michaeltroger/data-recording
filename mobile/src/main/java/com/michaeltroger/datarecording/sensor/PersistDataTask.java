package com.michaeltroger.datarecording.sensor;


import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedQueue;

// TODO: do the actual implementation
public class PersistDataTask extends AsyncTask<Void, Void, Void> {

    private static final String APP_DIRECTORY = "DataRecording";
    private static final String TAG =PersistDataTask.class.getSimpleName();

    private ConcurrentLinkedQueue<float[]> valuesQueue = new ConcurrentLinkedQueue<>();
    private final FileWriter fileWriter;

    public PersistDataTask() throws IOException {
        final String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        final String fileName = "test.csv"; // TODO: dynamically choose file
        final String filePath = baseDir + File.separator + APP_DIRECTORY + File.separator + fileName;

        final File folder = new File( Environment.getExternalStorageDirectory(), APP_DIRECTORY);
        if (!folder.exists()) {
            folder.mkdir();
        }

        final File file = new File(filePath);
        if (file.exists()) {
            fileWriter = new FileWriter(filePath, true);
        } else {
            fileWriter = new FileWriter(filePath);
        }

    }

    @Override
    protected Void doInBackground(Void... voids) {
         final CSVWriter csvWriter = new CSVWriter(
                fileWriter,
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);

        while (true) {
            if (isCancelled()) {
                return null;
            }

            if (valuesQueue.isEmpty()) {
                continue;
            }

            final float[] valuesAsFloat = valuesQueue.remove();
            final String[] valuesAsString = new String[3];
            for (int i = 0; i < valuesAsFloat.length; i++) {
                valuesAsString[i] = String.valueOf(valuesAsFloat[i]);
            }
            Log.d(TAG, "persisting data");
            csvWriter.writeNext(valuesAsString);
        }
    }

    @Override
    protected void onCancelled() {
        try {
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onCancelled();
    }

    public void addDataToPersist(@NonNull final float[] values) {
        valuesQueue.add(values);
    }
}
