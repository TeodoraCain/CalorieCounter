package com.example.caloriecounter.models.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

@SuppressWarnings("unused")
public class UserDAOImpl implements UserDAO{
    private final DatabaseReference userDatabaseReference;

    public UserDAOImpl(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String userID = Objects.requireNonNull(firebaseUser).getUid();
        userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("user_details");
    }

    @Override
    public DatabaseReference get() {
        return userDatabaseReference;
    }

    @Override
    public Task<Void> add(UserDetails userDetails) {
       return userDatabaseReference.setValue(userDetails);
    }

    @Override
    public Task<Void> update(UserDetails userDetails) {
        return userDatabaseReference.setValue(userDetails);
    }

    @Override
    public Task<Void> delete() {
        return userDatabaseReference.removeValue();
    }
}
