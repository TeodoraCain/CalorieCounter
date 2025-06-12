package com.example.caloriecounter.models.dataHolders;


import com.example.caloriecounter.models.dao.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeListHolder {
    private List<Recipe> recipeList;

    public List<Recipe> getData() {
        if (recipeList != null)
            return recipeList;

        else return new ArrayList<>();
    }

    public void setData(List<Recipe> recipeList) {
        this.recipeList = recipeList;
    }

    private static final RecipeListHolder holder = new RecipeListHolder();

    public static RecipeListHolder getInstance() {
        return holder;
    }
}
