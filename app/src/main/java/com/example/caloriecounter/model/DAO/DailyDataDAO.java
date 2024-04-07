package com.example.caloriecounter.model.DAO;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

public interface DailyDataDAO extends DAO<DailyData> {

    DatabaseReference get(String date);

    Task<Void> add(DailyData dailyData, String date);

    Task<Void> update(DailyData dailyData, String date);

    Task<Void> delete(DailyData dailyData, String date);
}
