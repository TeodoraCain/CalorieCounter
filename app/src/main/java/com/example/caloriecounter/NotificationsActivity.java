package com.example.caloriecounter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

public class NotificationsActivity extends AppCompatActivity {
    private final String WATER_REMINDER = "water_reminder";
    private final String STEP_REMINDER = "step_reminder";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        setToolbar();
        setUpNotificationSwitches();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setUpNotificationSwitches() {
        SwitchCompat swWaterReminder = findViewById(R.id.swWaterReminder);
        SwitchCompat swStepGoalReminder = findViewById(R.id.swStepGoalReminder);

        boolean isWaterReminderOn = retrieveReminderFromSharedPrefs(WATER_REMINDER);
        swWaterReminder.setChecked(isWaterReminderOn);

        boolean isStepGoalReminderOn = retrieveReminderFromSharedPrefs(STEP_REMINDER);
        swStepGoalReminder.setChecked(isStepGoalReminderOn);

        swWaterReminder.setOnCheckedChangeListener((buttonView, isChecked) -> setReminderToSharedPrefs(WATER_REMINDER, isChecked));
        swStepGoalReminder.setOnCheckedChangeListener((buttonView, isChecked) -> setReminderToSharedPrefs(STEP_REMINDER, isChecked));

    }

    private void setReminderToSharedPrefs(String reminder, boolean isChecked) {
        SharedPreferences prefs = getSharedPreferences("notification_prefs", MODE_PRIVATE);
        prefs.edit().putBoolean(reminder, isChecked).apply();

        Log.i("NotificationsActivity", "Reminder " + reminder + " set to " + isChecked);
    }

    private boolean retrieveReminderFromSharedPrefs(String reminder) {
        SharedPreferences prefs = getSharedPreferences("notification_prefs", MODE_PRIVATE);

        if (!prefs.contains(reminder)) {
            prefs.edit().putBoolean(reminder, false).apply();
        }

        return prefs.getBoolean(reminder, false);
    }

    /********************************* LIFECYCLE OVERRIDES ***********************************************/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}