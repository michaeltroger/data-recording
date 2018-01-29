package com.michaeltroger.datarecording.sensor;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.michaeltroger.settings.MetaDataPreferenceUtilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

/**
 * Persists data continuously.
 * new data can be added to the Queue on runtime
 */
public class PersistDataTask extends AsyncTask<Void, Void, Void> {

    /**
     * The directory to save the recordings in. It's located in the root
     */
    private static final String APP_DIRECTORY = "DataRecording";
    /**
     * The recommended file extension for CSV files
     */
    private static final String FILE_EXTENSION = ".csv";
    private static final String TAG = PersistDataTask.class.getSimpleName();
    /**
     * How entries in the CSV are delimited
     */
    private static final String COMMA_DELIMITER = ",";
    /**
     * How a new line shall be represented in the CSV file
     */
    private static final String NEW_LINE_SEPARATOR = "\n";
    /**
     * The labels of the sensors are used to create
     * the corresponding file names
     */
    private final List<String> labels;

    /**
     * There is one file for each sensor and each axis
     * e.g. accX, accY, gyrX
     */
    private List<FileWriter> fileWriters;

    /**
     * A queue holding all values to persist. It can be extended on runtime
     */
    private Queue<Map<String,float[]>> valuesQueue = new ConcurrentLinkedQueue<>();

    /**
     * Creates the folders and files if not existent
     * and writes the meta data like class, person already into the CSV files
     * The actual persisting of sensor data starts after launching
     * {@link #executeOnExecutor(Executor, Object[])}
     * @param context the context to retrieve preferences from
     * @param labels the sensor labels to use as file names
     * @throws IOException if writing or creating files was not successfull
     */
    public PersistDataTask(@NonNull final Context context, final List<String> labels) throws IOException {
        this.labels = labels;
        createFileWriters();
        writeSampleMetaDataIntoFile(context);
    }

    /**
     * creates multiple files depending on the sensors
     * chosen by the user
     * @throws IOException in case a file can't be created
     */
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


    /**
     * writes meta data like the class, sample number or person
     * into the CSV files
     * @param context the context to retrieve preferences from
     * @throws IOException in case the writing to the files fails
     */
    private void writeSampleMetaDataIntoFile(@NonNull final Context context) throws IOException {
        final String classLabel = MetaDataPreferenceUtilities.getClassLabel(context);
        final String sampleNr = String.valueOf(MetaDataPreferenceUtilities.getSampleNr(context));
        MetaDataPreferenceUtilities.increaseSampleNr(context);
        final String person = MetaDataPreferenceUtilities.getPerson(context);
        final String location = MetaDataPreferenceUtilities.getLocation(context);
        
        for (final FileWriter fileWriter : fileWriters) {
            fileWriter.append(classLabel);
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(sampleNr);
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(person);
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(location);
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

    /**
     * Add data to the queue which will be persisted later on
     * @param values the sensor values to add
     */
    public void addDataToPersist(@NonNull final Map<String, float[]> values) {
        valuesQueue.add(values);
    }
}
