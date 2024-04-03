package com.example.caloriecounter.model.DAO;

public class FoodItem {
    private double servingSize;
    private String name;
    private int caloriesPerServing;
    private boolean isVegetarian;
    private float protein;
//    private String mealType; // breakfast, lunch , dinner, snack

    public FoodItem(double servingSize, String name, int caloriesPerServing, boolean isVegetarian, float protein) {
        this.servingSize = servingSize;
        this.name = name;
        this.caloriesPerServing = caloriesPerServing;
        this.isVegetarian = isVegetarian;
        this.protein = protein;
    }


    public void setServingSize(int servingSize) {
        this.servingSize = servingSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCaloriesPerServing() {
        return caloriesPerServing;
    }

    public double getServingSize() {
        return servingSize;
    }

    public void setServingSize(double servingSize) {
        this.servingSize = servingSize;
    }

    public void setCaloriesPerServing(int caloriesPerServing) {
        this.caloriesPerServing = caloriesPerServing;
    }

    public boolean isVegetarian() {
        return isVegetarian;
    }

    public void setVegetarian(boolean vegetarian) {
        isVegetarian = vegetarian;
    }

    public float getProtein() {
        return protein;
    }

    public void setProtein(float protein) {
        this.protein = protein;
    }

}
