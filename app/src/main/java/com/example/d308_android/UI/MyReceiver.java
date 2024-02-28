package com.example.d308_android.UI;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.d308_android.R;
/*
public class MyReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "mychannelname";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("VACATION_ALERT")) {
            String title = intent.getStringExtra("title");
            String message = intent.getStringExtra("message");
            showNotification(context, title, message);
        } else {
            String channel_id = intent.getStringExtra("channel_id");
            String key = intent.getStringExtra("key");
            Toast.makeText(context, key, Toast.LENGTH_LONG).show();

            if (channel_id == null || channel_id.isEmpty()) {
                channel_id = generateRandomChannelId();
            }

            createNotificationChannel(context, CHANNEL_ID);

            Notification n = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentText(key)
                    .setContentTitle("NotificationTest").build();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, n);

            long startTimeInMillis = intent.getLongExtra("start_time", 0);
            long endTimeInMillis = intent.getLongExtra("end_time", 0);
            String vacationTitle = intent.getStringExtra("vacation_title");

            setVacationAlert(context, vacationTitle, startTimeInMillis, endTimeInMillis);
        }
    }

    private void createNotificationChannel(Context context, String CHANNEL_ID) {
        if (CHANNEL_ID == null || CHANNEL_ID.isEmpty()) {
            CHANNEL_ID = generateRandomChannelId();
        }
        CharSequence name = "mychannelname";
        String description = "mychanneldescription";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private void showNotification(Context context, String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    private String generateRandomChannelId() {
        return "channel_" + System.currentTimeMillis();
    }

    public static void setVacationAlert(Context context, String title, long startTimeMillis, long endTimeMillis) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent startIntent = new Intent(context, MyReceiver.class);
        startIntent.setAction("VACATION_ALERT");
        startIntent.putExtra("title", title);
        startIntent.putExtra("message", "Your vacation '" + title + "' is starting.");
        PendingIntent startPendingIntent = PendingIntent.getBroadcast(context, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, startTimeMillis, startPendingIntent);
        }

        Intent endIntent = new Intent(context, MyReceiver.class);
        endIntent.setAction("VACATION_ALERT");
        endIntent.putExtra("title", title);
        endIntent.putExtra("message", "Your vacation '" + title + "' is ending.");
        PendingIntent endPendingIntent = PendingIntent.getBroadcast(context, 1, endIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, endTimeMillis, endPendingIntent);
        }
    }
}
*/


import static android.content.Context.NOTIFICATION_SERVICE;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;


import com.example.d308_android.R;

import java.util.UUID;

public class MyReceiver extends BroadcastReceiver {

    String channel_id = "test";
    static int notificationID;

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, intent.getStringExtra("key"), Toast.LENGTH_LONG).show();
        createNotificationChannel(context, channel_id);
        Notification n=new NotificationCompat.Builder(context, channel_id)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentText(intent.getStringExtra("key"))
                .setContentTitle("NotificationTest").build();
        NotificationManager notificationManager=(NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(notificationID++,n);

    }


    private void createNotificationChannel(Context context, String CHANNEL_ID) {
        if (CHANNEL_ID == null || CHANNEL_ID.isEmpty()) {
            CHANNEL_ID = generateRandomChannelId();
        }
        CharSequence name ="mychannelname";
        String description="mychanneldescription";
        int importance= NotificationManager.IMPORTANCE_DEFAULT;
        //CharSequence name = context.getResources().getString(R.string.channel_name);
        //String description = context.getString(R.string.channel_description);
        //int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager=context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
    private String generateRandomChannelId() {
        return UUID.randomUUID().toString();
    }
}

