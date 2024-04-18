package com.example.caloriecounter.models.dao;

@SuppressWarnings("unused")
public class Exercise {
    private String name;
    private int calories;

    public Exercise() {
        this.name = "";
        this.calories = 0;
    }

    public Exercise(String name, int calories) {
        this.name = name;
        this.calories = calories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }
}
