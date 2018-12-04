package com.example.kyle.patiencetraining;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.util.Log;

import java.util.Objects;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationService extends JobService {
    /**
     * Todo Make the main activity 'refresh' so the reward is in the right place when notification is clicked
     */
    private static final String CHANNEL_ID = "REWARD_CHANNEL";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        String name = jobParameters.getExtras().getString(MainActivity.REWARD_NAME_BUNDLE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_unlock)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_text, name))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        createNotificationChannel();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        int notificationId = jobParameters.getJobId();
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, mBuilder.build());
        return false;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
