package com.example.caloriecounter.model.DAO;

import java.util.List;

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

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public void setWorkouts(List<Workout> workouts) {
        this.workouts = workouts;
    }

    public int getWaterDrank() {
        return waterDrank;
    }

    public void setWaterDrank(int waterDrank) {
        this.waterDrank = waterDrank;
    }

    public int getCaloriesConsumed() {
        return caloriesConsumed;
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

    public int getWorkoutCalories(){
        int calories = 0;
        for (Workout workout : workouts) {
            calories+= workout.getCaloriesBurned();
        }
        return calories;
    }

    public int getWorkoutTime(){
        int minutes = 0;
        for (Workout workout : workouts) {
            minutes += workout.getMinutes();
        }
        return minutes;
    }
}
