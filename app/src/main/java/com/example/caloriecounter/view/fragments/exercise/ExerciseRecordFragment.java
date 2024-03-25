package com.example.caloriecounter.view.fragments.exercise;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.caloriecounter.R;

import java.util.Objects;

public class ExerciseRecordFragment extends Fragment {

    private TextView tvCaloriesBurned;

    public ExerciseRecordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_record, container, false);

        final String CALORIES = "CALORIES";
        String calories = Objects.requireNonNull(this.getActivity()).getIntent().getStringExtra(CALORIES);

        tvCaloriesBurned = view.findViewById(R.id.tvCaloriesBurned);

        EditText etTimePerformed = view.findViewById(R.id.etMinutes);
        etTimePerformed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String minutes = s.toString();
                try {
                    int caloriesBurned = (int) ((Integer.parseInt(calories) / 60.0f) * Integer.parseInt(minutes));
                    String caloriesBurnedText = String.valueOf(caloriesBurned);
                    if (tvCaloriesBurned != null) {
                        tvCaloriesBurned.setText(caloriesBurnedText);
                    }
                } catch (NumberFormatException e) {
                    // Handle the case where parsing fails, for example, if the input is not a valid integer
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }
}