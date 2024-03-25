package com.example.caloriecounter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caloriecounter.controller.SimpleRecyclerViewerAdapter;
import com.example.caloriecounter.model.DAO.FoodItem;

import java.util.ArrayList;
import java.util.List;

//Clasă obiecte cu 5 date membre de tipuri diferite:
// inițializare cel putin 5 obiecte,
// afișarea tuturor obiectelor initializate in interfata aplicatiei

/**

 Activity class for displaying a list of food items.
 This class initializes a RecyclerView to display food items.

 @author cc458
 */

public class FoodItemDisplayActivity extends AppCompatActivity {

    private boolean isVegetarian;
    private String minProtein;
    private String maxProtein;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_item_display);

        isVegetarian = getIntent().getBooleanExtra("ISVEGETARIAN", false);
        minProtein = getIntent().getStringExtra("MINPROTEIN");
        maxProtein = getIntent().getStringExtra("MAXPROTEIN");
        Log.d("values", minProtein + maxProtein + isVegetarian);

        List<FoodItem> foodItemList = new ArrayList<>();
        foodItemList.add(new FoodItem(100.0, "Fruit Pie", 316, true, 3.0f));
        foodItemList.add(new FoodItem(100.0, "Yellow Tomatoes", 15, true, 0.98f));
        foodItemList.add(new FoodItem(100.0, "Chocolate Ice-cream", 216, true, 3.8f));
        foodItemList.add(new FoodItem(100.0, "McDONALD'S, Hamburger", 264, false, 12.92f));
        foodItemList.add(new FoodItem(100.0, "KFC, Popcorn Chicken", 351, false, 17.67f));
        foodItemList.add(new FoodItem(100.0, "Rye Crackers", 481, true, 9.2f));

        RecyclerView rvFoodItemList = findViewById(R.id.rvFoodItemList);
        rvFoodItemList.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvFoodItemList.setLayoutManager(layoutManager);

        RecyclerView.Adapter<SimpleRecyclerViewerAdapter.ViewHolder> adapter = new SimpleRecyclerViewerAdapter(applyFilters(foodItemList));

        rvFoodItemList.setAdapter(adapter);

        Button btnFilter = findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(v -> {
            Intent intent = new Intent(FoodItemDisplayActivity.this, FilterResultsActivity.class);
            startActivity(intent);
        });

    }

    public List<FoodItem> applyFilters(List<FoodItem> foodItemList) {
        List<FoodItem> filteredList = new ArrayList<>();

        if (minProtein == null && maxProtein == null) {
            return foodItemList;
        }

        assert minProtein != null;
        if (minProtein.isEmpty() && maxProtein.isEmpty()) {

            for (FoodItem item : foodItemList) {
                if (item.isVegetarian() == isVegetarian) {
                    filteredList.add(item);
                }
            }
        } else if (maxProtein != null && !maxProtein.isEmpty()) {

            for (FoodItem item : foodItemList) {
                if (item.isVegetarian() == isVegetarian && item.getProtein() <= Float.parseFloat(maxProtein)) {
                    filteredList.add(item);
                }
            }
        } else {
            for (FoodItem item : foodItemList) {
                if (item.isVegetarian() == isVegetarian && item.getProtein() >= Float.parseFloat(minProtein)) {
                    filteredList.add(item);
                }
            }
        }

        return filteredList;
    }
}