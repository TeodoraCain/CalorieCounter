package com.example.caloriecounter.view.fragments.dashboard;

import static android.content.Context.SENSOR_SERVICE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.caloriecounter.ExerciseActivity;
import com.example.caloriecounter.R;
import com.example.caloriecounter.model.DAO.DailyData;
import com.example.caloriecounter.model.DAO.DailyDataDAO;
import com.example.caloriecounter.model.DAO.DailyDataDAOImpl;
import com.example.caloriecounter.model.DAO.GoalData;
import com.example.caloriecounter.model.DAO.Recipe;
import com.example.caloriecounter.model.DAO.UserDetails;
import com.example.caloriecounter.model.dataHolder.DailyDataHolder;
import com.example.caloriecounter.model.dataHolder.GoalDataHolder;
import com.example.caloriecounter.model.dataHolder.UserDetailsHolder;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class HomeFragment extends Fragment implements SensorEventListener {

    private TextView weightCategory;
    private double weight;
    private String weightUnit;
    private int height;
    private String heightUnit;
    private double bmi;
    private Typeface TF;
    private View view;

    //water intake management
    private final int waterUnits = 200;
    private int waterIntake;
    private int waterGoal;

    //user data
    private UserDetails userDetails;
    private DailyData dailyData;
    private GoalData goalData;

    private TextView tvGoalCalories, tvStepGoal,
            tvTotalCalories, tvTotalFoodCalories, tvTotalExerciseCalories,
            tvCaloriesConsumedExercise, tvTimeElapsedExercise,
            tvBreakfastCalories, tvLunchCalories,
            tvDinnerCalories, tvSnacksCalories;

    private View addWaterIntake;
    private ProgressBar pbTotalCalories;

    //step counter
    private SensorManager sensorManager = null;
    private Sensor stepSensor;
    private int totalSteps = 0;
    private int currentSteps;
    private int previousTotalSteps = 0;
    private ProgressBar pbSteps;
    private TextView tvStepCount;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);

        init();
        setDataToView();
        setWaterIntake();
        resetSteps();


        addWaterIntake.setOnClickListener(v -> {
            waterIntake += waterUnits;

            if (waterIntake >= waterGoal) {
                Toast.makeText(container.getContext(), "Awesome! You reached your water goal for today!", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(container.getContext(), MessageFormat.format("Good job! {0}ml added", waterUnits), Toast.LENGTH_SHORT).show();

            dailyData.setWaterDrank(waterIntake);
            DailyDataDAO dailyDataDAO = new DailyDataDAOImpl();
            dailyDataDAO.update(dailyData);
            setWaterIntake();
        });

        loadStepData();

        ImageView ivAddExercise = view.findViewById(R.id.ivAddExercise);
        ivAddExercise.setOnClickListener(v -> goToExerciseActivity());

        // Inflate the layout for this fragment
        return view;
    }

    private void setDataToView() {
        setUserData();
        setDailyDataToView();
        setWaterIntake();
    }

    private void initStepper() {
        tvStepCount = view.findViewById(R.id.tvStepCount);
        sensorManager = (SensorManager) Objects.requireNonNull(this.getActivity()).getSystemService(SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        pbSteps = view.findViewById(R.id.pbSteps);
    }

    private void resetSteps() {
        tvStepCount.setOnClickListener(v -> Toast.makeText(HomeFragment.this.getContext(), "Long press to reset steps", Toast.LENGTH_SHORT).show());
        tvStepCount.setOnLongClickListener(v -> {
            previousTotalSteps = totalSteps;
            tvStepCount.setText(R.string.step_count_0);
            pbSteps.setProgress(0);
            saveData();
            return true;
        });
    }

    private void setUserData() {
        if (goalData != null) {
            tvStepGoal.setText(MessageFormat.format("Goal: {0} steps", Integer.parseInt(goalData.getStepGoal())));
            tvGoalCalories.setText(MessageFormat.format("Goal: {0} kcal", Integer.parseInt(goalData.getCalorieGoal())));
            tvTotalCalories.setText(R.string.total_calories_0);

            pbTotalCalories.setMax(Integer.parseInt(goalData.getCalorieGoal().replaceAll("[^0-9]", "")));
            pbTotalCalories.setProgress(0);

            pbSteps.setMax(Integer.parseInt(goalData.getStepGoal().replaceAll("[^0-9]", "")));
            pbSteps.setProgress(currentSteps);

            waterGoal = Integer.parseInt(goalData.getWaterIntakeGoal().replaceAll("[^0-9]", ""));
        }
        if (userDetails != null) {
            weight = Double.parseDouble(userDetails.getWeight());
            weightUnit = userDetails.getWeightUnit();
            height = Integer.parseInt(userDetails.getHeight());
            heightUnit = userDetails.getHeightUnit();
            bmi = calculateBMI(height, weight);
            setWeightChart();
        }
    }

    private void init() {
        tvStepGoal = view.findViewById(R.id.tvStepGoal);
        tvGoalCalories = view.findViewById(R.id.tvGoalCalories);
        tvTotalCalories = view.findViewById(R.id.tvTotalCalories);
        tvTotalFoodCalories = view.findViewById(R.id.tvTotalFoodCalories);
        tvTotalExerciseCalories = view.findViewById(R.id.tvTotalExerciseCalories);
        tvCaloriesConsumedExercise = view.findViewById(R.id.tvCaloriesConsumedExercise);
        tvTimeElapsedExercise = view.findViewById(R.id.tvTimeElapsedExercise);
        tvBreakfastCalories = view.findViewById(R.id.tvBreakfastCalories);
        tvLunchCalories = view.findViewById(R.id.tvLunchCalories);
        tvDinnerCalories = view.findViewById(R.id.tvDinnerCalories);
        tvSnacksCalories = view.findViewById(R.id.tvSnacksCalories);

        addWaterIntake = view.findViewById(R.id.ivAddWater);
        pbTotalCalories = view.findViewById(R.id.pbTotalCalories);
        initStepper();

        goalData = GoalDataHolder.getInstance().getData() != null ? GoalDataHolder.getInstance().getData() : new GoalData();
        dailyData = DailyDataHolder.getInstance().getData() != null ? DailyDataHolder.getInstance().getData() : new DailyData();
        userDetails = UserDetailsHolder.getInstance().getData() != null ? UserDetailsHolder.getInstance().getData() : new UserDetails();
    }

    @Override
    public void onResume() {
        super.onResume();
        goalData = GoalDataHolder.getInstance().getData();
        setUserData();

        dailyData = DailyDataHolder.getInstance().getData();
        if (dailyData != null) {
            setDailyDataToView();
        } else {
            dailyData = new DailyData();
            DailyDataHolder.getInstance().setData(dailyData);
        }
        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void goToExerciseActivity() {
        Intent exerciseActivity = new Intent(this.getActivity(), ExerciseActivity.class);
        startActivity(exerciseActivity);
    }

    private void setWeightChart() {
        if (!isAdded()) {
            return;
        }
        PieChart weightChart = view.findViewById(R.id.weightChart);
        weightCategory = view.findViewById(R.id.tvWeightCategory);
        String[] weightCategories = getResources().getStringArray(R.array.weight_categories_array);

        TF = ResourcesCompat.getFont(view.getContext(), R.font.lora_font);

        moveoffScreen(weightChart);
        weightChart.setUsePercentValues(false);
        weightChart.getDescription().setEnabled(false);
        weightChart.setDrawHoleEnabled(true);
        weightChart.setCenterText(weight + weightUnit);
        weightChart.setCenterTextTypeface(TF);
        weightChart.setCenterTextSize(23f);
        weightChart.setCenterTextColor(ResourcesCompat.getColor(this.getResources(), R.color.gray, Objects.requireNonNull(this.getContext()).getTheme()));

        weightChart.setMaxAngle(180);
        weightChart.setRotationAngle(180);
        weightChart.setCenterTextOffset(0, -5);

        weightChart.setEntryLabelColor(ResourcesCompat.getColor(this.getResources(), R.color.gray, this.getContext().getTheme()));
        weightChart.setEntryLabelTextSize(11f);
        weightChart.setEntryLabelTypeface(TF);

        setChartData(weightCategories.length, weightChart, weightCategories);
        weightCategory.setTypeface(TF);
        weightCategory.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        weightChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                switch ((int) h.getX()) {
                    case 0:
                        weightCategory.setText(R.string.underweight_categ_message);
                        break;
                    case 1:
                        weightCategory.setText(R.string.healthy_categ_message);
                        break;
                    case 2:
                        weightCategory.setText(R.string.overweight_categ_message);
                        break;
                    case 3:
                        weightCategory.setText(R.string.obese_categ_message);
                        break;
                }
            }
            @Override
            public void onNothingSelected() {
                selectBMICategory(weightChart);
            }
        });

        selectBMICategory(weightChart);
    }

    private void selectBMICategory(PieChart chart) {
        String formattedBMI = String.format(Locale.ENGLISH, "%.1f", bmi);
        if (bmi < 18.5) {
            chart.highlightValue(0.0F, 0);
            weightCategory.setText(MessageFormat.format("Your BMI is {0}. ", formattedBMI));
        } else if (bmi < 24.9) {
            chart.highlightValue(1.0f, 0);
            weightCategory.setText(MessageFormat.format("Your BMI is {0}. ", formattedBMI));
        } else if (bmi < 29.9) {
            chart.highlightValue(2.0f, 0);
            weightCategory.setText(MessageFormat.format("Your BMI is {0}. ", formattedBMI));
        } else {
            chart.highlightValue(3.0f, 0);
            weightCategory.setText(MessageFormat.format("Your BMI is {0}. ", formattedBMI));
        }
        weightCategory.setTextSize(16.0f);
    }

//    private void retrieveUserProfilePicture() {
//        UserDAO userDAO = new UserDAOImpl();
//        userDAO.get().child("imageUrl").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    String imageUrl = dataSnapshot.getValue(String.class);
//
//                    if (imageUrl != null && !imageUrl.isEmpty() && isAdded()) {
//                        SharedPreferences sharedPreferences = Objects.requireNonNull(HomeFragment.this.getActivity()).getSharedPreferences(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), Context.MODE_PRIVATE);
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//                        editor.putString("imageUrl", imageUrl);
//                        editor.apply();
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle errors
//            }
//        });
//    }

    private void setChartData(int count, PieChart chart, String[] categories) {
        ArrayList<PieEntry> values = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            float val = 25;
            values.add(new PieEntry(val, categories[i]));
        }

        PieDataSet dataSet = new PieDataSet(values, "Underweight");
        dataSet.setSelectionShift(5f);
        dataSet.setValueLineColor(Color.BLACK);
        dataSet.setDrawIcons(true);
        dataSet.setSliceSpace(3f);
        dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        dataSet.setFormSize(30f);
        dataSet.setValueTypeface(TF);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setDrawValues(false);
        data.setValueTextSize(15f);
        data.setValueTextColor(Color.BLACK);

        chart.setData(data);
        chart.getLegend().setTypeface(TF);
        chart.invalidate();
    }

    private void moveoffScreen(PieChart chart) {
        //Display display = Objects.requireNonNull(this.getActivity()).getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        Objects.requireNonNull(this.getActivity()).getWindowManager().getDefaultDisplay().getMetrics(metrics);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) chart.getLayoutParams();
        params.setMargins(0, 0, 0, -200);
        chart.setLayoutParams(params);
    }

    private void setWaterIntake() {
        LinearLayout imageGroup = view.findViewById(R.id.waterIntakeGroup);
        TextView totalWaterIntake = view.findViewById(R.id.tvWaterIntakeGoal);

        totalWaterIntake.setText(MessageFormat.format("Total: {0} ml / {1} ml", waterIntake, Integer.parseInt(goalData.getWaterIntakeGoal())));

        for (int i = 0; i < imageGroup.getChildCount(); i++) {
            View childView = imageGroup.getChildAt(i);
            if (childView instanceof ImageView) {
                ImageView imageView = (ImageView) childView;
                if (i < waterIntake / 400) {
                    imageView.setImageResource(R.drawable.water_glass_icon);
                } else {
                    imageView.setImageResource(R.drawable.water_glass_icon_gray);
                }
            }
        }
        Log.d("INFO", "Intake:" + ((double) waterIntake / 400.0));
        if (((double) waterIntake / 400.0) > waterIntake / 400 && waterIntake / 400 < 5) {
            ImageView imageView = (ImageView) imageGroup.getChildAt(waterIntake / 400);
            imageView.setImageResource(R.drawable.water_glass_icon_half);
        }
    }

    private void setDailyDataToView() {
        int caloriesConsumed = dailyData.getCaloriesConsumed();
        int caloriesBurned = dailyData.getWorkoutCalories();
        int minutes = dailyData.getWorkoutTime();

        int breakfastCalories = 0;
        List<Recipe> breakfast = dailyData.getBreakfast();
        if (breakfast != null)
            for (Recipe recipe : breakfast) {
                breakfastCalories += recipe.getCalories();
            }

        int lunchCalories = 0;
        List<Recipe> lunch = dailyData.getLunch();
        if (lunch != null)
            for (Recipe recipe : lunch) {
                lunchCalories += recipe.getCalories();
            }

        int dinnerCalories = 0;
        List<Recipe> dinner = dailyData.getDinner();
        if (dinner != null)
            for (Recipe recipe : dinner) {
                dinnerCalories += recipe.getCalories();
            }

        int snackCalories = 0;
        List<Recipe> snacks = dailyData.getSnacks();
        if (snacks != null)
            for (Recipe recipe : snacks) {
                snackCalories += recipe.getCalories();
            }
        if (caloriesConsumed == 0) {
            caloriesConsumed = breakfastCalories + lunchCalories + dinnerCalories + snackCalories;
        }
        int totalCalories = caloriesConsumed - caloriesBurned;

        tvTotalCalories.setText(String.valueOf(totalCalories));
        tvCaloriesConsumedExercise.setText(MessageFormat.format("{0} kcal", caloriesBurned));
        tvTimeElapsedExercise.setText(MessageFormat.format("{0} minutes", minutes));
        tvTotalFoodCalories.setText(MessageFormat.format("Food: {0} kcal", caloriesConsumed));
        tvTotalExerciseCalories.setText(MessageFormat.format("Exercise: {0} kcal", caloriesBurned));
        tvBreakfastCalories.setText(MessageFormat.format("Breakfast: {0} kcal", breakfastCalories));
        tvLunchCalories.setText(MessageFormat.format("Lunch: {0} kcal", lunchCalories));
        tvDinnerCalories.setText(MessageFormat.format("Dinner: {0} kcal", dinnerCalories));
        tvSnacksCalories.setText(MessageFormat.format("Snacks: {0} kcal", snackCalories));
        pbTotalCalories.setProgress(totalCalories);
        waterIntake = dailyData.getWaterDrank();
    }

    @Override
    public void onDestroyView() {
       // saveDailyData();
        super.onDestroyView();
    }

    private void saveDailyData() {
        DailyDataDAO dailyDataDAO = new DailyDataDAOImpl();
        dailyData = DailyDataHolder.getInstance().getData();
        dailyDataDAO.update(dailyData);
    }


    private double calculateBMI(int height, double weight) {

        if (heightUnit.equals("cm") && weightUnit.equals("kg")) {
            double heightMeters = height / 100.0;
            return weight / (heightMeters * heightMeters);
        }
        if (heightUnit.equals("in") && weightUnit.equals("lbs")) {
            double heightMeters = height * 0.0254;
            return (weight / (heightMeters * heightMeters)) * 703;
        }
        return 0;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            totalSteps = (int) event.values[0];
            currentSteps = totalSteps - previousTotalSteps;

            tvStepCount.setText(MessageFormat.format("{0} steps", currentSteps));
            pbSteps.setProgress(currentSteps);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private void saveData() {
        SharedPreferences sharedPreferences = Objects.requireNonNull(this.getContext()).getSharedPreferences(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("steps", String.valueOf(previousTotalSteps));
        editor.apply();
    }

    private void loadStepData() {
        SharedPreferences sharedPreferences = Objects.requireNonNull(this.getContext()).getSharedPreferences(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), Context.MODE_PRIVATE);
        String savedSteps = sharedPreferences.getString("steps", "0");
        previousTotalSteps = Integer.parseInt(savedSteps);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}