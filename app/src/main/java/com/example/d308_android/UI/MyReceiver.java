package com.example.d308_android.UI;




import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.d308_android.R;

import static android.content.Context.NOTIFICATION_SERVICE;

import java.util.UUID;

public class MyReceiver extends BroadcastReceiver {

    String channel_id = "test";
    static int notificationID;

    @Override
    public void onReceive(Context context, Intent intent) {

        String key = intent.getStringExtra("key");
        String key2 = intent.getStringExtra("key2");
        String key3 = intent.getStringExtra("key3");


        boolean isKeyNotification = key != null;
        boolean isKey2Notification = key2 != null;
        boolean isKey3Notification = key3 != null;

        String contentText = null;

        if (isKeyNotification) {
            contentText = key;
        } else if (isKey2Notification) {
            contentText = key2;
        } else if (isKey3Notification) {
            contentText = key3;
        } else {
            Log.e("MyReceiver", "No key found in the intent. Unable to determine notification type.");
            return;
        }

        Toast.makeText(context, contentText, Toast.LENGTH_LONG).show();
        createNotificationChannel(context, channel_id);

        Notification n=new NotificationCompat.Builder(context, channel_id)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("NOTIFICATION")
                .setContentText(contentText)
                .build();

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

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager=context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
    private String generateRandomChannelId() {
        return UUID.randomUUID().toString();
    }
}
