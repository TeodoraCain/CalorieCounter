package com.example.caloriecounter.model.DAO;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DailyDataDAOImpl implements DailyDataDAO {
    private final DatabaseReference dailyDataDatabaseReference;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseUser firebaseUser;

    public DailyDataDAOImpl() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        String userID = firebaseUser.getUid();
        dailyDataDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("daily_data").child(getCurrentDate());
    }

    private String getCurrentDate() {
//        Date currentDate = new Date();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//
//        return dateFormat.format(currentDate);
        return new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
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
}
