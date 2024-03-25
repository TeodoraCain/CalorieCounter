package com.example.caloriecounter.model.DAO;

import androidx.annotation.NonNull;

public class Workout {
    private String name;
    private int caloriesBurned;
    private int minutes;

    public Workout() {
    }

    public Workout(String name, int minutes, int caloriesBurned) {
        this.name = name;
        this.caloriesBurned = caloriesBurned;
        this.minutes = minutes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(int caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%s\nTime elapsed: %d\nCalories Burned: %d", getName(), minutes, caloriesBurned);
    }
}
