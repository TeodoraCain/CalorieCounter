package com.example.caloriecounter.view.fragments.exercise;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.caloriecounter.R;

import java.text.MessageFormat;
import java.util.Objects;

public class ExerciseTimerFragment extends Fragment {

    private View view;

    private Button btnStart;
    private ImageView icAnchor;
    private Animation clockAnimation;
    private boolean startRecord = false;
    private Chronometer timer;
    private String calories;
    private TextView tvCalories;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_exercise_timer, container, false);
        init();

        clockAnimation = AnimationUtils.loadAnimation(this.getContext(), R.anim.clock_animation);
        btnStart.setOnClickListener(v -> {
            if (!startRecord) {
                icAnchor.startAnimation(clockAnimation);
                btnStart.setText(R.string.end_workout);
                timer.setBase(SystemClock.elapsedRealtime());

                timer.start();
                startRecord = true;
                startCaloriesUpdate();
            } else {
                icAnchor.clearAnimation();
                btnStart.setText(R.string.start_workout);

                timer.stop();
                stopCaloriesUpdate();
                startRecord = false;
            }
        });

        return view;
    }

    private void startCaloriesUpdate() {
        caloriesHandler.post(caloriesRunnable);
    }

    private void stopCaloriesUpdate() {
        caloriesHandler.removeCallbacks(caloriesRunnable);
    }

    private final Handler caloriesHandler = new Handler();
    private final Runnable caloriesRunnable = new Runnable() {
        @Override
        public void run() {
            long elapsedSeconds = (SystemClock.elapsedRealtime() - timer.getBase()) / 1000;
            int caloriesBurned = (int) (calculateCaloriesPerSecond() * elapsedSeconds);

            tvCalories.setText(MessageFormat.format("{0} kcal", caloriesBurned));
            caloriesHandler.postDelayed(this, 1000);
        }
    };

    private double calculateCaloriesPerSecond() {
        return Integer.parseInt(calories) / 3600.0F;
    }

    private void init() {
        final String CALORIES = "CALORIES";

        calories = Objects.requireNonNull(this.getActivity()).getIntent().getStringExtra(CALORIES);

        tvCalories = view.findViewById(R.id.tvCalories);
        btnStart = view.findViewById(R.id.btnStartRecord);
        icAnchor = view.findViewById(R.id.ivAnchor);
        timer = view.findViewById(R.id.timer);
    }
}