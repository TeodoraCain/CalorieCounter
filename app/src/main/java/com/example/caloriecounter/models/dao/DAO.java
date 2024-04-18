package com.example.caloriecounter.models.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

@SuppressWarnings("unused")
public interface DAO<T> {
    DatabaseReference get();

    Task<Void> add(T t);

    Task<Void> update(T t);

    Task<Void> delete();
}
