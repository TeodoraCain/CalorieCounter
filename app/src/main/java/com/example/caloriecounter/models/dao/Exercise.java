package com.example.caloriecounter.models.dao;

@SuppressWarnings("unused")
public class Exercise {
    private String name;
    private Object calories;

    public Exercise() {
        this.name = "";
        this.calories = 0;
    }

    public Exercise(String name, Object calories) {
        this.name = name;
        this.calories = calories;
    }

    public Exercise(String name, String calories) {
        this.name = name;
        this.calories = Integer.parseInt(calories);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCalories(Object calories) {
        this.calories = calories;
    }

    public int getCalories() {
        if (calories instanceof Number) {
            return ((Number) calories).intValue();
        } else if (calories instanceof String) {
            try {
                return Integer.parseInt((String) calories);
            } catch (NumberFormatException e) {
                return 0;
            }
        } else {
            return 0;
        }
    }
}
