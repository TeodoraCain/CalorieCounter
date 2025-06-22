package com.example.caloriecounter.receivers;

import static android.content.Context.MODE_PRIVATE;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.caloriecounter.R;
import com.example.caloriecounter.SplashActivity;

public class WaterReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent openAppIntent = new Intent(context, SplashActivity.class);
        openAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE);

        SharedPreferences notificationPrefs = context.getSharedPreferences("notification_prefs", MODE_PRIVATE);
        String WATER_REMINDER = "water_reminder";
        boolean isReminderOn = notificationPrefs.getBoolean(WATER_REMINDER, false);

        if(isReminderOn) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "WATER_REMINDER_CHANNEL")
                    .setSmallIcon(R.drawable.ic_app_logo)
                    .setContentTitle("Time to drink water!")
                    .setContentText("Stay hydrated. Tap to log your water intake.")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(1001, builder.build());
        }
    }
}
