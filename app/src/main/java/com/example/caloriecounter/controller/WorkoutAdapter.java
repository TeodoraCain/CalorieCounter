package com.example.caloriecounter.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.caloriecounter.R;
import com.example.caloriecounter.model.DAO.Workout;

import java.text.MessageFormat;
import java.util.List;

public class WorkoutAdapter extends ArrayAdapter<Workout> {

    private Context mContext;
    private List<Workout> mWorkoutList;

    public WorkoutAdapter(Context context, List<Workout> workoutList) {
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

        Workout currentWorkout = mWorkoutList.get(position);

        TextView workoutName = listItem.findViewById(R.id.tvWorkoutName);
        TextView minutesElapsed = listItem.findViewById(R.id.tvMinutesElapsed);
        TextView caloriesConsumed = listItem.findViewById(R.id.tvCaloriesConsumed);
        // Customize how you want to display each Workout object
        if (currentWorkout != null) {
            workoutName.setText(currentWorkout.getName()); // For example, display the name of the workout
            minutesElapsed.setText(MessageFormat.format("{0} minutes", currentWorkout.getMinutes()));
            caloriesConsumed.setText(MessageFormat.format("{0} kcal", currentWorkout.getCaloriesBurned()));
        }

        return listItem;
    }
}