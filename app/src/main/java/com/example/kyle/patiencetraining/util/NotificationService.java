package com.example.kyle.patiencetraining.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;

import com.example.kyle.patiencetraining.reward.locked.LockedFragment;
import com.example.kyle.patiencetraining.main.MainActivity;
import com.example.kyle.patiencetraining.R;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

public class NotificationService extends JobService {

    /**
     * Add sound
     * Add collect reward option
     *
     */

    private static final String CHANNEL_ID = "REWARD_CHANNEL";
    public static final String REWARD_ID_EXTRA = "reward_id_extra";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Long id = jobParameters.getExtras().getLong(LockedFragment.REWARD_ID_BUNDLE);
        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra(REWARD_ID_EXTRA,id);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        int notificationId = jobParameters.getJobId();
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(notificationId, PendingIntent.FLAG_UPDATE_CURRENT);


        String name = jobParameters.getExtras().getString(LockedFragment.REWARD_NAME_BUNDLE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_unlock)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_text, name))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setSound(getNotificationSound());
        createNotificationChannel();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, mBuilder.build());
        return false;
    }

    private Uri getNotificationSound(){
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        Uri notificationUri = Uri.parse(sharedPref.getString(getString(R.string.notification_uri_key),""));
        return notificationUri;
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
            if(notificationManager != null)
                notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
