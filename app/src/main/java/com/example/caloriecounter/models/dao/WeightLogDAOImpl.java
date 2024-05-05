package com.example.caloriecounter.models.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

@SuppressWarnings("unused")
public class WeightLogDAOImpl implements WeightLogDAO {
    private final DatabaseReference weightLogDatabaseReference;

    public WeightLogDAOImpl() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String userID = Objects.requireNonNull(firebaseUser).getUid();
        weightLogDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("weight_log");
    }

    @Override
    public DatabaseReference get() {
        return weightLogDatabaseReference;
    }

    @Override
    public Task<Void> add(WeightLog weight) {
        return weightLogDatabaseReference.child(weight.getDate()).setValue(weight);
    }

    @Override
    public Task<Void> update(WeightLog weight) {
        return weightLogDatabaseReference.child(weight.getDate()).setValue(weight);
    }

    @Override
    public Task<Void> delete() {
        return null;
    }

}
