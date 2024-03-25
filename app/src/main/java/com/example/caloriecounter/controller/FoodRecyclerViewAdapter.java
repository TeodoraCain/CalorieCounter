package com.example.caloriecounter.controller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caloriecounter.R;
import com.example.caloriecounter.model.DAO.Food;

import java.text.MessageFormat;
import java.util.List;

public class FoodRecyclerViewAdapter extends RecyclerView.Adapter<FoodRecyclerViewAdapter.ViewHolder> {
    private List<Food> foods;
    private OnItemClickListener clickListener;

    public FoodRecyclerViewAdapter(List<Food> foods, OnItemClickListener clickListener) {
        this.foods = foods;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_item, parent, false);
        return new ViewHolder(view);
    }

    public interface OnItemClickListener {
        void onItemClick(Food food);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Food food = foods.get(position);
        // Bind data to views in ViewHolder
        holder.bind(food);
        holder.itemView.setOnClickListener(view -> clickListener.onItemClick(food));
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView exercise;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            exercise = itemView.findViewById(R.id.tvExercise);
        }

        public void bind(Food food) {
            exercise.setText(MessageFormat.format("{0} , {1}kcal", food.getName(), food.getCalories()));

        }
    }
}