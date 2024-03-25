package com.example.caloriecounter.model.DAO;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

public interface DAO<T> {
    DatabaseReference get();

    Task<Void> add(T t);

    Task<Void> update(T t);

    Task<Void> delete();
}
