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
import com.example.caloriecounter.models.dataModel.DefaultValue;
import com.example.caloriecounter.models.dataModel.IntentKeys;
import com.example.caloriecounter.models.dataModel.IntentResults;
import com.example.caloriecounter.models.dataModel.Meal;
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

/**
 * Activity to display detailed nutritional information for a selected food item or recipe.
 * <p>
 * This screen allows users to:
 * - View and edit serving size.
 * - See updated nutritional values in real-time.
 * - Add food or recipe to a selected meal on a given date.
 * <p>
 * Handles different use-cases:
 * - Displaying a single food.
 * - Displaying a recipe composed of multiple ingredients.
 * - Adding ingredients to a new recipe.
 * - Saving data for today or a different diary date.
 */
public class NutritionDisplayActivity extends AppCompatActivity {

    // attributes
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
    private Context context;
    private DailyDataDAO dailyDataDAO;
    //passed from intent
    private Recipe selectedRecipe;
    private Food selectedFood;
    private String selectedMeal;
    private String diaryDate;
    private boolean isRecipeIngredient;
    private boolean isRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition_display);

        initActivity();
        setUpActivity();
    }

    //region Init Activity
    private void initContext() {
        context = NutritionDisplayActivity.this;
    }

    /**
     * Parses data from the intent to determine selected food/recipe,
     * diary date, meal type, and usage context (e.g., ingredient or full recipe).
     */
    private void parseIntentExtras() {
        Intent intent = getIntent();
        selectedFood = intent.getParcelableExtra(IntentKeys.FOOD);
        selectedMeal = intent.getStringExtra(IntentKeys.MEAL);
        diaryDate = intent.getStringExtra(IntentKeys.DATE);
        isRecipeIngredient = intent.getBooleanExtra(IntentKeys.IS_RECIPE_INGREDIENT, false);
        isRecipe = intent.getBooleanExtra(IntentKeys.IS_RECIPE, false);
        selectedRecipe = intent.getParcelableExtra(IntentKeys.RECIPE);
    }

    private void initDailyDataDAO() {
        dailyDataDAO = new DailyDataDAOImpl();
    }

    //endregion
    private void initActivity() {
        initContext();
        initDailyDataDAO();
        parseIntentExtras();
    }

    //region Set Up Activity

    /**
     * Configures the toolbar with title and back button.
     */
    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);

        //set title
        String title = getString(R.string.food);
        if (selectedFood != null) {
            title = selectedFood.getName();
        } else if (selectedRecipe != null) {
            title = selectedRecipe.getName();
        }
        toolbar.setTitle(title);

        //set back button
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /**
     * Initializes view references and sets up listener for save button.
     */
    private void setUpViews() {
        if (isRecipe) {
            EditText etServingSizeTxt = findViewById(R.id.etServingSizeTxt);
            etServingSizeTxt.setText(R.string.portions);
        }
        etServingSize = findViewById(R.id.etServingSize);
        etServingSize.setOnClickListener(v -> clearInput());
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

        ImageView saveImageView = findViewById(R.id.ivSave);
        saveImageView.setOnClickListener(v -> onSave());
    }

    private void clearInput() {
        etServingSize.setText("");
    }

    /**
     * Sets up a text watcher for serving size to recalculate nutrition values in real-time.
     */
    void setUpTextWatcherForServingSize() {
        etServingSize.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    double imputedServingSize = s.length() > 0 ? Double.parseDouble(s.toString()) : DefaultValue.DEFAULT_SERVING_SIZE;
                    double servingSize = DefaultValue.DEFAULT_SERVING_SIZE, percentage;
                    if (selectedFood != null) {
                        servingSize = selectedFood.getServing_size();
                    } else if (selectedRecipe != null) {
                        servingSize = selectedRecipe.getServing_size();
                    }
                    if (servingSize == 0) {
                        return;
                    }

                    percentage = ((imputedServingSize * 100) / servingSize) / 100;
                    setModifiedTexts(percentage);
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Invalid serving size input", e);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    /**
     * Updates the UI with scaled nutrition values based on serving size input.
     *
     * @param percentage multiplier derived from the serving size entered by user
     */
    void setModifiedTexts(double percentage) {
        double calories = 0, carbs = 0, protein = 0, totalFat = 0, satFat = 0;
        double fiber = 0, iron = 0, sugar = 0, sodium = 0, calcium = 0, magnesium = 0;
        double vitaminA = 0, vitaminB6 = 0, vitaminB12 = 0, vitaminC = 0;

        if (selectedFood != null) {
            Food food = getScaledFood(selectedFood, percentage);
            calories = food.getCalories();
            carbs = food.getCarbohydrate();
            protein = food.getProtein();
            totalFat = food.getTotal_fat();
            satFat = food.getSaturated_fat();
            fiber = food.getFiber();
            iron = food.getIron();
            sugar = food.getSugars();
            sodium = food.getSodium();
            calcium = food.getCalcium();
            magnesium = food.getMagnesium();
            vitaminA = food.getVitamin_a();
            vitaminB6 = food.getVitamin_b6();
            vitaminB12 = food.getVitamin_b12();
            vitaminC = food.getVitamin_c();

        } else if (selectedRecipe != null && selectedRecipe.getIngredients() != null) {
            List<Food> modifiedIngredients = new ArrayList<>();
            for (Food food : selectedRecipe.getIngredients()) {
                food = getScaledFood(food, percentage);
                calories += food.getCalories();
                carbs += food.getCarbohydrate();
                protein += food.getProtein();
                totalFat += food.getTotal_fat();
                satFat += food.getSaturated_fat();
                fiber += food.getFiber();
                iron += food.getIron();
                sugar += food.getSugars();
                sodium += food.getSodium();
                calcium += food.getCalcium();
                magnesium += food.getMagnesium();
                vitaminA += food.getVitamin_a();
                vitaminB6 += food.getVitamin_b6();
                vitaminB12 += food.getVitamin_b12();
                vitaminC += food.getVitamin_c();
                modifiedIngredients.add(food);
            }
            selectedRecipe.setServing_size(selectedRecipe.getServing_size() * percentage);
            selectedRecipe.setIngredients(modifiedIngredients);
        }

        tvCalories.setText(String.format(Locale.ENGLISH, "%.1f", calories));
        tvCarbohydrate.setText(String.format(Locale.ENGLISH, "%.1f", carbs));
        tvProtein.setText(String.format(Locale.ENGLISH, "%.1f", protein));
        tvTotalFat.setText(String.format(Locale.ENGLISH, "%.1f", totalFat));
        tvSaturatedFat.setText(String.format(Locale.ENGLISH, "%.1f", satFat));
        tvFiber.setText(String.format(Locale.ENGLISH, "%.1f", fiber));
        tvIron.setText(String.format(Locale.ENGLISH, "%.1f", iron));
        tvSugar.setText(String.format(Locale.ENGLISH, "%.1f", sugar));
        tvSodium.setText(String.format(Locale.ENGLISH, "%.1f", sodium));
        tvCalcium.setText(String.format(Locale.ENGLISH, "%.1f", calcium));
        tvMagnesium.setText(String.format(Locale.ENGLISH, "%.1f", magnesium));
        tvVitaminA.setText(String.format(Locale.ENGLISH, "%.1f", vitaminA));
        tvVitaminB6.setText(String.format(Locale.ENGLISH, "%.1f", vitaminB6));
        tvVitaminB12.setText(String.format(Locale.ENGLISH, "%.1f", vitaminB12));
        tvVitaminC.setText(String.format(Locale.ENGLISH, "%.1f", vitaminC));
    }

    /**
     * Scales a Food object based on the provided multiplier.
     *
     * @param original   the original food object
     * @param percentage the scale factor
     * @return a new scaled Food object
     */
    private Food getScaledFood(Food original, double percentage) {
        Food scaled = new Food();

        scaled.setCalories(original.getCalories() * percentage);
        scaled.setCarbohydrate(original.getCarbohydrate() * percentage);
        scaled.setProtein(original.getProtein() * percentage);
        scaled.setTotal_fat(original.getTotal_fat() * percentage);
        scaled.setSaturated_fat(original.getSaturated_fat() * percentage);
        scaled.setFiber(original.getFiber() * percentage);
        scaled.setIron(original.getIron() * percentage);
        scaled.setSugars(original.getSugars() * percentage);
        scaled.setSodium(original.getSodium() * percentage);
        scaled.setCalcium(original.getCalcium() * percentage);
        scaled.setMagnesium(original.getMagnesium() * percentage);
        scaled.setVitamin_a(original.getVitamin_a() * percentage);
        scaled.setVitamin_b6(original.getVitamin_b6() * percentage);
        scaled.setVitamin_b12(original.getVitamin_b12() * percentage);
        scaled.setVitamin_c(original.getVitamin_c() * percentage);

        scaled.setName(original.getName());
        scaled.setServing_size(original.getServing_size() * percentage);

        return scaled;
    }

    //endregion
    private void setUpActivity() {
        setToolbar();
        setUpViews();
        setUpTextWatcherForServingSize();
    }

    //region Save Recipe

    /**
     * Saves the current food or recipe to the diary or recipe being created.
     * Handles saving for both today and other selected dates.
     */
    void onSave() {
        // Adding ingredient to new recipe list
        if (isRecipeIngredient) {
            Log.d(TAG, "Adding ingredient to recipe and returning to intent...");
            Food food = createFoodItemFromUI();

            Intent recipeIntent = new Intent();
            recipeIntent.putExtra(IntentKeys.INGREDIENT, food);
            setResult(IntentResults.RESULT_ADD_INGREDIENT, recipeIntent);
            finish();

            return;
        }
        // Adding food in diary for a different date than the current one
        if (diaryDate != null && !diaryDate.equals(getCurrentDate())) {
            Log.d(TAG, "Saving food to diary for date " + diaryDate + " ...");
            dailyDataDAO.get(diaryDate).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    DailyData dailyData = snapshot.getValue(DailyData.class);
                    dailyData = (dailyData == null) ? new DailyData() : dailyData;

                    updateDailyDataRecipes(dailyData);
                    saveDailyDataToDBByDate(dailyData, diaryDate);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Diary data update failed: " + error.getMessage());
                }
            });
        } else { // Adding food to today's food diary (from both diary and home page)
            Log.d(TAG, "Saving food to diary for date " + diaryDate + " ...");
            DailyData dailyData = DailyDataHolder.getInstance().getData();
            dailyData = (dailyData == null) ? new DailyData() : dailyData;

            updateDailyDataRecipes(dailyData);
            saveDailyDataToDB(dailyData);
            DailyDataHolder.getInstance().setData(dailyData);
        }
    }

    /**
     * Returns the current date in a "dd-MM-yyyy" format.
     */
    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        return new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(date);
    }

    /**
     * Updates the list of recipe for a specific diary entry.
     *
     * @param dailyData the daily data for which the recipe update should happen
     */
    private void updateDailyDataRecipes(DailyData dailyData) {
        List<Recipe> recipeList = getRecipeListByMealType(selectedMeal, dailyData);
        if (isRecipe && selectedRecipe != null) {
            recipeList.add(selectedRecipe);
        } else {
            Food currentFood = createFoodItemFromUI();
            List<Food> foods = new ArrayList<>();
            foods.add(currentFood);

            Recipe recipe = createRecipeFromFood(currentFood, foods);
            recipeList.add(recipe);
        }
        updateDailyDataWithRecipeList(selectedMeal, recipeList, dailyData);
    }

    /**
     * Creates a one-item recipe from a Food object.
     *
     * @param food  the base food
     * @param foods list containing the food (for consistency)
     * @return new Recipe instance
     */
    Recipe createRecipeFromFood(Food food, List<Food> foods) {
        return new Recipe(food.getName(), foods, food.getServing_size());
    }

    /**
     * Returns the list of recipes based on the selected meal type from the DailyData object.
     *
     * @param meal      meal category string
     * @param dailyData user's daily data
     * @return list of recipes for the meal
     */
    private List<Recipe> getRecipeListByMealType(String meal, DailyData dailyData) {
        switch (meal.toUpperCase()) {
            case Meal.BREAKFAST:
                return dailyData.getBreakfast();
            case Meal.LUNCH:
                return dailyData.getLunch();
            case Meal.DINNER:
                return dailyData.getDinner();
            case Meal.SNACKS:
                return dailyData.getSnacks();
            default:
                return new ArrayList<>();
        }
    }

    private void saveDailyDataToDB(DailyData dailyData) {
        dailyDataDAO.update(dailyData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Data: Updated " + selectedMeal + " for " + getCurrentDate());

                Intent intent;
                if (diaryDate != null) {
                    intent = new Intent(context, DashboardActivity.class);
                    intent.putExtra(IntentKeys.NAVIGATE_TO_DIARY_FRAGMENT, true);
                } else {
                    intent = new Intent(this, DashboardActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }
                startActivity(intent);
                finish();
            } else {
                Log.e("SaveData", "Failed to update data", task.getException());
            }
        });
    }

    /**
     * Updates the DailyData object with the modified recipe list based on meal type.
     *
     * @param meal       the meal category (breakfast, lunch, etc.)
     * @param recipeList list of recipes for the meal
     * @param dailyData  the full DailyData object to be updated
     */
    private void updateDailyDataWithRecipeList(String meal, List<Recipe> recipeList, DailyData dailyData) {
        switch (meal.toUpperCase()) {
            case Meal.BREAKFAST:
                dailyData.setBreakfast(recipeList);
                break;
            case Meal.LUNCH:
                dailyData.setLunch(recipeList);
                break;
            case Meal.DINNER:
                dailyData.setDinner(recipeList);
                break;
            case Meal.SNACKS:
                dailyData.setSnacks(recipeList);
                break;
        }
    }

    /**
     * Saves updated daily data to Firebase for a specific date.
     *
     * @param dailyData the data to save
     * @param date      the diary date to update
     */
    void saveDailyDataToDBByDate(DailyData dailyData, String date) {
        dailyDataDAO.update(dailyData, date).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Data: Updated " + selectedMeal + " for " + date);

                Intent intent = new Intent(context, DashboardActivity.class);
                intent.putExtra(IntentKeys.NAVIGATE_TO_DIARY_FRAGMENT, true);
                startActivity(intent);

            } else {
                Log.e(TAG, "Failed to update data", task.getException());
            }
        });
    }
    //endregion

    //region UI Transformations

    /**
     * Creates a Food object from current UI values.
     *
     * @return constructed Food object
     */
    Food createFoodItemFromUI() {
        Food food = new Food();
        food.setName(selectedFood.getName());
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

    void setTextsToUI() {
        double servingSize = 0, calories = 0, carbs = 0, protein = 0, totalFat = 0, satFat = 0;
        double fiber = 0, iron = 0, sugar = 0, sodium = 0, calcium = 0, magnesium = 0;
        double vitaminA = 0, vitaminB6 = 0, vitaminB12 = 0, vitaminC = 0;

        if (selectedFood != null) {
            servingSize = selectedFood.getServing_size();
            calories = selectedFood.getCalories();
            carbs = selectedFood.getCarbohydrate();
            protein = selectedFood.getProtein();
            totalFat = selectedFood.getTotal_fat();
            satFat = selectedFood.getSaturated_fat();
            fiber = selectedFood.getFiber();
            iron = selectedFood.getIron();
            sugar = selectedFood.getSugars();
            sodium = selectedFood.getSodium();
            calcium = selectedFood.getCalcium();
            magnesium = selectedFood.getMagnesium();
            vitaminA = selectedFood.getVitamin_a();
            vitaminB6 = selectedFood.getVitamin_b6();
            vitaminB12 = selectedFood.getVitamin_b12();
            vitaminC = selectedFood.getVitamin_c();

        } else if (selectedRecipe != null && selectedRecipe.getIngredients() != null) {
            servingSize += selectedRecipe.getServing_size();
            for (Food food : selectedRecipe.getIngredients()) {
                calories += food.getCalories();
                carbs += food.getCarbohydrate();
                protein += food.getProtein();
                totalFat += food.getTotal_fat();
                satFat += food.getSaturated_fat();
                fiber += food.getFiber();
                iron += food.getIron();
                sugar += food.getSugars();
                sodium += food.getSodium();
                calcium += food.getCalcium();
                magnesium += food.getMagnesium();
                vitaminA += food.getVitamin_a();
                vitaminB6 += food.getVitamin_b6();
                vitaminB12 += food.getVitamin_b12();
                vitaminC += food.getVitamin_c();
            }
        }
        etServingSize.setText(String.format(Locale.ENGLISH, "%.0f", servingSize));
        tvCalories.setText(String.format(Locale.ENGLISH, "%.1f", calories));
        tvCarbohydrate.setText(String.format(Locale.ENGLISH, "%.1f", carbs));
        tvProtein.setText(String.format(Locale.ENGLISH, "%.1f", protein));
        tvTotalFat.setText(String.format(Locale.ENGLISH, "%.1f", totalFat));
        tvSaturatedFat.setText(String.format(Locale.ENGLISH, "%.1f", satFat));
        tvFiber.setText(String.format(Locale.ENGLISH, "%.1f", fiber));
        tvIron.setText(String.format(Locale.ENGLISH, "%.1f", iron));
        tvSugar.setText(String.format(Locale.ENGLISH, "%.1f", sugar));
        tvSodium.setText(String.format(Locale.ENGLISH, "%.1f", sodium));
        tvCalcium.setText(String.format(Locale.ENGLISH, "%.1f", calcium));
        tvMagnesium.setText(String.format(Locale.ENGLISH, "%.1f", magnesium));
        tvVitaminA.setText(String.format(Locale.ENGLISH, "%.1f", vitaminA));
        tvVitaminB6.setText(String.format(Locale.ENGLISH, "%.1f", vitaminB6));
        tvVitaminB12.setText(String.format(Locale.ENGLISH, "%.1f", vitaminB12));
        tvVitaminC.setText(String.format(Locale.ENGLISH, "%.1f", vitaminC));
    }
    //endregion

    public void setSelectedFood(Food selectedFood) {
        this.selectedFood = selectedFood;
    }

    /********************************* LIFECYCLE OVERRIDES ****************************************************/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setTextsToUI();
    }


}
