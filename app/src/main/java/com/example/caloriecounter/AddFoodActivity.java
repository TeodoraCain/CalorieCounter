package com.example.caloriecounter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caloriecounter.adapters.FoodRecyclerViewAdapter;
import com.example.caloriecounter.models.dao.Food;
import com.example.caloriecounter.models.dataHolders.FoodListHolder;
import com.example.caloriecounter.models.dataModel.IntentKeys;
import com.example.caloriecounter.models.dataModel.IntentResults;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddFoodActivity extends AppCompatActivity {

    private final String TAG = "AddFoodActivity";
    private boolean isRecipeIngredient;
    private ActivityResultLauncher<Intent> launcher;

    //    private TabLayout foodTabs;
//    private ViewPager2 viewPager;
    private Context context;
    private List<Food> foods;
    private RecyclerView rvFoods;
    private EditText etSearchFoods;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        initActivity();
        setUpActivity();

    }

    //region Init Activity
    private void initContext() {
        context = AddFoodActivity.this;
    }

    private void parseIntentExtras() {
        Intent intent = getIntent();
        isRecipeIngredient = intent.getBooleanExtra(IntentKeys.IS_RECIPE_INGREDIENT, false);
    }

    //endregion
    private void initActivity() {
        initContext();
        parseIntentExtras();
    }


    //region Set Up Activity
    private void setUpLauncher() {
        launcher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            if (result.getResultCode() == IntentResults.RESULT_ADD_INGREDIENT) {
                                Intent data = result.getData();
                                if (data != null && data.hasExtra(IntentKeys.INGREDIENT)) {
                                    Food ingredient = (Food) data.getParcelableExtra(IntentKeys.INGREDIENT);
                                    Intent returnIntent = new Intent();
                                    returnIntent.putExtra(IntentKeys.INGREDIENT, ingredient);
                                    setResult(IntentResults.RESULT_ADD_INGREDIENT, returnIntent);
                                    finish();
                                }
                            }
                        });
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);

        String meal = this.getIntent().getStringExtra(IntentKeys.MEAL);
        if (meal == null || meal.isEmpty()) {
            meal = getString(R.string.food);
        }
        toolbar.setTitle(MessageFormat.format("Add {0}", meal));
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setUpViews() {
        //foodTabs = findViewById(R.id.tabLayout);
        //viewPager = findViewById(R.id.viewPager);
        etSearchFoods = findViewById(R.id.etSearchFoods);
        rvFoods = findViewById(R.id.rvFoods);
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

    //endregion
    private void setUpActivity() {
        setToolbar();
        setUpViews();
        setUpTextWatcherForSearch();
        setUpRecyclerView();
        setUpLauncher();
        //setUpTabsAndViewPager();
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
        String date = getIntent().getStringExtra(IntentKeys.DATE);
        String meal = getIntent().getStringExtra(IntentKeys.MEAL);
        Log.d(TAG, "ItemClick: Clicked on " + food.getName());

        Intent intent = new Intent(context, NutritionDisplayActivity.class);
        intent.putExtra(IntentKeys.MEAL, meal);
        intent.putExtra(IntentKeys.FOOD, food);
        intent.putExtra(IntentKeys.DATE, date);
        if (!isRecipeIngredient) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            intent.putExtra(IntentKeys.IS_RECIPE_INGREDIENT, isRecipeIngredient);
            launcher.launch(intent);
        }
    }

//    private void setUpTabsAndViewPager() {
//        Log.d(TAG, "Setting up the View Pager and Tabs..");
//        ViewPagerAdapter viewPagerAdapter = new AddFoodActivity.ViewPagerAdapter(this);
//        viewPager.setAdapter(viewPagerAdapter);
//
//        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageSelected(int position) {
//                super.onPageSelected(position);
//                Objects.requireNonNull(foodTabs.getTabAt(position)).select();
//            }
//        });
//
//        foodTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
//    }

    /********************************* LIFECYCLE OVERRIDES ***********************************************/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    /********************************* TAB VIEW PAGER ADAPTER *************************************/
//    public static class ViewPagerAdapter extends FragmentStateAdapter {
//
//        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
//            super(fragmentActivity);
//        }
//
//        @NonNull
//        @Override
//        public Fragment createFragment(int position) {
//            if (position == 0) return new AddSimpleFoodFragment();
//            else return new AddRecipeFragment();
//        }
//
//        @Override
//        public int getItemCount() {
//            return 2;
//        }
//    }
}