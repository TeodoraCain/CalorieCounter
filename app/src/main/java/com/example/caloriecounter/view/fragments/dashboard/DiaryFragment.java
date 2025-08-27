package com.example.caloriecounter.view.fragments.dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.caloriecounter.AddExerciseActivity;
import com.example.caloriecounter.AddFoodActivity;
import com.example.caloriecounter.AddRecipeActivity;
import com.example.caloriecounter.R;
import com.example.caloriecounter.adapters.RecipeAdapter;
import com.example.caloriecounter.adapters.WorkoutAdapter;
import com.example.caloriecounter.models.dao.DailyData;
import com.example.caloriecounter.models.dao.DailyDataDAO;
import com.example.caloriecounter.models.dao.DailyDataDAOImpl;
import com.example.caloriecounter.models.dao.Food;
import com.example.caloriecounter.models.dao.GoalData;
import com.example.caloriecounter.models.dao.Recipe;
import com.example.caloriecounter.models.dao.Workout;
import com.example.caloriecounter.models.dataHolders.DailyDataHolder;
import com.example.caloriecounter.models.dataHolders.GoalDataHolder;
import com.example.caloriecounter.models.dataModel.DefaultValue;
import com.example.caloriecounter.models.dataModel.IntentKeys;
import com.example.caloriecounter.models.dataModel.Meal;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DiaryFragment extends Fragment {

    private final int DAYS_TO_MOVE = 1;
    private final String TAG = "DiaryFragment";
    private Context context;
    //views
    private TextView tvTotalCaloriesExercise;
    private TextView tvTotalBreakfastCalories;
    private TextView tvTotalLunchCalories;
    private TextView tvTotalDinnerCalories;
    private TextView tvTotalSnackCalories;
    private TextView tvTotalCaloriesCount;
    private TextView tvTotalProteinCount;
    private TextView tvTotalCarbsCount;
    private TextView tvTotalFatCount;
    private TextView tvDiaryDate;
    private Calendar calendar;
    private ImageView ivPrevious, ivNext;
    private ListView lvWorkoutHistory;
    private ListView lvBreakfast;
    private ListView lvLunch;
    private ListView lvDinner;
    private ListView lvSnacks;
    private TextView tvAddBreakfast, tvAddLunch,
            tvAddDinner, tvAddSnacks, tvAddExercise,
            tvAddBreakfastRecipe, tvAddLunchRecipe,
            tvAddDinnerRecipe, tvAddSnacksRecipe;
    private ProgressBar pbProtein, pbFat, pbCarbs, pbCalories;
    // attributes
    private int maxGramsOfProtein;
    private int maxGramsOfFat;
    private int maxGramsOfCarbs;
    private int calorieGoal;
    private int totalCalories;
    private float gramsOfFat, gramsOfProtein, gramsOfCarbs;
    private String diaryDate;
    private DailyData dailyData;
    private DailyDataDAO dailyDataDAO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary, container, false);
        setUpViews(view);
        getCalorieGoal();
        initMacroValues();
        setMaxMacrosToUI();
        addListeners(view);
        updateFoodDiary(getCurrentDate());

        return view;
    }

    /********************************* INIT VIEW ***************************************************/
    private void setUpViews(View view) {
        tvTotalCaloriesExercise = view.findViewById(R.id.tvTotalExerciseCalories);
        tvTotalBreakfastCalories = view.findViewById(R.id.tvTotalBreakfastCalories);
        tvTotalLunchCalories = view.findViewById(R.id.tvTotalLunchCalories);
        tvTotalDinnerCalories = view.findViewById(R.id.tvTotalDinnerCalories);
        tvTotalSnackCalories = view.findViewById(R.id.tvTotalSnacksCalories);

        tvTotalCaloriesCount = view.findViewById(R.id.tvTotalCalorieCount);
        tvTotalProteinCount = view.findViewById(R.id.tvTotalProteinCount);
        tvTotalCarbsCount = view.findViewById(R.id.tvTotalCarbsCount);
        tvTotalFatCount = view.findViewById(R.id.tvTotalFatCount);

        lvBreakfast = view.findViewById(R.id.lvBreakfast);
        lvLunch = view.findViewById(R.id.lvLunch);
        lvDinner = view.findViewById(R.id.lvDinner);
        lvSnacks = view.findViewById(R.id.lvSnacks);
        lvWorkoutHistory = view.findViewById(R.id.lvWorkoutHistory);

        dailyData = DailyDataHolder.getInstance().getData();
        calendar = Calendar.getInstance();

        tvDiaryDate = view.findViewById(R.id.tvDiaryDate);
        tvAddBreakfast = view.findViewById(R.id.addBreakfast);
        tvAddLunch = view.findViewById(R.id.addLunch);
        tvAddDinner = view.findViewById(R.id.addDinner);
        tvAddSnacks = view.findViewById(R.id.addSnacks);
        tvAddExercise = view.findViewById(R.id.addExercise);

        tvAddBreakfastRecipe = view.findViewById(R.id.addBreakfastRecipe);
        tvAddLunchRecipe = view.findViewById(R.id.addLunchRecipe);
        tvAddDinnerRecipe = view.findViewById(R.id.addDinnerRecipe);
        tvAddSnacksRecipe = view.findViewById(R.id.addSnacksRecipe);

        ivNext = view.findViewById(R.id.ivNext);
        ivPrevious = view.findViewById(R.id.ivPrevious);

        pbCalories = view.findViewById(R.id.pbTotalCalories);
        pbProtein = view.findViewById(R.id.pbTotalProtein);
        pbCarbs = view.findViewById(R.id.pbTotalCarbs);
        pbFat = view.findViewById(R.id.pbTotalFat);
    }

    // get calorie goal from goal data
    private void getCalorieGoal() {
        GoalData goalData = GoalDataHolder.getInstance().getData();
        if (goalData != null) {
            calorieGoal = Integer.parseInt(goalData.getCalorieGoal());
        } else {
            calorieGoal = DefaultValue.CALORIE_GOAL;
        }
    }

    // init total grams of fat, carbs and protein
    private void initMacroValues() {
        maxGramsOfProtein = (int) (calorieGoal * 0.3) / 4;
        maxGramsOfCarbs = (int) (calorieGoal * 0.5) / 4;
        maxGramsOfFat = (int) (calorieGoal * 0.3) / 9;
    }

    private void setMaxMacrosToUI() {
        pbCalories.setMax(calorieGoal);
        pbProtein.setMax(maxGramsOfProtein);
        pbCarbs.setMax(maxGramsOfCarbs);
        pbFat.setMax(maxGramsOfFat);
    }

    private void addListeners(View view) {
        ivNext.setOnClickListener(v -> moveForward(view));
        ivPrevious.setOnClickListener(v -> moveBackward(view));

        tvAddBreakfast.setOnClickListener(v -> addFood(Meal.BREAKFAST));
        tvAddLunch.setOnClickListener(v -> addFood(Meal.LUNCH));
        tvAddDinner.setOnClickListener(v -> addFood(Meal.DINNER));
        tvAddSnacks.setOnClickListener(v -> addFood(Meal.SNACKS));
        tvAddExercise.setOnClickListener(v -> addExercise());

        tvAddBreakfastRecipe.setOnClickListener(v -> addRecipe(Meal.BREAKFAST));
        tvAddLunchRecipe.setOnClickListener(v -> addRecipe(Meal.LUNCH));
        tvAddDinnerRecipe.setOnClickListener(v -> addRecipe(Meal.DINNER));
        tvAddSnacksRecipe.setOnClickListener(v -> addRecipe(Meal.SNACKS));
//        //delete from list
        lvWorkoutHistory.setOnItemLongClickListener((parent, v, position, id) -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setMessage("Are you sure you want to delete this workout?");
            alertDialog.setPositiveButton("ok", (dialog, which) -> {
                List<Workout> workoutList = dailyData.getWorkouts();
                workoutList.remove(position);
                dailyData.setWorkouts(workoutList);
                setWorkouts();
                saveChanges();
            });
            alertDialog.setNegativeButton("cancel", (dialog, which) -> {
            });
            alertDialog.create().show();
            return true;
        });

        lvBreakfast.setOnItemLongClickListener((parent, v, position, id) -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setMessage("Are you sure you want to delete this recipe?");
            alertDialog.setPositiveButton("ok", (dialog, which) -> {
                List<Recipe> recipeList = dailyData.getBreakfast();
                recipeList.remove(position);
                dailyData.setBreakfast(recipeList);
                setFoodList(recipeList, tvTotalBreakfastCalories, lvBreakfast);
                saveChanges();
            });
            alertDialog.setNegativeButton("cancel", (dialog, which) -> {
            });
            alertDialog.create().show();
            return true;
        });
        lvLunch.setOnItemLongClickListener((parent, v, position, id) -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setMessage("Are you sure you want to delete this recipe?");
            alertDialog.setPositiveButton("ok", (dialog, which) -> {
                List<Recipe> recipeList = dailyData.getLunch();
                recipeList.remove(position);
                dailyData.setLunch(recipeList);
                setFoodList(recipeList, tvTotalLunchCalories, lvLunch);
                saveChanges();
            });
            alertDialog.setNegativeButton("cancel", (dialog, which) -> {
            });
            alertDialog.create().show();
            return true;
        });
        lvDinner.setOnItemLongClickListener((parent, v, position, id) -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setMessage("Are you sure you want to delete this recipe?");
            alertDialog.setPositiveButton("ok", (dialog, which) -> {
                List<Recipe> recipeList = dailyData.getDinner();
                recipeList.remove(position);
                dailyData.setDinner(recipeList);
                setFoodList(recipeList, tvTotalDinnerCalories, lvDinner);
                saveChanges();
            });
            alertDialog.setNegativeButton("cancel", (dialog, which) -> {
            });
            alertDialog.create().show();
            return true;
        });

        lvSnacks.setOnItemLongClickListener((parent, v, position, id) -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setMessage("Are you sure you want to delete this recipe?");
            alertDialog.setPositiveButton("ok", (dialog, which) -> {
                List<Recipe> recipeList = dailyData.getSnacks();
                recipeList.remove(position);
                dailyData.setSnacks(recipeList);
                setFoodList(recipeList, tvTotalSnackCalories, lvSnacks);
                saveChanges();
            });
            alertDialog.setNegativeButton("cancel", (dialog, which) -> {
            });
            alertDialog.create().show();
            return true;
        });

    }

    private void saveChanges() {
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(getCurrentDate());
        if (diaryDate.equals(currentDate)) {
            updateDailyDataToDB();
        } else {
            updateDailyDataToDB(diaryDate);
        }
    }

    @NonNull
    private Date getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    /********************************* DATABASE ACCESS ********************************************/
    private void updateDailyDataToDB(String diaryDate) {
        dailyDataDAO.update(dailyData, diaryDate).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DailyDataHolder.getInstance().setData(dailyData);
                Log.d(TAG, "Daily data updated successfully!");
            } else {
                Log.d(TAG, "Daily data update went wrong!");
            }
        });
    }

    private void updateDailyDataToDB() {
        dailyDataDAO.update(dailyData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DailyDataHolder.getInstance().setData(dailyData);
                Log.d(TAG, "Daily data updated successfully!");
            } else {
                Log.d(TAG, "Daily data update went wrong!");
            }
        });
    }

    /********************************* INIT DATA ***************************************************/

    private void updateFoodDiary(Date date) {
        diaryDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(date);
        tvDiaryDate.setText(diaryDate);
        getDailyData(diaryDate);
    }

    private void getDailyData(String date) {
        dailyDataDAO.get(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dailyData = snapshot.getValue(DailyData.class);
                if (dailyData == null) {
                    dailyData = new DailyData();
                }
                loadAndSetDataToUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadAndSetDataToUI() {
        List<Recipe> breakfastList = dailyData.getBreakfast();
        List<Recipe> lunchList = dailyData.getLunch();
        List<Recipe> dinnerList = dailyData.getDinner();
        List<Recipe> snacksList = dailyData.getSnacks();

        totalCalories = 0;
        gramsOfFat = 0;
        gramsOfCarbs = 0;
        gramsOfProtein = 0;

        if (breakfastList == null) {
            breakfastList = new ArrayList<>();
        }
        setFoodList(breakfastList, tvTotalBreakfastCalories, lvBreakfast);

        if (lunchList == null) {
            lunchList = new ArrayList<>();
        }
        setFoodList(lunchList, tvTotalLunchCalories, lvLunch);

        if (dinnerList == null) {
            dinnerList = new ArrayList<>();
        }
        setFoodList(dinnerList, tvTotalDinnerCalories, lvDinner);

        if (snacksList == null) {
            snacksList = new ArrayList<>();
        }
        setFoodList(snacksList, tvTotalSnackCalories, lvSnacks);

        setWorkouts();

        tvTotalCaloriesCount.setText(MessageFormat.format("{0} / {1} kcal", totalCalories, calorieGoal));
        tvTotalProteinCount.setText(MessageFormat.format("{0} / {1} g", gramsOfProtein, maxGramsOfProtein));
        tvTotalCarbsCount.setText(MessageFormat.format("{0} / {1} g", gramsOfCarbs, maxGramsOfCarbs));
        tvTotalFatCount.setText(MessageFormat.format("{0} / {1} g", gramsOfFat, maxGramsOfFat));

        pbCalories.setProgress(totalCalories);
        pbProtein.setProgress((int) gramsOfProtein);
        pbCarbs.setProgress((int) gramsOfCarbs);
        pbFat.setProgress((int) gramsOfFat);
    }

    private void setFoodList(List<Recipe> arrayList, TextView textView, ListView listView) {
        ArrayAdapter<Recipe> adapter = new RecipeAdapter(context, arrayList);
        listView.setAdapter(adapter);

        int totalCaloriesMeal = 0;
        for (Recipe recipe : arrayList) {
            totalCaloriesMeal += recipe.getCalories();
            totalCalories += recipe.getCalories();
            for (Food food : recipe.getIngredients()) {
                gramsOfProtein += food.getProtein();
                gramsOfCarbs += food.getCarbohydrate();
                gramsOfFat += food.getTotal_fat();
            }
        }
        textView.setText(String.valueOf(totalCaloriesMeal));
        setListViewHeightBasedOnItems(listView);
    }

    private void setWorkouts() {
        int totalWorkoutCalories = 0;
        List<Workout> workoutList = dailyData.getWorkouts();
        if (workoutList == null || workoutList.isEmpty()) {
            workoutList = new ArrayList<>();
        }
        ArrayAdapter<Workout> adapter = new WorkoutAdapter(context, workoutList);
        lvWorkoutHistory.setAdapter(adapter);

        for (Workout workout : workoutList) {
            totalWorkoutCalories += workout.getCaloriesBurned();
        }

        tvTotalCaloriesExercise.setText(String.valueOf(totalWorkoutCalories));
        totalCalories = totalCalories - totalWorkoutCalories;
        setListViewHeightBasedOnItems(lvWorkoutHistory);
    }

    private void setListViewHeightBasedOnItems(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    /********************************* ONCLICK ACTIONS *********************************************/
    private void addExercise() {
        Intent intent = new Intent(context, AddExerciseActivity.class);
        intent.putExtra(IntentKeys.FROMDIARY, true);
        intent.putExtra(IntentKeys.DATE, diaryDate);
        startActivity(intent);
    }

    private void addFood(String meal) {
        Intent intent = new Intent(context, AddFoodActivity.class);
        intent.putExtra(IntentKeys.MEAL, meal);
        intent.putExtra(IntentKeys.DATE, diaryDate);
        startActivity(intent);
    }

    private void addRecipe(String meal) {
        Intent intent = new Intent(context, AddRecipeActivity.class);
        intent.putExtra(IntentKeys.MEAL, meal);
        intent.putExtra(IntentKeys.DATE, diaryDate);
        startActivity(intent);
    }

    /********************************* VIEW ACTIONS ************************************************/
    private void moveForward(View view) {
        playPageTransitionAnimation(AnimationUtils.loadAnimation(DiaryFragment.this.getContext(), R.anim.slide_right), view);
        calendar.add(Calendar.DAY_OF_YEAR, DAYS_TO_MOVE);
        Date newDate = calendar.getTime();
        updateFoodDiary(newDate);
    }

    private void moveBackward(View view) {
        playPageTransitionAnimation(AnimationUtils.loadAnimation(DiaryFragment.this.getContext(), R.anim.slide_left), view);
        calendar.add(Calendar.DAY_OF_YEAR, -DAYS_TO_MOVE);
        Date newDate = calendar.getTime();
        updateFoodDiary(newDate);
    }

    private void playPageTransitionAnimation(Animation animation, View view) {
        view.startAnimation(animation);
    }

    /*********************************** LIFECYCLE OVERRIDES ***************************************/
    @Override
    public void onResume() {
        super.onResume();

        Date currentDate = calendar.getTime();
        updateFoodDiary(currentDate);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        this.dailyDataDAO = new DailyDataDAOImpl();
    }
}