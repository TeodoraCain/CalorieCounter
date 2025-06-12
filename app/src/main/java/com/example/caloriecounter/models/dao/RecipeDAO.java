package com.example.caloriecounter.models.dao;

import com.google.android.gms.tasks.Task;

import java.util.List;

public interface RecipeDAO extends DAO<Recipe>{

    Task<Void> update(List<Recipe> recipes);
}
