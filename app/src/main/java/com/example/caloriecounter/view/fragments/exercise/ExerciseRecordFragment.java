package com.example.caloriecounter.view.fragments.exercise;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.caloriecounter.R;

import java.util.Objects;

public class ExerciseRecordFragment extends Fragment {

    private final String TAG = "ExerciseRecordFragment";

    private TextView tvCaloriesBurned;
    private EditText etTimePerformed;
    private String calories;

    public ExerciseRecordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_record, container, false);
        init(view);
        setTextChangeListener();
        return view;
    }

    /********************************* INIT VIEW ***************************************************/
    private void init(View view) {
        initExerciseCalories();
        initViews(view);
    }

    private void initExerciseCalories() {
        final String CALORIES = "CALORIES";
        calories = Objects.requireNonNull(this.getActivity()).getIntent().getStringExtra(CALORIES);
    }

    private void initViews(View view) {
        tvCaloriesBurned = view.findViewById(R.id.tvCaloriesBurned);
        etTimePerformed = view.findViewById(R.id.etMinutes);
    }

    /********************************* SET UP TEXT LISTENER ****************************************/
    private void setTextChangeListener() {
        etTimePerformed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changeCaloriesBurned(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void changeCaloriesBurned(CharSequence s) {
        String minutes = s.toString();
        try {
            int caloriesBurned = (int) ((Integer.parseInt(calories) / 60.0f) * Integer.parseInt(minutes));
            String caloriesBurnedText = String.valueOf(caloriesBurned);
            if (tvCaloriesBurned != null) {
                tvCaloriesBurned.setText(caloriesBurnedText);
                Log.d(TAG, "Calories changed: "+ caloriesBurnedText);
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }

    }

    /*********************************** LIFECYCLE OVERRIDES ***************************************/
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "Fragment attached..");
    }

}