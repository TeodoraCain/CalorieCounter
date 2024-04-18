package com.example.caloriecounter.models.dataHolders;


import com.example.caloriecounter.models.dao.Food;

import java.util.ArrayList;
import java.util.List;

public class FoodListHolder {
    private List<Food> foodList;

    public List<Food> getData() {
        if (foodList != null)
            return foodList;

        else return new ArrayList<>();
    }

    public void setData(List<Food> foodList) {
        this.foodList = foodList;
    }

    private static final FoodListHolder holder = new FoodListHolder();

    public static FoodListHolder getInstance() {
        return holder;
    }
}
