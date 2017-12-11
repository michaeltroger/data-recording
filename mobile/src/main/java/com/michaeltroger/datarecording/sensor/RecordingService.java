package com.michaeltroger.datarecording.sensor;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.michaeltroger.datarecording.commands.NotificationActionService;
import com.michaeltroger.datarecording.R;

import java.util.List;

public class RecordingService extends Service {
    private static final int NOTIFICATION_ID = 101;
    private static final String NOTIFICATION_TITLE = "Recording data";
    private static final String NOTIFICATION_STOP_TITLE = "Stop";

    private static final String CHANNEL_ID = "com.michaeltroger.datarecording.DATARECORDING";
    private static final String CHANNEL_NAME = "Data recording";

    private NotificationManager notificationManager;
    private AsyncTask<Void, Void, Void> samplingTask;

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        final List<Integer> sensorTypes = intent.getIntegerArrayListExtra("sensorTypes");

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        startInForeground();
        samplingTask = new SamplingTask(this, sensorTypes).execute();

        return START_NOT_STICKY;
    }


    private void startInForeground() {
        String channelId = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel();
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
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(NOTIFICATION_TITLE)
                .addAction(R.drawable.ic_launcher_foreground, NOTIFICATION_STOP_TITLE, stopRecordingPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }

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
        notificationManager.cancel(NOTIFICATION_ID);
        samplingTask.cancel(true);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
