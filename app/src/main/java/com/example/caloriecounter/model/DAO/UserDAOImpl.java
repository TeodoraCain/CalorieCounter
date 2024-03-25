package com.example.caloriecounter.model.DAO;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserDAOImpl implements UserDAO{
    private final DatabaseReference userDatabaseReference;
    private final FirebaseAuth firebaseAuth ;
    private final FirebaseUser firebaseUser;


    public UserDAOImpl(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        String userID = firebaseUser.getUid();
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
