package com.example.caloriecounter.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.caloriecounter.R;
import com.example.caloriecounter.SplashActivity;
import com.example.caloriecounter.models.dao.GoalDAOImpl;
import com.example.caloriecounter.models.dao.GoalData;
import com.example.caloriecounter.models.dao.GoalDataDAO;
import com.example.caloriecounter.models.dataModel.DefaultValue;
import com.example.caloriecounter.utils.UserUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StepService extends Service implements SensorEventListener {
    private static final String TAG = "StepService";
    private static final String CHANNEL_ID = "StepServiceChannel";
    private SensorManager sensorManager;
    private boolean isReading = false;
    private int stepGoal = DefaultValue.STEP_GOAL;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Steps counter")
                .setContentText("Service is running in the background")
                .setSmallIcon(R.drawable.ic_app_logo)
                .build();
        startForeground(1, notification);
        retrieveStepGoalFromDB();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            if (stepSensor != null) {
                sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
                isReading = true;
            } else {
                stopSelf();
            }
        }
    }

    private void retrieveStepGoalFromDB() {
        GoalDataDAO goalDataDAO = new GoalDAOImpl();
        goalDataDAO.get().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GoalData goalData = snapshot.getValue(GoalData.class);
                if (goalData == null) {
                    goalData = new GoalData();
                }
                stepGoal = Integer.parseInt(goalData.getStepGoal());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!isReading || event.sensor.getType() != Sensor.TYPE_STEP_COUNTER) return;

        int currentSteps = (int) event.values[0];

        String date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        SharedPreferences prefs = getSharedPreferences("step_prefs", MODE_PRIVATE);

        if (!prefs.contains("initial_steps_" + date)) {
            prefs.edit().putInt("initial_steps_" + date, currentSteps).apply();
        }

        int initialSteps = prefs.getInt("initial_steps_" + date, currentSteps);
        int todaySteps = currentSteps - initialSteps;

        // saving steps to SharedPreferences
        prefs.edit().putInt("today_steps_" + UserUtils.getFirebaseUID(), todaySteps).apply();
        Log.d(TAG, "Today steps: " + todaySteps);

        boolean notified = prefs.getBoolean("notified_" + date, false);
        Log.d(TAG, "Notified: " + notified);

        SharedPreferences notificationPrefs = getSharedPreferences("notification_prefs", MODE_PRIVATE);
        String STEP_REMINDER = "step_reminder";
        boolean isReminderOn = notificationPrefs.getBoolean(STEP_REMINDER, false);

        // Notification for target reached
        if (todaySteps >= stepGoal && !notified && isReminderOn) {
            sendStepGoalNotification();
            prefs.edit().putBoolean("notified_" + date, true).apply();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onDestroy() {
        if (isReading) {
            sensorManager.unregisterListener(this);
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Step Count",
                NotificationManager.IMPORTANCE_HIGH
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) manager.createNotificationChannel(serviceChannel);
    }

    private void sendStepGoalNotification() {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_app_logo)
                .setContentTitle("🎉 Congratulations!")
                .setContentText("You reached your step goal for today !")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat.from(this).notify(1001, builder.build());
    }
}
