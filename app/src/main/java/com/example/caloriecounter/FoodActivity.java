package com.example.caloriecounter;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caloriecounter.controller.FoodRecyclerViewAdapter;
import com.example.caloriecounter.model.DAO.Food;
import com.example.caloriecounter.model.dataHolder.FoodListHolder;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FoodActivity extends AppCompatActivity {

    private String meal;
    private RecyclerView rvFoods;
    private EditText etSearchFoods;
    private List<Food> foods;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        init();

        etSearchFoods.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String query = s.toString().trim();
                searchByListQuery(query);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        rvFoods.setLayoutManager(new LinearLayoutManager(this));
    }

    private void searchByListQuery(String query) {
        List<Food> queryResult = new ArrayList<>();
        String[] queryWords = query.toLowerCase().split("\\s+");

        for(Food food : foods){
            if (food.getName().toLowerCase().contains(query.toLowerCase())) {
                queryResult.add(food);
            }
            for (String word : queryWords) {
                if (food.getName().toLowerCase().contains(word)) {
                    queryResult.add(food);
                }
            }
        }

        updateRecyclerView(queryResult);
    }

    private void updateRecyclerView(List<Food> foods) {
        FoodRecyclerViewAdapter rvAdapter = new FoodRecyclerViewAdapter(foods, this::onItemClick);
        rvFoods.setAdapter(rvAdapter);
    }

    private void init() {
        setToolbar();

        etSearchFoods = findViewById(R.id.etSearchFoods);
        rvFoods = (RecyclerView) findViewById(R.id.rvFoods);
        foods = FoodListHolder.getInstance().getData();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        final String MEAL = "MEAL";

        meal = this.getIntent().getStringExtra(MEAL);
        toolbar.setTitle(MessageFormat.format("Add {0}", meal));
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onItemClick(Food food) {
        String date = this.getIntent().getStringExtra("DATE");
        Intent intent = new Intent(FoodActivity.this, NutritionDisplayActivity.class);
        intent.putExtra("MEAL", meal);
        intent.putExtra("FOOD", food);
        intent.putExtra("DATE", date);
        Log.d("Info", food.getName());
        startActivity(intent);
    }
}