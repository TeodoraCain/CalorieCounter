package com.example.caloriecounter.model.DAO;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FoodDAOImpl implements FoodDAO {
    private final DatabaseReference foodsDatabaseReference;

    public FoodDAOImpl() {
        foodsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("food");
    }

    @Override
    public DatabaseReference get() {
        return foodsDatabaseReference;
    }

    @Override
    public Task<Void> add(Food foodItem) {
        return null;
    }

    @Override
    public Task<Void> update(Food foodItem) {
        return null;
    }

    @Override
    public Task<Void> delete() {
        return null;
    }

}
