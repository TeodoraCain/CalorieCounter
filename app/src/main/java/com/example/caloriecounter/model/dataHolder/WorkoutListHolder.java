package com.example.caloriecounter.model.dataHolder;

import com.example.caloriecounter.model.DAO.Workout;

import java.util.ArrayList;
import java.util.List;

public class WorkoutListHolder {
    private List<Workout> workoutList;

    public List<Workout> getData() {
        if (workoutList != null)
            return workoutList;
        else return new ArrayList<Workout>();
    }

    public void setData(List<Workout> workoutList) {
        this.workoutList = workoutList;
    }

    private static final WorkoutListHolder holder = new WorkoutListHolder();

    public static WorkoutListHolder getInstance() {
        return holder;
    }
}
