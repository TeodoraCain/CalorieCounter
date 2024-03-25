package com.example.caloriecounter.view.fragments.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.caloriecounter.R;
import com.example.caloriecounter.controller.RecipeAdapter;
import com.example.caloriecounter.controller.WorkoutAdapter;
import com.example.caloriecounter.model.DAO.DailyData;
import com.example.caloriecounter.model.DAO.Recipe;
import com.example.caloriecounter.model.DAO.Workout;
import com.example.caloriecounter.model.dataHolder.DailyDataHolder;

import java.util.List;


public class DiaryFragment extends Fragment {

    private TextView tvTotalCaloriesExercise;
    private TextView tvTotalBreakfastCalories;
    private TextView tvTotalLunchCalories;
    private TextView tvTotalDinnerCalories;
    private TextView tvTotalSnackCalories;

    private TextView tvDiaryDate;

    private ListView lvWorkoutHistory;
    private ListView lvBreakfast;
    private ListView lvLunch;
    private ListView lvDinner;
    private ListView lvSnacks;

    private DailyData dailyData;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_diary, container, false);
        init();
        loadData();

        return view;
    }

    private void loadData() {
        List<Recipe> breakfastList = dailyData.getBreakfast();
        List<Recipe> lunchList = dailyData.getLunch();
        List<Recipe> dinnerList = dailyData.getDinner();
        List<Recipe> snacksList = dailyData.getSnacks();

        setFoodList(breakfastList, tvTotalBreakfastCalories, lvBreakfast);
        setFoodList(lunchList, tvTotalLunchCalories, lvLunch);
        setFoodList(dinnerList, tvTotalDinnerCalories, lvDinner);
        setFoodList(snacksList, tvTotalSnackCalories, lvSnacks);

        setWorkouts();
    }

    private void setFoodList(List<Recipe> arrayList, TextView textView, ListView listView ) {
        ArrayAdapter adapter = new RecipeAdapter(DiaryFragment.this.getActivity(), arrayList);
        listView.setAdapter(adapter);

        int totalCalories = 0;
        for (Recipe recipe : arrayList) {
            totalCalories += recipe.getCalories();
        }

        textView.setText(String.valueOf(totalCalories));

        setListViewHeightBasedOnItems(listView);
    }

    private void setWorkouts() {
        List<Workout> workoutList = dailyData.getWorkouts();
        Log.d("INFO", workoutList.get(2).toString());

        ArrayAdapter adapter = new WorkoutAdapter(DiaryFragment.this.getActivity(), workoutList);
        lvWorkoutHistory.setAdapter(adapter);

        int totalWorkoutCalories = 0;
        for (Workout workout : workoutList) {
            totalWorkoutCalories += workout.getCaloriesBurned();
        }

        tvTotalCaloriesExercise.setText(String.valueOf(totalWorkoutCalories));

        setListViewHeightBasedOnItems(lvWorkoutHistory);
    }

    private void init() {
        tvTotalCaloriesExercise = view.findViewById(R.id.tvTotalExerciseCalories);
        tvTotalBreakfastCalories = view.findViewById(R.id.tvTotalBreakfastCalories);
        tvTotalLunchCalories = view.findViewById(R.id.tvTotalLunchCalories);
        tvTotalDinnerCalories = view.findViewById(R.id.tvTotalDinnerCalories);
        tvTotalSnackCalories = view.findViewById(R.id.tvTotalSnacksCalories);

        lvBreakfast = view.findViewById(R.id.lvBreakfast);
        lvLunch = view.findViewById(R.id.lvLunch);
        lvDinner = view.findViewById(R.id.lvDinner);
        lvSnacks = view.findViewById(R.id.lvSnacks);
        lvWorkoutHistory = view.findViewById(R.id.lvWorkoutHistory);

        dailyData = DailyDataHolder.getInstance().getData();

        tvDiaryDate = view.findViewById(R.id.tvDiaryDate);

    }

    private void setListViewHeightBasedOnItems(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int desiredHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            desiredHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = desiredHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
}