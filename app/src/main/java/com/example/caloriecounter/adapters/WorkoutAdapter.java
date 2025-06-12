package com.example.caloriecounter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.caloriecounter.R;
import com.example.caloriecounter.models.dao.Workout;

import java.text.MessageFormat;
import java.util.List;

public class WorkoutAdapter extends ArrayAdapter<Workout> {

    private final Context context;
    private final List<Workout> workoutList;

    public WorkoutAdapter(Context context, List<Workout> workoutList) {
        super(context, 0, workoutList);
        this.context = context;
        this.workoutList = workoutList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.cutom_list_item, parent, false);
        }

        Workout currentWorkout = workoutList.get(position);

        TextView workoutName = listItem.findViewById(R.id.tvItemName);
        TextView minutesElapsed = listItem.findViewById(R.id.tvItemDetail1);
        TextView caloriesConsumed = listItem.findViewById(R.id.tvItemDetail2);
        // Customize how you want to display each Workout object
        if (currentWorkout != null) {
            workoutName.setText(currentWorkout.getName()); // For example, display the name of the workout
            minutesElapsed.setText(MessageFormat.format("{0} minutes", currentWorkout.getMinutes()));
            caloriesConsumed.setText(MessageFormat.format("{0} kcal", currentWorkout.getCaloriesBurned()));
        }

        return listItem;
    }
}