package com.example.caloriecounter.models.dao;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class Recipe {
    private String name;
    private List<Food> ingredients;
    private double serving_size;
    private double calories;

    public Recipe() {
        this.name = "";
        this.ingredients = new ArrayList<>();
        this.serving_size = 0;
        this.calories = 0;
    }

    public Recipe(String name, List<Food> ingredients, double serving_size, double calories) {
        this.name = name;
        this.ingredients = ingredients;
        this.serving_size = serving_size;
        this.calories = calories;
    }

    public double getServing_size() {
        return serving_size;
    }

    public void setServing_size(double serving_size) {
        this.serving_size = serving_size;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Food> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Food> ingredients) {
        this.ingredients = ingredients;
    }
}
