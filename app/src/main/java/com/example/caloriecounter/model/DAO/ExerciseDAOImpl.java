package com.example.caloriecounter.model.DAO;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ExerciseDAOImpl implements ExerciseDAO {
    private final DatabaseReference exerciseDatabaseReference;

    public ExerciseDAOImpl() {
        exerciseDatabaseReference = FirebaseDatabase.getInstance().getReference().child("exercises");
    }

    @Override
    public DatabaseReference get() {
        return exerciseDatabaseReference;
    }

    @Override
    public Task<Void> add(Exercise exercise) {
        return null;
    }

    @Override
    public Task<Void> update(Exercise exercise) {
        return null;
    }

    @Override
    public Task<Void> delete() {
        return null;
    }
}
