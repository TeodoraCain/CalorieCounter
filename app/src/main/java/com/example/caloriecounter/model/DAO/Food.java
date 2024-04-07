package com.example.caloriecounter.model.DAO;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Food implements Parcelable {
    private String name;
    private double serving_size;
    private double calories;
    private double total_fat;
    private double saturated_fat;
    private double sodium;
    private double vitamin_a;
    private double vitamin_b12;
    private double vitamin_b6;
    private double vitamin_c;
    private double vitamin_d;
    private double vitamin_e;
    private double vitamin_k;
    private double calcium;
    private double iron;
    private double magnesium;
    private double potassium;
    private double protein;
    private double carbohydrate;
    private double fiber;
    private double sugars;

    public Food() {
    }

    public Food(String name, double serving_size, double calories, double total_fat, double saturated_fat, double sodium, double vitamin_a, double vitamin_b12, double vitamin_b6, double vitamin_c, double vitamin_d, double vitamin_e, double vitamin_k, double calcium, double iron, double magnesium, double potassium, double protein, double carbohydrate, double fiber, double sugars) {
        this.name = name;
        this.serving_size = serving_size;
        this.calories = calories;
        this.total_fat = total_fat;
        this.saturated_fat = saturated_fat;
        this.sodium = sodium;
        this.vitamin_a = vitamin_a;
        this.vitamin_b12 = vitamin_b12;
        this.vitamin_b6 = vitamin_b6;
        this.vitamin_c = vitamin_c;
        this.vitamin_d = vitamin_d;
        this.vitamin_e = vitamin_e;
        this.vitamin_k = vitamin_k;
        this.calcium = calcium;
        this.iron = iron;
        this.magnesium = magnesium;
        this.potassium = potassium;
        this.protein = protein;
        this.carbohydrate = carbohydrate;
        this.fiber = fiber;
        this.sugars = sugars;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public double getTotal_fat() {
        return total_fat;
    }

    public void setTotal_fat(double total_fat) {
        this.total_fat = total_fat;
    }

    public double getSaturated_fat() {
        return saturated_fat;
    }

    public void setSaturated_fat(double saturated_fat) {
        this.saturated_fat = saturated_fat;
    }

    public double getSodium() {
        return sodium;
    }

    public void setSodium(double sodium) {
        this.sodium = sodium;
    }

    public double getVitamin_a() {
        return vitamin_a;
    }

    public void setVitamin_a(double vitamin_a) {
        this.vitamin_a = vitamin_a;
    }

    public double getVitamin_b12() {
        return vitamin_b12;
    }

    public void setVitamin_b12(double vitamin_b12) {
        this.vitamin_b12 = vitamin_b12;
    }

    public double getVitamin_b6() {
        return vitamin_b6;
    }

    public void setVitamin_b6(double vitamin_b6) {
        this.vitamin_b6 = vitamin_b6;
    }

    public double getVitamin_c() {
        return vitamin_c;
    }

    public void setVitamin_c(double vitamin_c) {
        this.vitamin_c = vitamin_c;
    }

    public double getVitamin_d() {
        return vitamin_d;
    }

    public void setVitamin_d(double vitamin_d) {
        this.vitamin_d = vitamin_d;
    }

    public double getVitamin_e() {
        return vitamin_e;
    }

    public void setVitamin_e(double vitamin_e) {
        this.vitamin_e = vitamin_e;
    }

    public double getVitamin_k() {
        return vitamin_k;
    }

    public void setVitamin_k(double vitamin_k) {
        this.vitamin_k = vitamin_k;
    }

    public double getCalcium() {
        return calcium;
    }

    public void setCalcium(double calcium) {
        this.calcium = calcium;
    }

    public double getIron() {
        return iron;
    }

    public void setIron(double iron) {
        this.iron = iron;
    }

    public double getMagnesium() {
        return magnesium;
    }

    public void setMagnesium(double magnesium) {
        this.magnesium = magnesium;
    }

    public double getPotassium() {
        return potassium;
    }

    public void setPotassium(double potassium) {
        this.potassium = potassium;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getCarbohydrate() {
        return carbohydrate;
    }

    public void setCarbohydrate(double carbohydrate) {
        this.carbohydrate = carbohydrate;
    }

    public double getFiber() {
        return fiber;
    }

    public void setFiber(double fiber) {
        this.fiber = fiber;
    }

    public double getSugars() {
        return sugars;
    }

    public void setSugars(double sugars) {
        this.sugars = sugars;
    }

    protected Food(Parcel in) {
        name = in.readString();
        serving_size = in.readDouble();
        calories = in.readDouble();
        total_fat = in.readDouble();
        saturated_fat = in.readDouble();
        sodium = in.readDouble();
        vitamin_a = in.readDouble();
        vitamin_b12 = in.readDouble();
        vitamin_b6 = in.readDouble();
        vitamin_c = in.readDouble();
        vitamin_d = in.readDouble();
        vitamin_e = in.readDouble();
        vitamin_k = in.readDouble();
        calcium = in.readDouble();
        iron = in.readDouble();
        magnesium = in.readDouble();
        potassium = in.readDouble();
        protein = in.readDouble();
        carbohydrate = in.readDouble();
        fiber = in.readDouble();
        sugars = in.readDouble();
    }
    public static final Creator<Food> CREATOR = new Creator<Food>() {
        @Override
        public Food createFromParcel(Parcel in) {
            return new Food(in);
        }

        @Override
        public Food[] newArray(int size) {
            return new Food[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeDouble(serving_size);
        dest.writeDouble(calories);
        dest.writeDouble(total_fat);
        dest.writeDouble(saturated_fat);
        dest.writeDouble(sodium);
        dest.writeDouble(vitamin_a);
        dest.writeDouble(vitamin_b12);
        dest.writeDouble(vitamin_b6);
        dest.writeDouble(vitamin_c);
        dest.writeDouble(vitamin_d);
        dest.writeDouble(vitamin_e);
        dest.writeDouble(vitamin_k);
        dest.writeDouble(calcium);
        dest.writeDouble(iron);
        dest.writeDouble(magnesium);
        dest.writeDouble(potassium);
        dest.writeDouble(protein);
        dest.writeDouble(carbohydrate);
        dest.writeDouble(fiber);
        dest.writeDouble(sugars);
    }
}
