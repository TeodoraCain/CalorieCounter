package com.example.caloriecounter;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.caloriecounter.model.DAO.DailyData;
import com.example.caloriecounter.model.DAO.DailyDataDAO;
import com.example.caloriecounter.model.DAO.DailyDataDAOImpl;
import com.example.caloriecounter.model.DAO.Food;
import com.example.caloriecounter.model.DAO.Recipe;
import com.example.caloriecounter.model.dataHolder.DailyDataHolder;
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

    private EditText etServingSize;
    private TextView tvCalories, tvCarbohydrate, tvProtein,
            tvTotalFat, tvSaturatedFat, tvFiber, tvIron, tvSugar,
            tvSodium, tvCalcium, tvMagnesium, tvVitaminA,
            tvVitaminB6, tvVitaminB12, tvVitaminC;

    private Food currentFood;
    private String meal;
    private String date;
    private String currentDate;

    private DailyDataDAO dailyDataDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition_display);

        Intent intent = getIntent();
        currentFood = intent.getParcelableExtra("FOOD");
        meal = intent.getStringExtra("MEAL");
        date = intent.getStringExtra("DATE");

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(date);

        setToolbar();
        init();
        setTexts();
        setListeners();
    }

    private void setListeners() {
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
                        // Handle the case where parsing fails, for example, if the input is not a valid integer
                        e.printStackTrace();
                        percentage = 1;
                    }

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
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void setTexts() {
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

    private void init() {
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

        dailyDataDAO = new DailyDataDAOImpl();
    }


    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle(currentFood.getName());
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ImageView saveImageView = findViewById(R.id.ivSave);
        saveImageView.setOnClickListener(v -> save());
    }

    private void save() {
        if (date != null && !date.equals(currentDate)) {
            dailyDataDAO.get(date).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    DailyData dailyData = snapshot.getValue(DailyData.class);
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
                    Log.d("dailydata", dailyData + "\n" + dailyData.getLunch().isEmpty());
                    saveDailyDataToDatabase(dailyData, date);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle cancellation
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
            Log.d("dailydata", dailyData + "\n" + dailyData.getLunch().isEmpty());

            dailyDataDAO.update(dailyData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("SaveData", "Data updated successfully");
                    if (date != null) {
                        Intent intent = new Intent(NutritionDisplayActivity.this, DashboardActivity.class);
                        intent.putExtra("NAVIGATE_TO_DIARY_FRAGMENT", true);
                        startActivity(intent);
                    } else {
                        finish();
                    }
                } else {
                    Log.e("SaveData", "Failed to update data", task.getException());
                }
            });
            DailyDataHolder.getInstance().setData(dailyData);
        }
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

    private Food createFoodItemFromUI() {
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

    private Recipe createRecipeFromFood(Food food, List<Food> foods) {
        return new Recipe(food.getName(), foods, food.getServing_size(), food.getCalories());
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

    private void saveDailyDataToDatabase(DailyData dailyData, String date) {
        dailyDataDAO.update(dailyData, date).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                Log.d("SaveData", "Data updated successfully");
                Intent intent = new Intent(NutritionDisplayActivity.this, DashboardActivity.class);
                intent.putExtra("NAVIGATE_TO_DIARY_FRAGMENT", true);
                startActivity(intent);
                //finish();
            } else {
                Log.e("SaveData", "Failed to update data", task.getException());
            }
        });
    }
}