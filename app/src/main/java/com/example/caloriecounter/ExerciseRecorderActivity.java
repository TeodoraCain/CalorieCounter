package com.example.caloriecounter;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExerciseRecorderActivity extends AppCompatActivity {

    private TabLayout exerciseTabs;
    private ViewPager2 viewPager;
    private String exercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_recorder);
        setToolbar();

        exerciseTabs = findViewById(R.id.tabExercise);
        viewPager = findViewById(R.id.viewPager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

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

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Objects.requireNonNull(exerciseTabs.getTabAt(position)).select();
            }
        });

    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        final String EXERCISE = "EXERCISE";

        exercise = this.getIntent().getStringExtra(EXERCISE);
        toolbar.setTitle(exercise);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ImageView saveImageView = findViewById(R.id.ivSave);

        saveImageView.setOnClickListener(v -> save());
    }

    private void save() {
        int currentItem = viewPager.getCurrentItem();
        Workout workout = null;
        if (currentItem == 0) {
            Chronometer timer = findViewById(R.id.timer);
            TextView tvCalories = findViewById(R.id.tvCalories);

            timer.stop();
            long elapsedSeconds = (SystemClock.elapsedRealtime() - timer.getBase()) / 1000;
            long minutes = elapsedSeconds / 60;

            workout = new Workout(exercise, (int) minutes, Integer.parseInt(tvCalories.getText().toString().replaceAll("[^0-9]", "")));

        } else if (currentItem == 1) {
            EditText etMinutes = findViewById(R.id.etMinutes);
            TextView tvCalories = findViewById(R.id.tvCaloriesBurned);
            if (etMinutes.getText() != null || etMinutes.getText().toString().isEmpty()) {
                workout = new Workout(exercise, Integer.parseInt(etMinutes.getText().toString()), Integer.parseInt(tvCalories.getText().toString().replaceAll("[^0-9]", "")));
            }
        }
        if (workout != null) {
            saveToDailyData(workout);
        }


        this.finish();
        Intent intent = new Intent(ExerciseRecorderActivity.this, DashboardActivity.class);
        startActivity(intent);
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

    public static class ViewPagerAdapter extends FragmentStateAdapter {

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) return new ExerciseTimerFragment();
            else return new ExerciseRecordFragment();
        }

        @Override
        public int getItemCount() {
            return 2;
        }

    }
}