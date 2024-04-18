package com.example.caloriecounter.models.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

@SuppressWarnings("unused")
public class DailyDataDAOImpl implements DailyDataDAO {
    private final DatabaseReference dailyDataDatabaseReference;
    private final String userID;

    public DailyDataDAOImpl() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        userID = firebaseUser.getUid();
        dailyDataDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("daily_data").child(getCurrentDate());
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime());
    }

    @Override
    public DatabaseReference get() {
        return dailyDataDatabaseReference;
    }

    @Override
    public Task<Void> add(DailyData dailyData) {
        return dailyDataDatabaseReference.setValue(dailyData);
    }

    @Override
    public Task<Void> update(DailyData dailyData) {
        return dailyDataDatabaseReference.setValue(dailyData);
    }

    @Override
    public Task<Void> delete() {
        return dailyDataDatabaseReference.removeValue();
    }

    @Override
    public DatabaseReference get(String date) {
        return FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("daily_data").child(date);
    }

    @Override
    public Task<Void> add(DailyData dailyData, String date) {
        return FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("daily_data").child(date).setValue(dailyData);
    }

    @Override
    public Task<Void> update(DailyData dailyData, String date) {
        return FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("daily_data").child(date).setValue(dailyData);
    }

    @Override
    public Task<Void> delete(DailyData dailyData, String date) {
        return FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("daily_data").child(date).removeValue();
    }


}
