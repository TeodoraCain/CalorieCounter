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

import com.example.caloriecounter.R;
import com.example.caloriecounter.CreateRecipeActivity;
import com.example.caloriecounter.adapters.RecipeRecyclerViewAdapter;
import com.example.caloriecounter.models.dao.Recipe;
import com.example.caloriecounter.models.dataHolders.RecipeListHolder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AddRecipeFragment extends Fragment {

    private final String TAG = "AddRecipeFragment";
    private Context context;
    private List<Recipe> recipes;

    private RecyclerView rvRecipes;
    private EditText etSearchRecipes;

    public AddRecipeFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_recipe, container, false);
        initFragment(view);
        return view;
    }

    private void initFragment(View view) {
        setUpViews(view);
        setUpTextWatcherForSearch();
        setUpRecyclerView();
    }


    private void setUpViews(View view) {
        etSearchRecipes = view.findViewById(R.id.etSearchRecipes);
        rvRecipes = view.findViewById(R.id.rvRecipes);
        recipes = RecipeListHolder.getInstance().getData();

        FloatingActionButton btnNewRecipe = view.findViewById(R.id.btnNewRecipe);
        btnNewRecipe.setOnClickListener(this::onGoToCreateRecipe);
    }

    private void onGoToCreateRecipe() {
        Intent intent = new Intent(context, CreateRecipeActivity.class);
        startActivity(intent);
    }

    private void setUpRecyclerView() {
        rvRecipes.setLayoutManager(new LinearLayoutManager(this.context));
        searchByQuery(""); // Initial search
    }

    private void setUpTextWatcherForSearch() {
        Log.d(TAG, "Setting up the text watcher for search..");
        etSearchRecipes.addTextChangedListener(new TextWatcher() {
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
        List<Recipe> queryResult = new ArrayList<>();
        String[] queryWords = query.toLowerCase().split("\\s+");

        for (Recipe recipe : recipes) {
            boolean matched = recipe.getName().toLowerCase().contains(query.toLowerCase());
            for (String word : queryWords) {
                if (recipe.getName().toLowerCase().contains(word)) {
                    matched = true;
                }
            }
            if (matched) {
                queryResult.add(recipe);
            }
        }
        updateRecyclerView(queryResult);
    }

    private void updateRecyclerView(List<Recipe> recipes) {
        RecipeRecyclerViewAdapter rvAdapter = new RecipeRecyclerViewAdapter(recipes, this::onItemClick);
        rvRecipes.setAdapter(rvAdapter);
    }

    public void onItemClick(Recipe recipe) {
        onRecipeSelected(recipe);
    }

    public void onRecipeSelected(Recipe recipe) {
//        String date = requireActivity().getIntent().getStringExtra(IntentKeys.DATE);
//        String meal = requireActivity().getIntent().getStringExtra(IntentKeys.MEAL);
//        Log.d(TAG, "ItemClick: Clicked on " + recipe.getName());
//
//        Intent intent = new Intent(context, NutritionDisplayActivity.class);
//        intent.putExtra(IntentKeys.MEAL, meal);
//        intent.putExtra(IntentKeys.RECIPE, recipe);
//        intent.putExtra(IntentKeys.DATE, date);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
    }

    /*********************************** LIFECYCLE OVERRIDES ***************************************/
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "Fragment attached..");
        this.context = context;
    }

    private void onGoToCreateRecipe(View v) {
        onGoToCreateRecipe();
    }
}