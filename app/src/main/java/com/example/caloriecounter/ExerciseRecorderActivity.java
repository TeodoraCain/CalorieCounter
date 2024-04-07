package com.example.caloriecounter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.caloriecounter.model.DAO.DailyData;
import com.example.caloriecounter.model.DAO.DailyDataDAO;
import com.example.caloriecounter.model.DAO.DailyDataDAOImpl;
import com.example.caloriecounter.model.DAO.Workout;
import com.example.caloriecounter.model.dataHolder.DailyDataHolder;
import com.example.caloriecounter.view.fragments.exercise.ExerciseRecordFragment;
import com.example.caloriecounter.view.fragments.exercise.ExerciseTimerFragment;
import com.google.android.material.tabs.TabLayout;
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

public class ExerciseRecorderActivity extends AppCompatActivity {

    private TabLayout exerciseTabs;
    private ViewPager2 viewPager;
    private String exercise;
    private boolean fromDiary;
    private String date;

    private Context mContext;
    private final String TAG = "ExerciseRecorderActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_recorder);

        setUpViews();
        setUpTabsAndViewPager();
    }

    private void setUpTabsAndViewPager() {
        Log.d(TAG, "Setting up the View Pager and Tabs..");
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, fromDiary);
        viewPager.setAdapter(viewPagerAdapter);

        if (fromDiary) {
            exerciseTabs.removeTabAt(0);
        }

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Objects.requireNonNull(exerciseTabs.getTabAt(position)).select();
            }
        });

        exerciseTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setUpViews() {
        setToolbar();
        mContext = ExerciseRecorderActivity.this;
        exerciseTabs = findViewById(R.id.tabExercise);
        viewPager = findViewById(R.id.viewPager);
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        final String EXERCISE = "EXERCISE";
        final String FROMDIARY = "FROMDIARY";
        final String DATE = "DATE";

        Intent intent = this.getIntent();
        fromDiary = intent.getBooleanExtra(FROMDIARY, false);
        exercise = intent.getStringExtra(EXERCISE);
        date = intent.getStringExtra(DATE);
        toolbar.setTitle(exercise);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setUpSaveToolbarButton();
    }

    private void setUpSaveToolbarButton() {
        ImageView saveImageView = findViewById(R.id.ivSave);
        saveImageView.setOnClickListener(v -> save());
    }

    private void save() {
        int currentItem = viewPager.getCurrentItem();
        Workout workout = null;
        if (!fromDiary) {
            if (currentItem == 0) {
                Log.d(TAG, "Saving chronometer data..");
                Chronometer timer = findViewById(R.id.timer);
                TextView tvCalories = findViewById(R.id.tvCalories);

                timer.stop();
                long elapsedSeconds = (SystemClock.elapsedRealtime() - timer.getBase()) / 1000;
                long minutes = elapsedSeconds / 60;

                workout = new Workout(exercise, (int) minutes, Integer.parseInt(tvCalories.getText().toString().replaceAll("[^0-9]", "")));

            } else if (currentItem == 1) {
                Log.d(TAG, "Saving record data..");
                EditText etMinutes = findViewById(R.id.etMinutes);
                TextView tvCalories = findViewById(R.id.tvCaloriesBurned);
                if (etMinutes.getText() != null || etMinutes.getText().toString().isEmpty()) {
                    workout = new Workout(exercise, Integer.parseInt(etMinutes.getText().toString()), Integer.parseInt(tvCalories.getText().toString().replaceAll("[^0-9]", "")));
                }
            }
            if (workout != null) {
                saveToDailyData(workout);
            }

        } else {
            Log.d(TAG, "Saving data from diary..");
            EditText etMinutes = findViewById(R.id.etMinutes);
            TextView tvCalories = findViewById(R.id.tvCaloriesBurned);
            if (etMinutes.getText() != null || etMinutes.getText().toString().isEmpty()) {
                workout = new Workout(exercise, Integer.parseInt(etMinutes.getText().toString()), Integer.parseInt(tvCalories.getText().toString().replaceAll("[^0-9]", "")));
            }
            if (workout != null && date != null) {
                saveToDailyData(workout, date);
            }
        }

        this.finish();
        Intent intent = new Intent(mContext, DashboardActivity.class);
        startActivity(intent);
    }

    private void saveToDailyData(Workout newWorkout, String date) {
        DailyDataDAO dailyDataDAO = new DailyDataDAOImpl();
        dailyDataDAO.get(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DailyData dailyData = snapshot.getValue(DailyData.class);
                List<Workout> workouts = new ArrayList<>();
                if (dailyData == null) {
                    dailyData = new DailyData();
                    workouts = dailyData.getWorkouts();
                }
                workouts.add(newWorkout);
                dailyData.setWorkouts(workouts);

                Calendar calendar = Calendar.getInstance();
                Date todaysDate = calendar.getTime();
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(todaysDate);

                if (date.equals(currentDate)) {
                    DailyDataHolder.getInstance().setData(dailyData);
                    DailyDataDAO dailyDataDAO = new DailyDataDAOImpl();
                    dailyDataDAO.update(dailyData);
                }
                saveDailyDataToDatabase(dailyData, date);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void saveToDailyData(Workout workout) {
        List<Workout> workouts = new ArrayList<>();
        DailyData dailyData = DailyDataHolder.getInstance().getData();
        if (dailyData != null) {
            workouts = dailyData.getWorkouts();
        } else {
            dailyData = new DailyData();
        }
        workouts.add(workout);
        dailyData.setWorkouts(workouts);
        DailyDataHolder.getInstance().setData(dailyData);
        DailyDataDAO dailyDataDAO = new DailyDataDAOImpl();
        dailyDataDAO.update(dailyData);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveDailyDataToDatabase(DailyData dailyData, String date) {
        DailyDataDAO dailyDataDAO = new DailyDataDAOImpl();
        dailyDataDAO.update(dailyData, date).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                Log.d("SaveData", "Data updated successfully");
                Intent intent = new Intent(mContext, DashboardActivity.class);
                intent.putExtra("NAVIGATE_TO_DIARY_FRAGMENT", true);
                startActivity(intent);
                //finish();
            } else {
                Log.e("SaveData", "Failed to update data", task.getException());
            }
        });
    }

    public static class ViewPagerAdapter extends FragmentStateAdapter {
        private final boolean fromDiary;

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, boolean fromDiary) {
            super(fragmentActivity);
            this.fromDiary = fromDiary;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (fromDiary) {
                if (position == 0) return new ExerciseRecordFragment();
            }

            if (position == 0) return new ExerciseTimerFragment();
            else return new ExerciseRecordFragment();
        }

        @Override
        public int getItemCount() {
            return fromDiary ? 1 : 2;
        }
    }
}