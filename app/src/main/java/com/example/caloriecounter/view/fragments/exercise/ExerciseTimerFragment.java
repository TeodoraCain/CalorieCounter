package com.example.caloriecounter.view.fragments.exercise;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.caloriecounter.R;

import java.text.MessageFormat;
import java.util.Objects;

public class ExerciseTimerFragment extends Fragment {

    private final String TAG = "ExerciseTimerFragment";
    private Context context;

    private boolean isStarted = false;
    private boolean isPaused = false;
    private long timeWhenStopped = 0;
    private String calories;

    private Button btnStart;
    private Button btnPause;
    private ImageView icAnchor;
    private Animation clockAnimation;
    private Chronometer chronometer;
    private TextView tvCalories;
    private Runnable runnable;
    //TODO: FIX HANDLER
    private Handler handler;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_exercise_timer, container, false);
        init(view);
        setOnClickListeners();
        return view;
    }

    /********************************* INIT VIEW ***************************************************/
    private void init(View view) {
        retrieveExerciseCalories();
        initViews(view);
        initHandler();
        initRunnable();
        loadClockAnimation();
    }

    private void retrieveExerciseCalories() {
        final String CALORIES = "CALORIES";
        calories = Objects.requireNonNull(this.getActivity()).getIntent().getStringExtra(CALORIES);
    }

    private void loadClockAnimation() {
        clockAnimation = AnimationUtils.loadAnimation(context, R.anim.clock_animation);
    }

    private void initViews(View view) {
        tvCalories = view.findViewById(R.id.tvCalories);
        btnStart = view.findViewById(R.id.btnStartRecord);
        btnPause = view.findViewById(R.id.btnPauseRecord);
        icAnchor = view.findViewById(R.id.ivAnchor);
        chronometer = view.findViewById(R.id.timer);
    }

    private void initHandler() {
        handler = new Handler();
    }

    private void initRunnable() {
        runnable = new Runnable() {
            @Override
            public void run() {
                long elapsedSeconds = (SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000;
                int caloriesBurned = (int) (calculateCaloriesPerSecond() * elapsedSeconds);

                tvCalories.setText(MessageFormat.format("{0} kcal", caloriesBurned));
                handler.postDelayed(this, 1000);
            }
        };
    }

    /********************************* SET UP TIMER ************************************************/
    private void setOnClickListeners() {
        btnStart.setOnClickListener(v -> startStopTimer());
        btnPause.setOnClickListener(v -> pauseResumeTimer());
    }

    private void pauseResumeTimer() {
        if (isPaused) {
            resumeTimer();
        } else {
            pauseTimer();
        }
        isPaused = !isPaused;
    }

    private void startStopTimer() {
        if (!isStarted) {
            startTimer();
            isStarted = true;
            startCaloriesUpdate();
        } else {
            stopTimer();
            isStarted = false;
            stopCaloriesUpdate();
        }
    }

    /********************************* TIMER *******************************************************/
    private void startTimer() {
        Log.d(TAG, "Timer started...");
        icAnchor.startAnimation(clockAnimation);
        btnStart.setText(R.string.end_workout);
        btnPause.setVisibility(View.VISIBLE);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
    }

    private void stopTimer() {
        Log.d(TAG, "Timer stopped...");
        icAnchor.clearAnimation();
        btnStart.setText(R.string.start_workout);
        btnPause.setVisibility(View.GONE);
        chronometer.stop();
        stopCaloriesUpdate();
        isStarted = false;
    }

    private void pauseTimer() {
        Log.d(TAG, "Timer paused...");
        icAnchor.clearAnimation();
        btnPause.setText(R.string.resume_workout);
        timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
        chronometer.stop();
        stopCaloriesUpdate();
    }

    private void resumeTimer() {
        Log.d(TAG, "Timer resumed...");
        icAnchor.startAnimation(clockAnimation);
        btnPause.setText(R.string.pause);
        chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
        chronometer.start();
        startCaloriesUpdate();
    }

    /********************************* UPDATE CALORIES *********************************************/
    private void startCaloriesUpdate() {
        handler.post(runnable);
    }

    private void stopCaloriesUpdate() {
        handler.removeCallbacks(runnable);
    }

    private double calculateCaloriesPerSecond() {
        return Integer.parseInt(calories) / 3600.0F;
    }

    /*********************************** LIFECYCLE OVERRIDES ***************************************/
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "Fragment attached..");
        this.context = context;
    }
}