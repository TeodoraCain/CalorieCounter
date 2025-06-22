package com.example.caloriecounter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.caloriecounter.adapters.FoodAdapter;
import com.example.caloriecounter.models.dao.Food;
import com.example.caloriecounter.models.dao.Recipe;
import com.example.caloriecounter.models.dao.RecipeDAO;
import com.example.caloriecounter.models.dao.RecipeDAOImpl;
import com.example.caloriecounter.models.dataHolders.RecipeListHolder;
import com.example.caloriecounter.models.dataModel.IntentKeys;
import com.example.caloriecounter.models.dataModel.IntentResults;
import com.example.caloriecounter.view.dialog.SuccessDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CreateRecipeActivity extends AppCompatActivity {

    private final String TAG = "RecipeCreationActivity";
    private ActivityResultLauncher<Intent> launcher;
    private Context context;
    private EditText etRecipeName;
    private EditText etPortions;
    private RecipeDAO recipeDAO;
    private FoodAdapter adapter;
    private List<Food> ingredientList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_creation);
        initActivity();
        setUpActivity();
    }

    //region Init Activity
    private void initContext() {
        context = this;
    }

    private void initDataDAO() {
        recipeDAO = new RecipeDAOImpl();
    }

    //endregion
    private void initActivity() {
        initContext();
        initDataDAO();
    }

    //region Set Up Activity
    private void setUpViews() {
        etRecipeName = findViewById(R.id.etRecipeName);
        etPortions = findViewById(R.id.etPortions);

        ImageView ivNewIngredient = findViewById(R.id.ivNewIngredient);
        ivNewIngredient.setOnClickListener(this::onGoToAddFood);

        ImageView ivSave = findViewById(R.id.ivSave);
        ivSave.setOnClickListener(v -> onSave());

        adapter = new FoodAdapter(context, ingredientList);
        ListView lvIngredientList = findViewById(R.id.lvIngredients);
        lvIngredientList.setAdapter(adapter);
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setUpLauncher() {
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == IntentResults.RESULT_ADD_INGREDIENT) {
                        Intent data = result.getData();
                        if (data != null && data.hasExtra(IntentKeys.INGREDIENT)) {
                            Food ingredient = (Food) data.getParcelableExtra(IntentKeys.INGREDIENT);
                            ingredientList.add(ingredient);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    //endregion
    private void setUpActivity() {
        setToolbar();
        setUpLauncher();
        setUpViews();
    }


    private void onSave() {
        String recipeName = etRecipeName.getText().toString();
        String servingSize = etPortions.getText().toString();

        if (recipeName.isEmpty()) {
            Toast.makeText(context, "Please add a name for this recipe before saving", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ingredientList.isEmpty()) {
            Toast.makeText(context, "Please add ingredients to this recipe before saving", Toast.LENGTH_SHORT).show();
            return;
        }

        if (servingSize.isEmpty()) {
            Toast.makeText(context, "Please add serving size to this recipe before saving", Toast.LENGTH_SHORT).show();
            return;
        }

        Recipe recipe = new Recipe(recipeName, ingredientList, Double.parseDouble(servingSize));
        List<Recipe> recipeList = RecipeListHolder.getInstance().getData();
        recipeList.add(recipe);
        recipeDAO.update(recipeList).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Log.i(TAG, "Recipe added successfully...");
                RecipeListHolder.getInstance().setData(recipeList);
                showSuccessDialog();// i want to end this activity after the dialog is shown
            }else {
                Log.d(TAG, "Recipe could not be saved..");
            }
        });

    }

    private void showSuccessDialog() {
        SuccessDialog successDialog = new SuccessDialog(context);
        successDialog.show();

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() -> runOnUiThread(() -> {
            successDialog.dismiss();
            finish();
        }), 2000, TimeUnit.MILLISECONDS);
    }


    /********************************* LIFECYCLE OVERRIDES ***********************************************/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onGoToAddFood(View v) {
        Intent intent = new Intent(context, AddFoodActivity.class);
        intent.putExtra(IntentKeys.IS_RECIPE_INGREDIENT, true);

        launcher.launch(intent);
    }
}
