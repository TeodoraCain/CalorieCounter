package com.example.caloriecounter.models.dao;

import com.example.caloriecounter.models.dataModel.DefaultValue;

public class GoalData {
    private String waterIntakeGoal;
    private String calorieGoal;
    private String stepGoal;
    private String weightGoal;
    private String exerciseTimeGoal;

    public GoalData() {
        this.calorieGoal = "";
        this.weightGoal = "";
        this.exerciseTimeGoal = String.valueOf(DefaultValue.EXERCISE_TIME_GOAL);
        this.stepGoal = String.valueOf(DefaultValue.STEP_GOAL);
        this.waterIntakeGoal = String.valueOf(DefaultValue.WATER_GOAL);
    }

    public GoalData(String waterIntakeGoal, String calorieGoal, String stepsDailyGoal, String weightGoal, String exerciseTimeGoal) {
        this.waterIntakeGoal = waterIntakeGoal;
        this.calorieGoal = calorieGoal;
        this.stepGoal = stepsDailyGoal;
        this.weightGoal = weightGoal;
        this.exerciseTimeGoal = exerciseTimeGoal;
    }

    public String getWaterIntakeGoal() {
        return waterIntakeGoal;
    }

    public void setWaterIntakeGoal(String waterIntakeGoal) {
        this.waterIntakeGoal = waterIntakeGoal;
    }

    public String getCalorieGoal() {
        return calorieGoal;
    }

    public void setCalorieGoal(String calorieGoal) {
        this.calorieGoal = calorieGoal;
    }

    public String getStepGoal() {
        return stepGoal;
    }

    public void setStepGoal(String stepGoal) {
        this.stepGoal = stepGoal;
    }

    public String getWeightGoal() {
        return weightGoal;
    }

    public void setWeightGoal(String weightGoal) {
        this.weightGoal = weightGoal;
    }

    public String getExerciseTimeGoal() {
        return exerciseTimeGoal;
    }

    public void setExerciseTimeGoal(String exerciseTimeGoal) {
        this.exerciseTimeGoal = exerciseTimeGoal;
    }
}
