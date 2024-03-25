package com.example.caloriecounter.model.DAO;

public class Exercise {
    private String name;
    private String calories;

    public Exercise() {
    }

    public Exercise( String name, int calories) {
        this.name = name;
        this.calories = String.valueOf(calories);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCalories() {
        return Integer.parseInt(calories);
    }

    public void setCalories(int calories) {
        this.calories = String.valueOf(calories);
    }
}
