package com.example.caloriecounter.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.caloriecounter.R;
import com.example.caloriecounter.model.DAO.Recipe;

import java.text.MessageFormat;
import java.util.List;

public class RecipeAdapter extends ArrayAdapter<Recipe> {

    private Context mContext;
    private List<Recipe> mWorkoutList;

    public RecipeAdapter(Context context, List<Recipe> workoutList) {
        super(context, 0, workoutList);
        mContext = context;
        mWorkoutList = workoutList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.diary_workout_list_view, parent, false);
        }

        Recipe currentWorkout = mWorkoutList.get(position);

        TextView workoutName = listItem.findViewById(R.id.tvWorkoutName);
        TextView minutesElapsed = listItem.findViewById(R.id.tvMinutesElapsed);
        TextView caloriesConsumed = listItem.findViewById(R.id.tvCaloriesConsumed);
        // Customize how you want to display each Workout object
        if (currentWorkout != null) {
            workoutName.setText(currentWorkout.getName()); // For example, display the name of the workout
            minutesElapsed.setText(MessageFormat.format("{0} grams", currentWorkout.getServing_size()));
            caloriesConsumed.setText(MessageFormat.format("{0} kcal", currentWorkout.getCalories()));
        }

        return listItem;
    }
}