package com.example.caloriecounter.models.dao;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class DailyData {
    private int waterDrank;
    private int caloriesConsumed; // total calories from food intake
    private int caloriesBurned; // total calories burned through exercise
    private int steps;
    private List<Recipe> breakfast;
    private List<Recipe> lunch;
    private List<Recipe> dinner;
    private List<Recipe> snacks;
    private List<Workout> workouts;

    public DailyData() {
        this.waterDrank = 0;
        this.caloriesConsumed = 0;
        this.caloriesBurned = 0;
        this.steps = 0;
        this.breakfast = new ArrayList<>();
        this.lunch = new ArrayList<>();
        this.dinner = new ArrayList<>();
        this.snacks = new ArrayList<>();
        this.workouts = new ArrayList<>();
    }

    public DailyData(int waterDrank, int caloriesConsumed, int caloriesBurned, int steps, List<Recipe> breakfast, List<Recipe> lunch, List<Recipe> dinner, List<Recipe> snacks, List<Workout> workouts) {
        this.waterDrank = waterDrank;
        this.caloriesConsumed = caloriesConsumed;
        this.caloriesBurned = caloriesBurned;
        this.steps = steps;
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
        this.snacks = snacks;
        this.workouts = workouts;
    }

    public List<Workout> getWorkouts() {
        return workouts;
    }

    public void setWorkouts(List<Workout> workouts) {
        this.workouts = workouts;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getWaterDrank() {
        return waterDrank;
    }

    public void setWaterDrank(int waterDrank) {
        this.waterDrank = waterDrank;
    }

    public int getCaloriesConsumed() {
        int calories = 0;
        if (breakfast != null)
            for (Recipe recipe : breakfast) {
                calories += recipe.getCalories();
            }
        if (lunch != null)
            for (Recipe recipe : lunch) {
                calories += recipe.getCalories();
            }
        if (dinner != null)
            for (Recipe recipe : dinner) {
                calories += recipe.getCalories();
            }
        if (snacks != null)
            for (Recipe recipe : snacks) {
                calories += recipe.getCalories();
            }
        if (workouts != null)
            for (Workout workout : workouts) {
                calories -= workout.getCaloriesBurned();
            }

        return calories;
    }

    public void setCaloriesConsumed(int caloriesConsumed) {
        this.caloriesConsumed = caloriesConsumed;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(int caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public List<Recipe> getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(List<Recipe> breakfast) {
        this.breakfast = breakfast;
    }

    public List<Recipe> getLunch() {
        return lunch;
    }

    public void setLunch(List<Recipe> lunch) {
        this.lunch = lunch;
    }

    public List<Recipe> getDinner() {
        return dinner;
    }

    public void setDinner(List<Recipe> dinner) {
        this.dinner = dinner;
    }

    public List<Recipe> getSnacks() {
        return snacks;
    }

    public void setSnacks(List<Recipe> snacks) {
        this.snacks = snacks;
    }

    public int getWorkoutCalories() {
        int calories = 0;
        if (workouts == null)
            return 0;
        for (Workout workout : workouts) {
            calories += workout.getCaloriesBurned();
        }
        return calories;
    }

    public int getWorkoutTime() {
        int minutes = 0;
        if (workouts == null)
            return 0;
        for (Workout workout : workouts) {
            minutes += workout.getMinutes();
        }
        return minutes;
    }
}
