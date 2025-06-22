package com.example.caloriecounter.models.dao;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class Recipe implements Parcelable {
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

    public Recipe(String name, List<Food> ingredients, double serving_size) {
        this.name = name;
        this.ingredients = ingredients;
        this.serving_size = serving_size;
        this.calories = calculateRecipeCalories(ingredients);
    }

    private double calculateRecipeCalories(List<Food> ingredients) {
        double totalCalories = 0;
        for(Food food : ingredients){
            totalCalories+= food.getCalories();
        }
        return  totalCalories;
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
        this.calories = calculateRecipeCalories(ingredients);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeTypedList(ingredients);
        dest.writeDouble(serving_size);
        dest.writeDouble(calories);

    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    protected Recipe(Parcel in) {
        this.name = in.readString();
        this.ingredients = in.createTypedArrayList(Food.CREATOR);
        this.serving_size = in.readDouble();
        this.calories = in.readDouble();

    }

}
