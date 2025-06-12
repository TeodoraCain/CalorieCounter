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

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.caloriecounter.AddExerciseActivity;
import com.example.caloriecounter.AddWeightActivity;
import com.example.caloriecounter.R;
import com.example.caloriecounter.StepLogActivity;
import com.example.caloriecounter.WaterLogActivity;
import com.example.caloriecounter.WeightLogActivity;
import com.example.caloriecounter.models.dao.DailyData;
import com.example.caloriecounter.models.dao.DailyDataDAO;
import com.example.caloriecounter.models.dao.DailyDataDAOImpl;
import com.example.caloriecounter.models.dao.GoalData;
import com.example.caloriecounter.models.dao.Recipe;
import com.example.caloriecounter.models.dao.UserDetails;
import com.example.caloriecounter.models.dataHolders.DailyDataHolder;
import com.example.caloriecounter.models.dataHolders.GoalDataHolder;
import com.example.caloriecounter.models.dataHolders.UserDetailsHolder;
import com.example.caloriecounter.view.dialog.SelectMealTypeDialog;
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

    private final String TAG = "HomeFragment";
    private Context context;
    private Context attributionContext;

    //views
    private View view;
    private View weightHistory, waterHistory, stepHistory;
    private LinearLayout imageGroup;
    private TextView weightCategory;
    private TextView tvGoalCalories, tvStepGoal, tvTotalCalories, tvTotalFoodCalories, tvTotalExerciseCalories, tvCaloriesConsumedExercise, tvTimeElapsedExercise, tvBreakfastCalories, tvLunchCalories, tvDinnerCalories, tvSnacksCalories;
    private TextView tvStepCount;
    private TextView tvTotalWaterIntake;
    private ImageView ivAddFoodCalories, ivAddWaterIntake, ivAddWeight, ivAddExercise;
    private ProgressBar pbTotalCalories;
    private ProgressBar pbSteps;

    // attributes
    private Typeface typeface;

    private int waterIntake;
    private int waterGoal;
    private int totalSteps = 0;
    private int previousTotalSteps = 0;
    private double weight;
    private double bmi;
    private String firebaseUID;
    private String weightUnit;
    private String heightUnit;

    private UserDetails userDetails;
    private DailyData dailyData;
    private GoalData goalData;

    //step counter
    private SensorManager sensorManager = null;
    private Sensor stepSensor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        setUpViews();

        initUserData();
        loadStepDataFromSharedPrefs();
        setDataToUI();
        initStepper();
        setWaterIntake();
        resetSteps();
        setUpListeners();
        return view;
    }

    /********************************* INIT DATA ***************************************************/
    private void setUpViews() {
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
        tvStepCount = view.findViewById(R.id.tvStepCount);
        tvTotalWaterIntake = view.findViewById(R.id.tvWaterIntakeGoal);

        weightHistory = view.findViewById(R.id.weightHistory);
        stepHistory = view.findViewById(R.id.stepHistory);
        waterHistory = view.findViewById(R.id.waterHistory);

        ivAddFoodCalories = view.findViewById(R.id.ivAddFoodCalories);
        ivAddExercise = view.findViewById(R.id.ivAddExercise);
        ivAddWaterIntake = view.findViewById(R.id.ivAddWater);
        ivAddWeight = view.findViewById(R.id.ivAddWeight);
        imageGroup = view.findViewById(R.id.waterIntakeGroup);

        pbTotalCalories = view.findViewById(R.id.pbTotalCalories);
        pbSteps = view.findViewById(R.id.pbSteps);

        firebaseUID = FirebaseAuth.getInstance().getUid();
    }

    private void initAttributionContext() {
        attributionContext = Objects.requireNonNull(getActivity()).createAttributionContext("calorieCounter");
    }

    private void setUpListeners() {
        weightHistory.setOnClickListener(v -> goToActivity(WeightLogActivity.class));
        stepHistory.setOnClickListener(v -> goToActivity(StepLogActivity.class));
        waterHistory.setOnClickListener(v -> goToActivity(WaterLogActivity.class));
        ivAddWeight.setOnClickListener(v -> goToActivity(AddWeightActivity.class));
        ivAddFoodCalories.setOnClickListener(v ->onShowMealTypeDialog());
        //ivAddFoodCalories.setOnClickListener(v -> goToActivity(AddFoodActivity.class));
        ivAddExercise.setOnClickListener(v -> goToActivity(AddExerciseActivity.class));
        ivAddWaterIntake.setOnClickListener(v -> addWaterIntake());
    }

    private void onShowMealTypeDialog() {
        SelectMealTypeDialog selectMealTypeDialog = new SelectMealTypeDialog();
        selectMealTypeDialog.show(requireActivity().getSupportFragmentManager(), "Select Meal Type");
    }

    private void initUserData() {
        goalData = GoalDataHolder.getInstance().getData() != null ? GoalDataHolder.getInstance().getData() : new GoalData();
        dailyData = DailyDataHolder.getInstance().getData() != null ? DailyDataHolder.getInstance().getData() : new DailyData();
        userDetails = UserDetailsHolder.getInstance().getData() != null ? UserDetailsHolder.getInstance().getData() : new UserDetails();
    }

    private void setDataToUI() {
        setGoalDataToUI();
        setUserDataToUI();
        setDailyDataToUI();
        setWaterIntake();
    }

    private void setGoalDataToUI() {
        if (goalData == null) return;
        tvStepGoal.setText(MessageFormat.format("Goal: {0} steps", Integer.parseInt(goalData.getStepGoal())));
        int calGoal = 2000;
        try {
            calGoal = Integer.parseInt(goalData.getCalorieGoal());
        } catch (Exception e) {
            Log.d("error", "Could not parse Calorie goal data");
        }
        tvGoalCalories.setText(MessageFormat.format("Goal: {0} kcal", calGoal));
        tvTotalCalories.setText(R.string.total_calories_0);
        int maxCal = 2000;
        try {
            maxCal = Integer.parseInt(goalData.getCalorieGoal().replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            Log.d("error", "Could not parse Calorie goal data");
        }

        pbTotalCalories.setMax(maxCal);
        pbTotalCalories.setProgress(0);

        int maxSteps = 6000;
        waterGoal = 2000;
        try {
            maxSteps = Integer.parseInt(goalData.getStepGoal().replaceAll("[^0-9]", ""));
            waterGoal = Integer.parseInt(goalData.getWaterIntakeGoal().replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            Log.d("error", "Could not parse Calorie goal data");
        }
        pbSteps.setMax(maxSteps);

    }

    private void setUserDataToUI() {
        if (userDetails == null) return;
        try {
            weight = Double.parseDouble(userDetails.getWeight());
        } catch (Exception e) {
            Log.d(TAG, "User weight cannot be set");
        }

        weightUnit = userDetails.getWeightUnit();
        heightUnit = userDetails.getHeightUnit();

        int height;
        bmi = 0;
        try {
            height = Integer.parseInt(userDetails.getHeight());
            bmi = calculateBMI(height, weight);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        setWeightChart();
    }

    private void setDailyDataToUI() {
        int caloriesConsumed = dailyData.getCaloriesConsumed();
        int caloriesBurned = dailyData.getWorkoutCalories();
        int minutes = dailyData.getWorkoutTime();

        int breakfastCalories = 0;
        List<Recipe> breakfast = dailyData.getBreakfast();
        if (breakfast != null) for (Recipe recipe : breakfast) {
            breakfastCalories += recipe.getCalories();
        }

        int lunchCalories = 0;
        List<Recipe> lunch = dailyData.getLunch();
        if (lunch != null) for (Recipe recipe : lunch) {
            lunchCalories += recipe.getCalories();
        }

        int dinnerCalories = 0;
        List<Recipe> dinner = dailyData.getDinner();
        if (dinner != null) for (Recipe recipe : dinner) {
            dinnerCalories += recipe.getCalories();
        }

        int snackCalories = 0;
        List<Recipe> snacks = dailyData.getSnacks();
        if (snacks != null) for (Recipe recipe : snacks) {
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

    private void setWaterIntake() {
        tvTotalWaterIntake.setText(MessageFormat.format("Total: {0} ml / {1} ml", waterIntake, Integer.parseInt(goalData.getWaterIntakeGoal())));

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
        int cupCount = waterIntake / 400;
        if (((double) waterIntake / 400.0) > cupCount && cupCount < 5) {
            ImageView imageView = (ImageView) imageGroup.getChildAt(waterIntake / 400);
            imageView.setImageResource(R.drawable.water_glass_icon_half);
        }
    }

    /**************************************** STEP COUNTER *****************************************/
    private void initStepper() {
        sensorManager = (SensorManager) attributionContext.getSystemService(SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }

    private void resetSteps() {
        tvStepCount.setOnClickListener(v -> Toast.makeText(context, "Long press to reset steps", Toast.LENGTH_SHORT).show());
        tvStepCount.setOnLongClickListener(v -> {
            resetStepCount();
            return true;
        });
    }

    private void resetStepCount() {
        previousTotalSteps = totalSteps;
        tvStepCount.setText(R.string.step_count_0);
        pbSteps.setProgress(0);
        saveStepsToSharedPrefs();
        dailyData.setSteps(0);
        saveDailyDataToDB();
    }

    private void saveStepsToSharedPrefs() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(firebaseUID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //save to shared preferences
        editor.putString("steps", String.valueOf(previousTotalSteps));
        editor.apply();
    }

    private void loadStepDataFromSharedPrefs() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(firebaseUID, Context.MODE_PRIVATE);
        String savedSteps = sharedPreferences.getString("steps", "0");
        previousTotalSteps = Integer.parseInt(savedSteps);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            totalSteps = (int) event.values[0];
            if (dailyData.getSteps() == 0) {
                resetStepCount();
            }
            Log.d(TAG, "steps: " + dailyData.getSteps());

            int currentSteps = totalSteps - previousTotalSteps;
            tvStepCount.setText(MessageFormat.format("{0} steps", currentSteps));
            pbSteps.setProgress(currentSteps);

            dailyData.setSteps(currentSteps + 1);
            saveDailyDataToDB();
            saveStepsToSharedPrefs();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /************************************* ONCLICK ACTIONS *****************************************/
    private void addWaterIntake() {
        //water intake management
        int waterUnits = 200;
        waterIntake += waterUnits;
        if (waterIntake >= waterGoal) {
            Toast.makeText(context, "Awesome! You reached your water goal for today!", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(context, MessageFormat.format("Good job! {0}ml added", waterUnits), Toast.LENGTH_SHORT).show();

        dailyData.setWaterDrank(waterIntake);
        saveDailyDataToDB();
        setWaterIntake();
    }

    private void goToActivity(Class<?> activity) {
        Intent exerciseActivity = new Intent(context, activity);
        startActivity(exerciseActivity);
    }

    /************************************* DB ACCESS ***********************************************/
    private void saveDailyDataToDB() {
        DailyDataDAO dailyDataDAO = new DailyDataDAOImpl();
        dailyDataDAO.update(dailyData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DailyDataHolder.getInstance().setData(dailyData);
            }
        });
    }

    /************************************** WEIGHT/BMI CHART ***************************************/
    private void setWeightChart() {
        if (!isAdded()) {
            return;
        }
        PieChart weightChart = view.findViewById(R.id.weightChart);
        weightCategory = view.findViewById(R.id.tvWeightCategory);
        String[] weightCategories = getResources().getStringArray(R.array.weight_categories_array);

        typeface = ResourcesCompat.getFont(view.getContext(), R.font.lora_font);

        moveOffScreen(weightChart);
        weightChart.setUsePercentValues(false);
        weightChart.getDescription().setEnabled(false);
        weightChart.setDrawHoleEnabled(true);
        weightChart.setCenterText(weight + weightUnit);
        weightChart.setCenterTextTypeface(typeface);
        weightChart.setCenterTextSize(23f);
        weightChart.setCenterTextColor(ResourcesCompat.getColor(this.getResources(), R.color.gray, Objects.requireNonNull(this.getContext()).getTheme()));

        weightChart.setMaxAngle(180);
        weightChart.setRotationAngle(180);
        weightChart.setCenterTextOffset(0, -5);

        weightChart.setEntryLabelColor(ResourcesCompat.getColor(this.getResources(), R.color.gray, this.getContext().getTheme()));
        weightChart.setEntryLabelTextSize(11f);
        weightChart.setEntryLabelTypeface(typeface);

        setChartData(weightCategories.length, weightChart, weightCategories);
        weightCategory.setTypeface(typeface);
        weightCategory.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        weightChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                switch ((int) h.getX()) {
                    case 0:
                        weightCategory.setText(R.string.underweight_category_message);
                        break;
                    case 1:
                        weightCategory.setText(R.string.healthy_category_message);
                        break;
                    case 2:
                        weightCategory.setText(R.string.overweight_category_message);
                        break;
                    case 3:
                        weightCategory.setText(R.string.obese_category_message);
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
        dataSet.setValueTypeface(typeface);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setDrawValues(false);
        data.setValueTextSize(15f);
        data.setValueTextColor(Color.BLACK);

        chart.setData(data);
        chart.getLegend().setTypeface(typeface);
        chart.invalidate();
    }

    private void moveOffScreen(PieChart chart) {
        //Display display = context.getDisplay().getRealMetrics(metrics);//Objects.requireNonNull(this.getActivity()).getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
//        Objects.requireNonNull(this.getActivity()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        context.getDisplay().getRealMetrics(metrics);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) chart.getLayoutParams();
        params.setMargins(0, 0, 0, -200);
        chart.setLayoutParams(params);
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

    /*********************************** LIFECYCLE OVERRIDES ***************************************/

    @Override
    public void onResume() {
        super.onResume();
        goalData = GoalDataHolder.getInstance().getData();
        setUserDataToUI();

        dailyData = DailyDataHolder.getInstance().getData();
        if (dailyData != null) {
            setDailyDataToUI();
        } else {
            dailyData = new DailyData();
            DailyDataHolder.getInstance().setData(dailyData);
        }
        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        //saveDailyDataToDB();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "Fragment attached..");
        this.context = context;
        initAttributionContext();
    }

}