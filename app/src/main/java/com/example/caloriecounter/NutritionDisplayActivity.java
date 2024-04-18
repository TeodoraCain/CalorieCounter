package com.example.caloriecounter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.caloriecounter.models.dao.DailyData;
import com.example.caloriecounter.models.dao.DailyDataDAO;
import com.example.caloriecounter.models.dao.DailyDataDAOImpl;
import com.example.caloriecounter.models.dao.Food;
import com.example.caloriecounter.models.dao.Recipe;
import com.example.caloriecounter.models.dataHolders.DailyDataHolder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class NutritionDisplayActivity extends AppCompatActivity {

    private final String TAG = "NutritionDisplayActivity";
    // views
    EditText etServingSize;
    TextView tvCalories;
    TextView tvCarbohydrate;
    TextView tvProtein;
    TextView tvTotalFat;
    TextView tvSaturatedFat;
    TextView tvFiber;
    TextView tvIron;
    TextView tvSugar;
    TextView tvSodium;
    TextView tvCalcium;
    TextView tvMagnesium;
    TextView tvVitaminA;
    TextView tvVitaminB6;
    TextView tvVitaminB12;
    TextView tvVitaminC;
    // attributes
    Food currentFood;
    DailyDataDAO dailyDataDAO;
    private String meal;
    private String date;
    private String currentDate;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition_display);

        parseIntentExtras();
        setToolbar();
        setUpViews();
        initDailyDataDAO();

        setUpSaveBtnListener();
        setUpTextWatcherForServingSize();
    }

    /********************************* INIT ACTIVITY **********************************************/
    private void parseIntentExtras() {
        Intent intent = getIntent();
        currentFood = intent.getParcelableExtra("FOOD");
        meal = intent.getStringExtra("MEAL");
        date = intent.getStringExtra("DATE");

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(date);
    }

    private void setUpViews() {
        context = NutritionDisplayActivity.this;
        etServingSize = findViewById(R.id.etServingSize);
        tvCalories = findViewById(R.id.tvCalories);
        tvCarbohydrate = findViewById(R.id.tvCarbohydrate);
        tvProtein = findViewById(R.id.tvProtein);
        tvTotalFat = findViewById(R.id.tvTotalFat);
        tvSaturatedFat = findViewById(R.id.tvSaturatedFat);
        tvFiber = findViewById(R.id.tvFiber);
        tvIron = findViewById(R.id.tvIron);
        tvSugar = findViewById(R.id.tvSugar);
        tvSodium = findViewById(R.id.tvSodium);
        tvCalcium = findViewById(R.id.tvCalcium);
        tvMagnesium = findViewById(R.id.tvMagnesium);
        tvVitaminA = findViewById(R.id.tvVitaminA);
        tvVitaminB6 = findViewById(R.id.tvVitaminB6);
        tvVitaminB12 = findViewById(R.id.tvVitaminB12);
        tvVitaminC = findViewById(R.id.tvVitaminC);
    }

    private void initDailyDataDAO() {
        dailyDataDAO = new DailyDataDAOImpl();
    }

    /********************************* SET UP TEXT WATCHER ****************************************/
    void setUpTextWatcherForServingSize() {
        etServingSize.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                double servingSize = 100.0;
                if (s.length() > 0) {
                    servingSize = Double.parseDouble(s.toString());
                }
                double percentage;
                if (servingSize > 0) {
                    try {
                        percentage = 1 + (servingSize - 100) / 100;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        percentage = 1;
                    }
                    setModifiedTexts(percentage);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    void setModifiedTexts(double percentage) {
        tvCalories.setText(String.format(Locale.ENGLISH, "%.1f", currentFood.getCalories() * percentage));
        tvCarbohydrate.setText(String.format(Locale.ENGLISH, "%.1f", currentFood.getCarbohydrate() * percentage));
        tvProtein.setText(String.format(Locale.ENGLISH, "%.1f", currentFood.getProtein() * percentage));
        tvTotalFat.setText(String.format(Locale.ENGLISH, "%.1f", currentFood.getTotal_fat() * percentage));
        tvSaturatedFat.setText(String.format(Locale.ENGLISH, "%.1f", currentFood.getSaturated_fat() * percentage));
        tvFiber.setText(String.format(Locale.ENGLISH, "%.1f", currentFood.getFiber() * percentage));
        tvIron.setText(String.format(Locale.ENGLISH, "%.1f", currentFood.getIron() * percentage));
        tvSugar.setText(String.format(Locale.ENGLISH, "%.1f", currentFood.getSugars() * percentage));
        tvSodium.setText(String.format(Locale.ENGLISH, "%.1f", currentFood.getSodium() * percentage));
        tvCalcium.setText(String.format(Locale.ENGLISH, "%.1f", currentFood.getCalcium() * percentage));
        tvMagnesium.setText(String.format(Locale.ENGLISH, "%.1f", currentFood.getMagnesium() * percentage));
        tvVitaminA.setText(String.format(Locale.ENGLISH, "%.1f", currentFood.getVitamin_a() * percentage));
        tvVitaminB6.setText(String.format(Locale.ENGLISH, "%.1f", currentFood.getVitamin_b6() * percentage));
        tvVitaminB12.setText(String.format(Locale.ENGLISH, "%.1f", currentFood.getVitamin_b12() * percentage));
        tvVitaminC.setText(String.format(Locale.ENGLISH, "%.1f", currentFood.getVitamin_c() * percentage));
    }

    /********************************* SET UP LISTENERS *******************************************/
    private void setUpSaveBtnListener() {
        ImageView saveImageView = findViewById(R.id.ivSave);
        saveImageView.setOnClickListener(v -> save());
    }

    /********************************* SAVE RECIPE ************************************************/
    void save() {
        if (date != null && !date.equals(currentDate)) {
            dailyDataDAO.get(date).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    DailyData dailyData = snapshot.getValue(DailyData.class);
                    if (dailyData == null) {
                        dailyData = new DailyData();
                    }

                    // get the recipe list based on the user selection BREAKFAST, LUNCH, DINNER, SNACKS
                    List<Recipe> recipeList = getRecipeListByMealType(meal, dailyData);
                    // create a new food item based on the new food serving size
                    Food currentFood = createFoodItemFromUI();
                    // create a new list of foods and add the food retrieved from UI
                    List<Food> foods = new ArrayList<>();
                    foods.add(currentFood);
                    // create new recipe based on current food and add it to the recipeList
                    Recipe recipe = createRecipeFromFood(currentFood, foods);
                    recipeList.add(recipe);
                    // update db with the new recipeList
                    updateDailyDataWithRecipeList(meal, recipeList, dailyData);
                    updateTotalCaloriesConsumed(currentFood.getCalories(), dailyData);
                    saveDailyDataToDBByDate(dailyData, date);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            DailyData dailyData = DailyDataHolder.getInstance().getData();
            if (dailyData == null) {
                dailyData = new DailyData();
            }

            List<Recipe> recipeList = getRecipeListByMealType(meal, dailyData);
            Food currentFood = createFoodItemFromUI();
            List<Food> foods = new ArrayList<>();
            foods.add(currentFood);
            Recipe recipe = createRecipeFromFood(currentFood, foods);
            recipeList.add(recipe);
            updateDailyDataWithRecipeList(meal, recipeList, dailyData);
            updateTotalCaloriesConsumed(currentFood.getCalories(), dailyData);

            saveDailyDataToDB(dailyData);
            DailyDataHolder.getInstance().setData(dailyData);
        }
    }

    private void saveDailyDataToDB(DailyData dailyData) {
        dailyDataDAO.update(dailyData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Data: Updated " + meal + " for " + currentDate);
                if (date != null) {
                    Intent intent = new Intent(context, DashboardActivity.class);
                    intent.putExtra("NAVIGATE_TO_DIARY_FRAGMENT", true);
                    startActivity(intent);
                } else {
                    finish();
                }
            } else {
                Log.e("SaveData", "Failed to update data", task.getException());
            }
        });
    }

    private List<Recipe> getRecipeListByMealType(String meal, DailyData dailyData) {
        switch (meal) {
            case "Breakfast":
                return dailyData.getBreakfast();
            case "Lunch":
                return dailyData.getLunch();
            case "Dinner":
                return dailyData.getDinner();
            case "Snacks":
                return dailyData.getSnacks();
            default:
                return new ArrayList<>();
        }
    }

    private void updateDailyDataWithRecipeList(String meal, List<Recipe> recipeList, DailyData dailyData) {
        switch (meal) {
            case "Breakfast":
                dailyData.setBreakfast(recipeList);
                break;
            case "Lunch":
                dailyData.setLunch(recipeList);
                break;
            case "Dinner":
                dailyData.setDinner(recipeList);
                break;
            case "Snacks":
                dailyData.setSnacks(recipeList);
                break;
        }
    }

    private void updateTotalCaloriesConsumed(double calories, DailyData dailyData) {
        int caloriesConsumed = dailyData.getCaloriesConsumed();
        caloriesConsumed += calories;
        dailyData.setCaloriesConsumed(caloriesConsumed);
    }

    void saveDailyDataToDBByDate(DailyData dailyData, String date) {
        dailyDataDAO.update(dailyData, date).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Data: Updated " + meal + " for " + date);
                Intent intent = new Intent(context, DashboardActivity.class);
                intent.putExtra("NAVIGATE_TO_DIARY_FRAGMENT", true);
                startActivity(intent);

            } else {
                Log.e("SaveData", "Failed to update data", task.getException());
            }
        });
    }

    Food createFoodItemFromUI() {
        Food food = new Food();
        food.setName(currentFood.getName());
        food.setServing_size(Double.parseDouble(etServingSize.getText().toString()));
        food.setCalories(Double.parseDouble(tvCalories.getText().toString()));
        food.setCarbohydrate(Double.parseDouble(tvCarbohydrate.getText().toString()));
        food.setProtein(Double.parseDouble(tvProtein.getText().toString()));
        food.setTotal_fat(Double.parseDouble(tvTotalFat.getText().toString()));
        food.setSaturated_fat(Double.parseDouble(tvSaturatedFat.getText().toString()));
        food.setFiber(Double.parseDouble(tvFiber.getText().toString()));
        food.setIron(Double.parseDouble(tvIron.getText().toString()));
        food.setSugars(Double.parseDouble(tvSugar.getText().toString()));
        food.setSodium(Double.parseDouble(tvSodium.getText().toString()));
        food.setCalcium(Double.parseDouble(tvCalcium.getText().toString()));
        food.setMagnesium(Double.parseDouble(tvMagnesium.getText().toString()));
        food.setVitamin_a(Double.parseDouble(tvVitaminA.getText().toString()));
        food.setVitamin_b6(Double.parseDouble(tvVitaminB6.getText().toString()));
        food.setVitamin_b12(Double.parseDouble(tvVitaminB12.getText().toString()));
        food.setVitamin_c(Double.parseDouble(tvVitaminC.getText().toString()));
        return food;
    }

    Recipe createRecipeFromFood(Food food, List<Food> foods) {
        return new Recipe(food.getName(), foods, food.getServing_size(), food.getCalories());
    }

    /********************************* TOOLBAR ****************************************************/
    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        //set title
        toolbar.setTitle(currentFood.getName());
        setSupportActionBar(toolbar);
        //set back button
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

    /********************************* LIFECYCLE OVERRIDES ****************************************************/
    @Override
    protected void onStart() {
        super.onStart();
        setTextsToUI();
    }

    void setTextsToUI() {
        if (currentFood != null) {
            etServingSize.setText(String.valueOf(currentFood.getServing_size()));
            tvCalories.setText(String.valueOf(currentFood.getCalories()));
            tvCarbohydrate.setText(String.valueOf(currentFood.getCarbohydrate()));
            tvProtein.setText(String.valueOf(currentFood.getProtein()));
            tvTotalFat.setText(String.valueOf(currentFood.getTotal_fat()));
            tvSaturatedFat.setText(String.valueOf(currentFood.getSaturated_fat()));
            tvFiber.setText(String.valueOf(currentFood.getFiber()));
            tvIron.setText(String.valueOf(currentFood.getIron()));
            tvSugar.setText(String.valueOf(currentFood.getSugars()));
            tvSodium.setText(String.valueOf(currentFood.getSodium()));
            tvCalcium.setText(String.valueOf(currentFood.getCalcium()));
            tvMagnesium.setText(String.valueOf(currentFood.getMagnesium()));
            tvVitaminA.setText(String.valueOf(currentFood.getVitamin_a()));
            tvVitaminB6.setText(String.valueOf(currentFood.getVitamin_b6()));
            tvVitaminB12.setText(String.valueOf(currentFood.getVitamin_b12()));
            tvVitaminC.setText(String.valueOf(currentFood.getVitamin_c()));
        }
    }
}