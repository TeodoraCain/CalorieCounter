package com.example.caloriecounter.view.fragments.food;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caloriecounter.NutritionDisplayActivity;
import com.example.caloriecounter.R;
import com.example.caloriecounter.adapters.FoodRecyclerViewAdapter;
import com.example.caloriecounter.models.dao.Food;
import com.example.caloriecounter.models.dataHolders.FoodListHolder;
import com.example.caloriecounter.models.dataModel.IntentKeys;

import java.util.ArrayList;
import java.util.List;

public class AddSimpleFoodFragment extends Fragment {

    private final String TAG = "AddSimpleFoodFragment";
    private Context context;
    private List<Food> foods;

    private RecyclerView rvFoods;
    private EditText etSearchFoods;

    public AddSimpleFoodFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_simple_food, container, false);
        initFragment(view);
        return view;
    }

    /********************************* INIT FRAGMENT ***********************************************/
    private void initFragment(View view) {
        setUpViews(view);
        setUpTextWatcherForSearch();
        setUpRecyclerView();
    }

    private void setUpViews(View view) {
        etSearchFoods = view.findViewById(R.id.etSearchFoods);
        rvFoods = view.findViewById(R.id.rvFoods);
        foods = FoodListHolder.getInstance().getData();
    }

    private void setUpRecyclerView() {
        rvFoods.setLayoutManager(new LinearLayoutManager(this.context));
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
        onFoodSelected(food);
    }

    public void onFoodSelected(Food food) {
        String date = requireActivity().getIntent().getStringExtra(IntentKeys.DATE);
        String meal = requireActivity().getIntent().getStringExtra(IntentKeys.MEAL);
        Log.d(TAG, "ItemClick: Clicked on " + food.getName());

        Intent intent = new Intent(context, NutritionDisplayActivity.class);
        intent.putExtra(IntentKeys.MEAL, meal);
        intent.putExtra(IntentKeys.FOOD, food);
        intent.putExtra(IntentKeys.DATE, date);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /*********************************** LIFECYCLE OVERRIDES ***************************************/
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "Fragment attached..");
        this.context = context;
    }
}