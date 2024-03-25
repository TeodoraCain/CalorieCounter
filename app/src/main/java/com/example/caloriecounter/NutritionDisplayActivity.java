package com.example.caloriecounter;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.caloriecounter.model.DAO.DailyData;
import com.example.caloriecounter.model.DAO.DailyDataDAO;
import com.example.caloriecounter.model.DAO.DailyDataDAOImpl;
import com.example.caloriecounter.model.DAO.Food;
import com.example.caloriecounter.model.DAO.Recipe;
import com.example.caloriecounter.model.dataHolder.DailyDataHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class NutritionDisplayActivity extends AppCompatActivity {

    private EditText etServingSize;
    private TextView tvCalories, tvCarbohydrate, tvProtein, tvTotalFat, tvSaturatedFat, tvFiber,
            tvIron, tvSugar, tvSodium, tvCalcium, tvMagnesium, tvVitaminA, tvVitaminB6, tvVitaminB12, tvVitaminC;

    private Food food;
    private String meal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition_display);

        Intent intent = getIntent();
        food = intent.getParcelableExtra("FOOD");
        meal = intent.getStringExtra("MEAL");

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
                double percentage = 1;
                if (servingSize > 0) {
                    try {
                        percentage = 1 + (servingSize - 100) / 100;

                    } catch (NumberFormatException e) {
                        // Handle the case where parsing fails, for example, if the input is not a valid integer
                        e.printStackTrace();
                        percentage = 1;
                    }

                    tvCalories.setText(String.format(Locale.ENGLISH, "%.1f", food.getCalories() * percentage));
                    tvCarbohydrate.setText(String.format(Locale.ENGLISH, "%.1f", food.getCarbohydrate() * percentage));
                    tvProtein.setText(String.format(Locale.ENGLISH, "%.1f", food.getProtein() * percentage));
                    tvTotalFat.setText(String.format(Locale.ENGLISH, "%.1f", food.getTotal_fat() * percentage));
                    tvSaturatedFat.setText(String.format(Locale.ENGLISH, "%.1f", food.getSaturated_fat() * percentage));
                    tvFiber.setText(String.format(Locale.ENGLISH, "%.1f", food.getFiber() * percentage));
                    tvIron.setText(String.format(Locale.ENGLISH, "%.1f", food.getIron() * percentage));
                    tvSugar.setText(String.format(Locale.ENGLISH, "%.1f", food.getSugars() * percentage));
                    tvSodium.setText(String.format(Locale.ENGLISH, "%.1f", food.getSodium() * percentage));
                    tvCalcium.setText(String.format(Locale.ENGLISH, "%.1f", food.getCalcium() * percentage));
                    tvMagnesium.setText(String.format(Locale.ENGLISH, "%.1f", food.getMagnesium() * percentage));
                    tvVitaminA.setText(String.format(Locale.ENGLISH, "%.1f", food.getVitamin_a() * percentage));
                    tvVitaminB6.setText(String.format(Locale.ENGLISH, "%.1f", food.getVitamin_b6() * percentage));
                    tvVitaminB12.setText(String.format(Locale.ENGLISH, "%.1f", food.getVitamin_b12() * percentage));
                    tvVitaminC.setText(String.format(Locale.ENGLISH, "%.1f", food.getVitamin_c() * percentage));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void setTexts() {
        if (food != null) {
            etServingSize.setText(String.valueOf(food.getServing_size()));
            tvCalories.setText(String.valueOf(food.getCalories()));
            tvCarbohydrate.setText(String.valueOf(food.getCarbohydrate()));
            tvProtein.setText(String.valueOf(food.getProtein()));
            tvTotalFat.setText(String.valueOf(food.getTotal_fat()));
            tvSaturatedFat.setText(String.valueOf(food.getSaturated_fat()));
            tvFiber.setText(String.valueOf(food.getFiber()));
            tvIron.setText(String.valueOf(food.getIron()));
            tvSugar.setText(String.valueOf(food.getSugars()));
            tvSodium.setText(String.valueOf(food.getSodium()));
            tvCalcium.setText(String.valueOf(food.getCalcium()));
            tvMagnesium.setText(String.valueOf(food.getMagnesium()));
            tvVitaminA.setText(String.valueOf(food.getVitamin_a()));
            tvVitaminB6.setText(String.valueOf(food.getVitamin_b6()));
            tvVitaminB12.setText(String.valueOf(food.getVitamin_b12()));
            tvVitaminC.setText(String.valueOf(food.getVitamin_c()));
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
    }


    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle(food.getName());
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ImageView saveImageView = findViewById(R.id.ivSave);
        saveImageView.setOnClickListener(v -> save());
    }

    private void save() {
        DailyData dailyData = DailyDataHolder.getInstance().getData();
        List<Recipe> recipeList = new ArrayList<>();
        switch (meal) {
            case "BREAKFAST":
                recipeList = dailyData.getBreakfast();
                break;
            case "LUNCH":
                recipeList = dailyData.getLunch();
                break;
            case "DINNER":
                recipeList = dailyData.getDinner();
                break;
            case "SNACKS":
                recipeList = dailyData.getSnacks();
                break;
        }
        List<Food> foods = new ArrayList<>();
        Food currentFood = food;
        currentFood.setServing_size(Double.parseDouble(etServingSize.getText().toString()));
        currentFood.setCalories(Double.parseDouble(tvCalories.getText().toString()));
        currentFood.setCarbohydrate(Double.parseDouble(tvCarbohydrate.getText().toString()));
        currentFood.setProtein(Double.parseDouble(tvProtein.getText().toString()));
        currentFood.setTotal_fat(Double.parseDouble(tvTotalFat.getText().toString()));
        currentFood.setSaturated_fat(Double.parseDouble(tvSaturatedFat.getText().toString()));
        currentFood.setFiber(Double.parseDouble(tvFiber.getText().toString()));
        currentFood.setIron(Double.parseDouble(tvIron.getText().toString()));
        currentFood.setSugars(Double.parseDouble(tvSugar.getText().toString()));
        currentFood.setSodium(Double.parseDouble(tvSodium.getText().toString()));
        currentFood.setCalcium(Double.parseDouble(tvCalcium.getText().toString()));
        currentFood.setMagnesium(Double.parseDouble(tvMagnesium.getText().toString()));
        currentFood.setVitamin_a(Double.parseDouble(tvVitaminA.getText().toString()));
        currentFood.setVitamin_b6(Double.parseDouble(tvVitaminB6.getText().toString()));
        currentFood.setVitamin_b12(Double.parseDouble(tvVitaminB12.getText().toString()));
        currentFood.setVitamin_c(Double.parseDouble(tvVitaminC.getText().toString()));

        foods.add(currentFood);
        Recipe recipe = new Recipe(currentFood.getName(), foods, currentFood.getServing_size(), currentFood.getCalories());
        if (recipeList == null) {
            recipeList = new ArrayList<>();
        }
        recipeList.add(recipe);
        switch (meal) {
            case "BREAKFAST":
                dailyData.setBreakfast(recipeList);
                break;
            case "LUNCH":
                dailyData.setLunch(recipeList);
                break;
            case "DINNER":
                dailyData.setDinner(recipeList);
                break;
            case "SNACKS":
                dailyData.setSnacks(recipeList);
                break;
        }

        DailyDataDAO dailyDataDAO = new DailyDataDAOImpl();
        dailyDataDAO.update(dailyData);
        this.finish();
    }
}