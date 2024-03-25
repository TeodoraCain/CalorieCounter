package com.example.caloriecounter.controller;

import com.example.caloriecounter.model.DAO.FoodItem;

import java.util.List;

public interface SimpleRecyclerViewerInterface {

    public List<FoodItem> applyFilters(List<FoodItem> foodItemList);
}
