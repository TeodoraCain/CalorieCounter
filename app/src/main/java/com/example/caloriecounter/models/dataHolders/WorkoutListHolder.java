package com.example.caloriecounter.models.dataHolders;

import com.example.caloriecounter.models.dao.Workout;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class WorkoutListHolder {
    private List<Workout> workoutList;

    public List<Workout> getData() {
        if (workoutList != null)
            return workoutList;
        else return new ArrayList<>();
    }

    public void setData(List<Workout> workoutList) {
        this.workoutList = workoutList;
    }

    private static final WorkoutListHolder holder = new WorkoutListHolder();

    public static WorkoutListHolder getInstance() {
        return holder;
    }
}
