package com.cs180.simplenote.simplenoteapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import static android.content.Context.NOTIFICATION_SERVICE;

public class ReminderReceiver extends BroadcastReceiver {

    public static final String CHANNEL_ID = "28848";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Bundle bundle = intent.getExtras();
        String noteTitle = bundle.getString("title");
        //Toast.makeText(context, "Title: " + noteTitle, Toast.LENGTH_LONG).show();

        NotificationManager notifManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        Intent tempIntent = new Intent(context, MainActivity.class);
        tempIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 100, tempIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.document_icon)
                .setContentTitle("SimpleNote Reminder")
                .setContentText(noteTitle)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "SimpleNote";
            String description = "Notification channel for SimpleNote";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            builder.setChannelId(CHANNEL_ID);
            notifManager.createNotificationChannel(channel);
        }
        notifManager.notify(100, builder.build());

    }
}
