package com.example.caloriecounter.view.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.caloriecounter.R;
import com.example.caloriecounter.models.dao.UserDetails;
import com.example.caloriecounter.models.dataHolders.UserDetailsHolder;

import java.util.ArrayList;
import java.util.Objects;

public class ChangeGoalsDialog extends AppCompatDialogFragment {

    private final TextView textView;
    GoalsDialogListener listener;
    private Spinner spinner;


    public ChangeGoalsDialog(TextView textView) {
        this.textView = textView;
    }

    @SuppressLint({"InflateParams", "NonConstantResourceId"})
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view;
        view = inflater.inflate(R.layout.cutom_dialog_change_goals, null);
        spinner = view.findViewById(R.id.spGoal);

        switch (textView.getId()) {
            case R.id.tvCalorieGoal:
                builder.setView(view).setNegativeButton("Cancel", (dialog, which) -> {

                }).setPositiveButton("Ok", (dialog, which) -> {
                    String text = spinner.getSelectedItem().toString() + " kcal";
                    listener.applyText(text, textView);
                });

                ArrayList<Integer> calorieList = new ArrayList<>();
                for (int i = 1000; i <= 6000; i += 100) {
                    calorieList.add(i);
                }
                ArrayAdapter<Integer> calorieAdapter = new ArrayAdapter<>(this.getContext(), R.layout.custom_spinner, calorieList);
                calorieAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinner.setAdapter(calorieAdapter);
                break;

            case R.id.tvWeightGoal:
                UserDetails userDetails = UserDetailsHolder.getInstance().getData();
                builder.setView(view).setNegativeButton("Cancel", (dialog, which) -> {

                }).setPositiveButton("Ok", (dialog, which) -> {
                    String text = spinner.getSelectedItem().toString() + " " + userDetails.getWeightUnit();
                    listener.applyText(text, textView);
                });

                ArrayList<Integer> weightList = new ArrayList<>();
                for (int i = 40; i <= 200; i++) {
                    weightList.add(i);
                }
                ArrayAdapter<Integer> weightAdapter = new ArrayAdapter<>(this.getContext(), R.layout.custom_spinner, weightList);
                weightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinner.setAdapter(weightAdapter);
                break;

            case R.id.tvExerciseGoal:
                builder.setView(view).setNegativeButton("Cancel", (dialog, which) -> {

                }).setPositiveButton("Ok", (dialog, which) -> {
                    String text = spinner.getSelectedItem().toString() + " min";
                    listener.applyText(text, textView);
                });

                ArrayList<Integer> exerciseList = new ArrayList<>();
                for (int i = 30; i <= 480; i += 15) {
                    exerciseList.add(i);
                }
                ArrayAdapter<Integer> exerciseAdapter = new ArrayAdapter<>(this.getContext(), R.layout.custom_spinner, exerciseList);
                exerciseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinner.setAdapter(exerciseAdapter);
                break;

            case R.id.tvWaterGoal:
                builder.setView(view).setNegativeButton("Cancel", (dialog, which) -> {

                }).setPositiveButton("Ok", (dialog, which) -> {
                    String text = spinner.getSelectedItem().toString() + " ml";
                    listener.applyText(text, textView);
                });

                ArrayList<Integer> waterList = new ArrayList<>();
                for (int i = 1000; i <= 3500; i += 500) {
                    waterList.add(i);
                }
                ArrayAdapter<Integer> waterAdapter = new ArrayAdapter<>(this.getContext(), R.layout.custom_spinner, waterList);
                waterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinner.setAdapter(waterAdapter);
                break;

            case R.id.tvStepGoal:
                builder.setView(view).setNegativeButton("Cancel", (dialog, which) -> {

                }).setPositiveButton("Ok", (dialog, which) -> {
                    String text = spinner.getSelectedItem().toString() + " steps";
                    listener.applyText(text, textView);
                });

                ArrayList<Integer> stepList = new ArrayList<>();
                for (int i = 1000; i <= 10000; i += 500) {
                    stepList.add(i);
                }
                ArrayAdapter<Integer> stepAdapter = new ArrayAdapter<>(this.getContext(), R.layout.custom_spinner, stepList);
                stepAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinner.setAdapter(stepAdapter);
                break;
        }

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (GoalsDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }

    public interface GoalsDialogListener {
        void applyText(String text, TextView textView);
    }
}
