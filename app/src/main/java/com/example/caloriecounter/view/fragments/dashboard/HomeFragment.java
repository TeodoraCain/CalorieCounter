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
import com.example.caloriecounter.models.dataModel.DefaultValue;
import com.example.caloriecounter.utils.UserUtils;
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
    private int waterIntake;
    private int waterGoal;
    private double weight;
    private double bmi;
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

        initFragment();
        setUpFragment();
        return view;
    }

    //region Init Fragment
    private void initViews() {
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
    }

    private void initUserData() {
        goalData = GoalDataHolder.getInstance().getData() != null ? GoalDataHolder.getInstance().getData() : new GoalData();
        dailyData = DailyDataHolder.getInstance().getData() != null ? DailyDataHolder.getInstance().getData() : new DailyData();
        userDetails = UserDetailsHolder.getInstance().getData() != null ? UserDetailsHolder.getInstance().getData() : new UserDetails();
    }

    private void initAttributionContext() {
        attributionContext = Objects.requireNonNull(getActivity()).createAttributionContext("calorieCounter");
    }

    //endregion
    private void initFragment() {
        initViews();
        initUserData();
    }

    //region Set Up Fragment
    //region Set Up UI
    private void setGoalDataToUI() {
        int calGoal = DefaultValue.CALORIE_GOAL;
        int maxCal = DefaultValue.CALORIE_GOAL;
        int maxSteps = DefaultValue.STEP_GOAL;

        if (goalData == null) return;
        tvStepGoal.setText(MessageFormat.format("Goal: {0} steps", Integer.parseInt(goalData.getStepGoal())));

        try {
            calGoal = Integer.parseInt(goalData.getCalorieGoal());
        } catch (Exception e) {
            Log.d("error", "Could not parse Calorie goal data");
        }
        tvGoalCalories.setText(MessageFormat.format("Goal: {0} kcal", calGoal));
        tvTotalCalories.setText(R.string.total_calories_0);

        try {
            maxCal = Integer.parseInt(goalData.getCalorieGoal().replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            Log.d("error", "Could not parse Calorie goal data");
        }
        pbTotalCalories.setMax(maxCal);
        pbTotalCalories.setProgress(0);

        waterGoal = DefaultValue.WATER_GOAL;
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
        int caloriesBurned = dailyData.getCaloriesBurned();
        int minutes = dailyData.calculateWorkoutTime();

        int breakfastCalories = calculateMealCalories(dailyData.getBreakfast());
        int lunchCalories = calculateMealCalories(dailyData.getLunch());
        int dinnerCalories = calculateMealCalories(dailyData.getDinner());
        int snackCalories = calculateMealCalories(dailyData.getSnacks());

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

    private int calculateMealCalories(List<Recipe> meals) {
        int calculatedCalories = 0;
        if (meals != null)
            for (Recipe recipe : meals) {
                calculatedCalories += recipe.getCalories();
            }
        return calculatedCalories;
    }

    //endregion
    private void setUpUI() {
        setGoalDataToUI();
        setUserDataToUI();
        setDailyDataToUI();
    }

    //region Set Up Listeners
    private void goToActivity(Class<?> activity) {
        Intent exerciseActivity = new Intent(context, activity);
        startActivity(exerciseActivity);
    }

    private void onShowMealTypeDialog() {
        SelectMealTypeDialog selectMealTypeDialog = new SelectMealTypeDialog();
        selectMealTypeDialog.show(requireActivity().getSupportFragmentManager(), "Select Meal Type");
    }

    private void addWaterIntake() {
        //water intake management
        waterIntake += DefaultValue.WATER_UNIT;
        if (waterIntake >= waterGoal) {
            Toast.makeText(context, "Awesome! You reached your water goal for today!", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(context, MessageFormat.format("Good job! {0}ml added", DefaultValue.WATER_UNIT), Toast.LENGTH_SHORT).show();

        dailyData.setWaterDrank(waterIntake);
        saveDailyDataToDB();
        setUpWaterIntake();
    }

    //endregion
    private void setUpListeners() {
        weightHistory.setOnClickListener(v -> goToActivity(WeightLogActivity.class));
        stepHistory.setOnClickListener(v -> goToActivity(StepLogActivity.class));
        waterHistory.setOnClickListener(v -> goToActivity(WaterLogActivity.class));
        ivAddWeight.setOnClickListener(v -> goToActivity(AddWeightActivity.class));
        ivAddFoodCalories.setOnClickListener(v -> onShowMealTypeDialog());
        ivAddExercise.setOnClickListener(v -> goToActivity(AddExerciseActivity.class));
        ivAddWaterIntake.setOnClickListener(v -> addWaterIntake());
    }

    //region Step Counter
    private void initStepCounter() {
        sensorManager = (SensorManager) attributionContext.getSystemService(SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        SharedPreferences prefs = requireContext().getSharedPreferences("step_prefs", Context.MODE_PRIVATE);
        int todaySteps = prefs.getInt("today_steps_" + UserUtils.getFirebaseUID(), 0);
        tvStepCount.setText(MessageFormat.format("{0} steps", todaySteps));
        pbSteps.setProgress(todaySteps);

        dailyData.setSteps(todaySteps);
        DailyDataHolder.getInstance().setData(dailyData);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //endregion

    private void setUpWaterIntake() {
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

    //endregion
    private void setUpFragment() {
        setUpUI();
        setUpListeners();
        initStepCounter();
        setUpWaterIntake();
        updateStepCountDisplay();
    }

    //region DB Access
    private void saveDailyDataToDB() {
        DailyDataDAO dailyDataDAO = new DailyDataDAOImpl();
        dailyDataDAO.update(dailyData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DailyDataHolder.getInstance().setData(dailyData);
            }
        });
    }
    //endregion


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
        saveDailyDataToDB();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "Fragment attached..");
        this.context = context;
        initAttributionContext();
    }

    private void updateStepCountDisplay() {
        SharedPreferences prefs = requireContext().getSharedPreferences("step_prefs", Context.MODE_PRIVATE);
        int todaySteps = prefs.getInt("today_steps_" + UserUtils.getFirebaseUID(), 0);

        tvStepCount.setText(String.format(Locale.ENGLISH, "%d steps", todaySteps));
    }

    //region WEIGHT/BMI CHART
    private void setWeightChart() {
        if (!isAdded()) {
            return;
        }
        PieChart weightChart = view.findViewById(R.id.weightChart);
        weightCategory = view.findViewById(R.id.tvWeightCategory);
        String[] weightCategories = getResources().getStringArray(R.array.weight_categories_array);

        Typeface typeface = ResourcesCompat.getFont(view.getContext(), R.font.lora_font);

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

        setChartData(weightCategories.length, weightChart, weightCategories, typeface);
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

    private void setChartData(int count, PieChart chart, String[] categories, Typeface typeface) {
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
        DisplayMetrics metrics = new DisplayMetrics();
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
    //endregion

}