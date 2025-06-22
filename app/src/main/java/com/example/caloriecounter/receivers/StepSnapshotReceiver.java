package com.example.caloriecounter.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.caloriecounter.services.StepService;

public class StepSnapshotReceiver extends BroadcastReceiver {
    private static final String TAG = "StepSnapshotReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Alarm triggered, starting StepService...");
        Intent serviceIntent = new Intent(context, StepService.class);
        context.startForegroundService(serviceIntent);
    }
}
