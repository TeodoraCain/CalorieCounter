package com.example.caloriecounter.model.DAO;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RecipeDAOImpl implements RecipeDAO {
    private final DatabaseReference recipesDatabaseReference;

    public RecipeDAOImpl() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String userID = Objects.requireNonNull(firebaseUser).getUid();
        this.recipesDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("recipes");
    }

    @Override
    public DatabaseReference get() {
        return recipesDatabaseReference;
    }

    @Override
    public Task<Void> add(Recipe recipe) {
        return recipesDatabaseReference.setValue(recipe);
    }

    @Override
    public Task<Void> update(Recipe recipe) {
        return recipesDatabaseReference.setValue(recipe);
    }

    @Override
    public Task<Void> delete() {
        return recipesDatabaseReference.removeValue();
    }
}
