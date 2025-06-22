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

import com.example.caloriecounter.adapters.RecipeRecyclerViewAdapter;
import com.example.caloriecounter.models.dao.Recipe;
import com.example.caloriecounter.models.dataHolders.RecipeListHolder;
import com.example.caloriecounter.models.dataModel.IntentKeys;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddRecipeActivity extends AppCompatActivity {

    private final String TAG = "AddRecipeActivity";
    private List<Recipe> recipes;

    private String selectedMeal;
    private String diaryDate;

    private Context context;
    private EditText etSearchRecipes;
    private RecyclerView rvRecipes;

    public AddRecipeActivity() {
        // Required empty public constructor
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        initActivity();
        setUpActivity();
    }

    //region Init Activity
    private void initContext() {
        context = AddRecipeActivity.this;
    }

    private void parseIntentExtras() {
        Intent intent = getIntent();
        selectedMeal = intent.getStringExtra(IntentKeys.MEAL);
        diaryDate = intent.getStringExtra(IntentKeys.DATE);
    }

    private void initData() {
        recipes = RecipeListHolder.getInstance().getData();
    }

    //endregion
    private void initActivity() {
        initContext();
        initData();
        parseIntentExtras();
    }

    //region Set Up Activity
    private void setUpViews() {
        etSearchRecipes = findViewById(R.id.etSearchRecipes);
        rvRecipes = findViewById(R.id.rvRecipes);

        FloatingActionButton btnNewRecipe = findViewById(R.id.btnNewRecipe);
        btnNewRecipe.setOnClickListener(v -> {
            Intent intent = new Intent(context, CreateRecipeActivity.class);
            intent.putExtra(IntentKeys.MEAL, selectedMeal);
            intent.putExtra(IntentKeys.DATE, diaryDate);
            startActivity(intent);
        });
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

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    //endregion
    private void setUpActivity() {
        setUpViews();
        setUpTextWatcherForSearch();
        setUpRecyclerView();
        setToolbar();
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
        Log.d(TAG, "ItemClick: Clicked on " + recipe.getName());

        Intent intent = new Intent(context, NutritionDisplayActivity.class);
        intent.putExtra(IntentKeys.MEAL, selectedMeal);
        intent.putExtra(IntentKeys.DATE, diaryDate);
        intent.putExtra(IntentKeys.RECIPE, recipe);
        intent.putExtra(IntentKeys.IS_RECIPE, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        initData();
        searchByQuery("");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}