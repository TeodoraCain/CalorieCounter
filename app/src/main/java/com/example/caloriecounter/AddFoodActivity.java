package com.example.caloriecounter;

import android.content.Context;
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

import com.example.caloriecounter.adapters.FoodRecyclerViewAdapter;
import com.example.caloriecounter.models.dao.Food;
import com.example.caloriecounter.models.dataHolders.FoodListHolder;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddFoodActivity extends AppCompatActivity {

    private final String TAG = "AddFoodActivity";
    private Context context;

    private String meal;
    private List<Food> foods;

    private RecyclerView rvFoods;
    private EditText etSearchFoods;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        setUpViews();
        setUpTextWatcherForSearch();
        setUpRecyclerView();
    }

    /********************************* SET UP VIEWS ***********************************************/
    private void setUpViews() {
        setToolbar();
        context = AddFoodActivity.this;
        etSearchFoods = findViewById(R.id.etSearchFoods);
        rvFoods = findViewById(R.id.rvFoods);
        foods = FoodListHolder.getInstance().getData();
    }

    private void setUpRecyclerView() {
        rvFoods.setLayoutManager(new LinearLayoutManager(this));
        searchByQuery(""); // Initial search
    }

    private void setUpTextWatcherForSearch() {
        Log.d(TAG, "Setting up the text watcher for search..");
        etSearchFoods.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String query = s.toString().trim();
                searchByQuery(query);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /********************************* ACTIONS ****************************************************/
    private void searchByQuery(String query) {
        List<Food> queryResult = new ArrayList<>();
        String[] queryWords = query.toLowerCase().split("\\s+");

        for (Food food : foods) {
            boolean matched = food.getName().toLowerCase().contains(query.toLowerCase());
            for (String word : queryWords) {
                if (food.getName().toLowerCase().contains(word)) {
                    matched = true;
                }
            }
            if (matched) {
                queryResult.add(food);
            }
        }
        updateRecyclerView(queryResult);
    }

    private void updateRecyclerView(List<Food> foods) {
        FoodRecyclerViewAdapter rvAdapter = new FoodRecyclerViewAdapter(foods, this::onItemClick);
        rvFoods.setAdapter(rvAdapter);
    }

    public void onItemClick(Food food) {
        String date = this.getIntent().getStringExtra("DATE");

        Log.d(TAG, "ItemClick: Clicked on " + food.getName());

        Intent intent = new Intent(context, NutritionDisplayActivity.class);
        intent.putExtra("MEAL", meal);
        intent.putExtra("FOOD", food);
        intent.putExtra("DATE", date);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    /********************************* SET UP TOOLBAR *********************************************/
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}