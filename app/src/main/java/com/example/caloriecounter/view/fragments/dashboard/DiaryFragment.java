package com.example.caloriecounter.view.fragments.dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;

import com.example.caloriecounter.AddExerciseActivity;
import com.example.caloriecounter.AddFoodActivity;
import com.example.caloriecounter.R;
import com.example.caloriecounter.controllers.RecipeAdapter;
import com.example.caloriecounter.controllers.WorkoutAdapter;
import com.example.caloriecounter.model.DAO.DailyData;
import com.example.caloriecounter.model.DAO.DailyDataDAO;
import com.example.caloriecounter.model.DAO.DailyDataDAOImpl;
import com.example.caloriecounter.model.DAO.Food;
import com.example.caloriecounter.model.DAO.GoalData;
import com.example.caloriecounter.model.DAO.Recipe;
import com.example.caloriecounter.model.DAO.Workout;
import com.example.caloriecounter.model.dataHolder.DailyDataHolder;
import com.example.caloriecounter.model.dataHolder.GoalDataHolder;
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
            tvAddDinner, tvAddSnacks, tvAddExercise;

    private DailyData dailyData;
    private View view;

    private Context mContext;
    private String diaryDate;

    private int maxGramsOfProtein;
    private int maxGramsOfFat;
    private int maxGramsOfCarbs;
    private int calorieGoal;

    private ProgressBar pbProtein, pbFat, pbCarbs, pbCalories;

    private float gramsOfFat, gramsOfProtein, gramsOfCarbs;
    private int totalCalories;

    private static final int DAYS_TO_MOVE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_diary, container, false);
        init();

        calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        updateFoodDiary(currentDate);

        addListeners();
        return view;
    }

    private void addListeners() {
        ivNext.setOnClickListener(v -> moveForward());
        ivPrevious.setOnClickListener(v -> moveBackward());

        tvAddBreakfast.setOnClickListener(v -> addFood("Breakfast"));
        tvAddLunch.setOnClickListener(v -> addFood("Lunch"));
        tvAddDinner.setOnClickListener(v -> addFood("Dinner"));
        tvAddSnacks.setOnClickListener(v -> addFood("Snacks"));
        tvAddExercise.setOnClickListener(v -> addExercise());
    }

    private void addExercise() {
        Intent intent = new Intent(mContext, AddExerciseActivity.class);
        intent.putExtra("FROMDIARY", true);
        intent.putExtra("DATE", diaryDate);
        startActivity(intent);
    }

    private void addFood(String meal) {
        Intent intent = new Intent(mContext, AddFoodActivity.class);
        intent.putExtra("MEAL", meal);
        intent.putExtra("DATE", diaryDate);
        startActivity(intent);
    }

    private void moveForward() {
        playPageTransitionAnimation(AnimationUtils.loadAnimation(DiaryFragment.this.getContext(), R.anim.slide_right));

        calendar.add(Calendar.DAY_OF_YEAR, DAYS_TO_MOVE);
        Date newDate = calendar.getTime();
        updateFoodDiary(newDate);
    }

    private void moveBackward() {
        playPageTransitionAnimation(AnimationUtils.loadAnimation(DiaryFragment.this.getContext(), R.anim.slide_left));
        calendar.add(Calendar.DAY_OF_YEAR, -DAYS_TO_MOVE);
        Date newDate = calendar.getTime();
        updateFoodDiary(newDate);
    }

    private void updateFoodDiary(Date date) {
        diaryDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(date);
        tvDiaryDate.setText(diaryDate);
        getDailyData(diaryDate);
    }

    private void playPageTransitionAnimation(Animation animation) {
        view.startAnimation(animation);
    }

    private void getDailyData(String date) {
        DailyDataDAO dailyDataDAO = new DailyDataDAOImpl();
        dailyDataDAO.get(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dailyData = snapshot.getValue(DailyData.class);
                if (dailyData == null) {
                    dailyData = new DailyData();
                }
                loadData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadData() {
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
        ArrayAdapter<Recipe> adapter = new RecipeAdapter(mContext, arrayList);
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
        ArrayAdapter<Workout> adapter = new WorkoutAdapter(mContext, workoutList);
        lvWorkoutHistory.setAdapter(adapter);

        for (Workout workout : workoutList) {
            totalWorkoutCalories += workout.getCaloriesBurned();
        }

        tvTotalCaloriesExercise.setText(String.valueOf(totalWorkoutCalories));
        totalCalories = totalCalories- totalWorkoutCalories;
        setListViewHeightBasedOnItems(lvWorkoutHistory);
    }

    private void init() {
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

        tvDiaryDate = view.findViewById(R.id.tvDiaryDate);
        tvAddBreakfast = view.findViewById(R.id.addBreakfast);
        tvAddLunch = view.findViewById(R.id.addLunch);
        tvAddDinner = view.findViewById(R.id.addDinner);
        tvAddSnacks = view.findViewById(R.id.addSnacks);
        tvAddExercise = view.findViewById(R.id.addExercise);

        ivNext = view.findViewById(R.id.ivNext);
        ivPrevious = view.findViewById(R.id.ivPrevious);

        pbCalories = view.findViewById(R.id.pbTotalCalories);
        pbProtein = view.findViewById(R.id.pbTotalProtein);
        pbCarbs = view.findViewById(R.id.pbTotalCarbs);
        pbFat = view.findViewById(R.id.pbTotalFat);

        getCalorieGoal();
        initMacroValues();
        setMaxMacrosToUI();
    }

    private void setMaxMacrosToUI() {
        pbCalories.setMax(calorieGoal);
        pbProtein.setMax(maxGramsOfProtein);
        pbCarbs.setMax(maxGramsOfCarbs);
        pbFat.setMax(maxGramsOfFat);
    }

    // get calorie goal from goal data
    private void getCalorieGoal() {
        GoalData goalData = GoalDataHolder.getInstance().getData();
        if (goalData != null) {
            calorieGoal = Integer.parseInt(goalData.getCalorieGoal());
        } else {
            calorieGoal = 2000;
        }
    }

    // init total grams of fat, carbs and protein
    private void initMacroValues() {
        maxGramsOfProtein = (int) (calorieGoal * 0.3) / 4;
        maxGramsOfCarbs = (int) (0.4 * calorieGoal) / 4;
        maxGramsOfFat = (int) (0.3 * calorieGoal) / 9;
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

    @Override
    public void onResume() {
        super.onResume();

        Date currentDate = calendar.getTime();
        updateFoodDiary(currentDate);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }
}