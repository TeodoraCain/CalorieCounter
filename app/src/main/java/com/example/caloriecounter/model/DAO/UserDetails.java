package com.example.caloriecounter.model.DAO;

import java.util.HashMap;

public class UserDetails {

    public String fitnessGoal, activityLevel, height, heightUnit,
            weight, weightUnit, gender, dob, country, bmi, imageUrl;

    public UserDetails() {
    }

    public UserDetails(String fitnessGoal, String activityLevel, String height,
                       String heightUnit, String weight, String weightUnit,
                       String gender, String dob, String country, String imageUrl, String bmi) {
        this.fitnessGoal = fitnessGoal;
        this.activityLevel = activityLevel;
        this.height = height;
        this.heightUnit = heightUnit;
        this.weight = weight;
        this.weightUnit = weightUnit;
        this.gender = gender;
        this.dob = dob;
        this.country = country;
        this.bmi = bmi;
        this.imageUrl = imageUrl;
    }

    public UserDetails(HashMap<String, String> userInputs) {
        this.fitnessGoal = userInputs.get("Fitness Goal");
        this.activityLevel = userInputs.get("Activity Level");
        this.height = userInputs.get("Height");
        this.heightUnit = userInputs.get("Height Unit");
        this.weight = userInputs.get("Weight");
        this.weightUnit = userInputs.get("Weight Unit");
        this.gender = userInputs.get("Gender");
        this.dob = userInputs.get("DOB");
        this.country = userInputs.get("Country");
        this.bmi = String.valueOf(calculateBMI());
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    private double calculateBMI() {
        int height = Integer.getInteger(getHeight());
        double weight = Double.parseDouble(getWeight());

        if (heightUnit.equals("cm") && weightUnit.equals("kg")) {
            double heightMeters = height / 100.0;
            return weight / (heightMeters * heightMeters);
        }
        if (heightUnit.equals("in") && weightUnit.equals("lbs")) {
            double heightMeters = height * 0.0254;
            return (weight / (heightMeters * heightMeters)) * 703;
        }
        return 0;
    }

    public String getBmi() {
        return bmi;
    }

    public void setBmi(String bmi) {
        this.bmi = bmi;
    }

    public String getFitnessGoal() {
        return fitnessGoal;
    }

    public void setFitnessGoal(String fitnessGoal) {
        this.fitnessGoal = fitnessGoal;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getHeightUnit() {
        return heightUnit;
    }

    public void setHeightUnit(String heightUnit) {
        this.heightUnit = heightUnit;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

}

