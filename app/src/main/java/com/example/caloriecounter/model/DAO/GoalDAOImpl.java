package com.example.caloriecounter.model.DAO;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class GoalDAOImpl implements GoalDataDAO{
    private final DatabaseReference goalDatabaseReference;

    public GoalDAOImpl() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String userID = Objects.requireNonNull(firebaseUser).getUid();
        goalDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("goals");
    }

    @Override
    public DatabaseReference get() {
        return goalDatabaseReference;
    }

    @Override
    public Task<Void> add(GoalData goalData) {
        return goalDatabaseReference.setValue(goalData);
    }

    @Override
    public Task<Void> update(GoalData goalData) {
        return goalDatabaseReference.setValue(goalData);
    }

    @Override
    public Task<Void> delete() {
        return goalDatabaseReference.removeValue();
    }
}
