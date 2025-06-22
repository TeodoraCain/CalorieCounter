package com.example.caloriecounter.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.caloriecounter.models.dao.DailyData;
import com.example.caloriecounter.models.dao.DailyDataDAO;
import com.example.caloriecounter.models.dao.DailyDataDAOImpl;
import com.example.caloriecounter.utils.UserUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class StepDataUploadWorker extends Worker {
    private int todaySteps;

    public StepDataUploadWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i("StepUploader", "Uploading step data..");
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("step_prefs", Context.MODE_PRIVATE);
        todaySteps = prefs.getInt("today_steps_"+ UserUtils.getFirebaseUID(), 0);
        if (todaySteps < 0) {
            todaySteps = 0;
        }

        DailyDataDAO dailyDataDAO = new DailyDataDAOImpl();
        dailyDataDAO.get().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DailyData dailyData = snapshot.getValue(DailyData.class);
                if (dailyData == null) {
                    dailyData = new DailyData();
                }
                dailyData.setSteps(todaySteps);

                dailyDataDAO.update(dailyData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UploadWorker", "Firebase error: " + error.getMessage());
            }
        });

        return Result.success();
    }
}

