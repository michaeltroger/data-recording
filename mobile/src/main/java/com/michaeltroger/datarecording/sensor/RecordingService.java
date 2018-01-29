package com.michaeltroger.datarecording.sensor;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.michaeltroger.datarecording.AppState;
import com.michaeltroger.datarecording.MessageEvent;
import com.michaeltroger.datarecording.controller.NotificationActionService;
import com.michaeltroger.datarecording.R;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

/**
 * Core responsibility for recording and stopping the recording
 */
public class RecordingService extends Service {
    /**
     * The id with which the recording notification is associated
     */
    private static final int NOTIFICATION_ID = 101;
    /**
     * The title of the recording notification
     */
    private static final String NOTIFICATION_TITLE = "Recording data";
    /**
     * The title of the stop recording action in the notification
     */
    private static final String NOTIFICATION_STOP_TITLE = "Stop";

    /**
     * The channel ID is needed on Android Oreo and higher
     */
    private static final String CHANNEL_ID = "com.michaeltroger.datarecording.DATARECORDING";
    /**
     * The channel name is needed on Android Oreo and higher
     */
    private static final String CHANNEL_NAME = "Data recording";

    /**
     * A reference to the notification manager in order to cancel
     * the notification when recording is done
     */
    private NotificationManager notificationManager;
    /**
     * The sampling task asks with the frequency defined in settings
     * for sensor values and tells another Task to persist the data
     */
    private AsyncTask<Void, Void, Void> samplingTask;
    /**
     * The sound to play when recording starts
     */
    private MediaPlayer startSound;

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        EventBus.getDefault().post(new MessageEvent(AppState.RECORDING));
        startSound = MediaPlayer.create(this, R.raw.start);
        startSound.start();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        startInForeground();
        try {
            samplingTask = new SamplingTask(this);
            samplingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (NoSensorChosenException e) {
            final Handler mHandler = new Handler(getMainLooper());
            mHandler.post(() -> Toast.makeText(getApplicationContext(), R.string.error_no_sensor, Toast.LENGTH_SHORT).show());
            stopSelf();
        } catch (IOException e) {
            final Handler mHandler = new Handler(getMainLooper());
            mHandler.post(() -> Toast.makeText(getApplicationContext(), R.string.error_filesystem, Toast.LENGTH_SHORT).show());
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    /**
     * We start the service as a foreground service
     * i.e. with an ongoing notification.
     * The notification gives the user also more interaction possibilities
     */
    private void startInForeground() {
        final String channelId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel();
        } else {
            channelId = null;
        }

        final Intent stopRecordingIntent = new Intent(this, NotificationActionService.class);
        stopRecordingIntent.putExtra(NotificationActionService.NOTIFICATION_ACTION, NotificationActionService.NOTIFICATION_STOP_COMMAND);

        final PendingIntent stopRecordingPendingIntent = PendingIntent.getService(this,
                0,
                stopRecordingIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        final Notification notification = notificationBuilder
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_reorder_white_24px)
                .setContentTitle(NOTIFICATION_TITLE)
                .addAction(R.drawable.ic_launcher_foreground, NOTIFICATION_STOP_TITLE, stopRecordingPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    /**
     * Create a notification channel when Android oreo is used
     * @return The created channel's ID
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel() {
        final NotificationChannel notificationChannel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
        );
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        notificationManager.createNotificationChannel(notificationChannel);

        return CHANNEL_ID;
    }

    @Override
    public void onDestroy() {
        if (samplingTask != null) {
            samplingTask.cancel(true);
        }
        notificationManager.cancel(NOTIFICATION_ID);

        EventBus.getDefault().post(new MessageEvent(AppState.STANDBY));
        startSound.release(); // not releasing could lead to a memory leak

        // The sound to play when the recording stops
        final MediaPlayer endSound = MediaPlayer.create(this, R.raw.end);
        endSound.start();
        endSound.setOnCompletionListener(MediaPlayer::release); // not releasing could lead to a memory leak

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
