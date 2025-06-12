package com.example.caloriecounter.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caloriecounter.R;
import com.example.caloriecounter.models.dao.Recipe;

import java.text.MessageFormat;
import java.util.List;

public class RecipeRecyclerViewAdapter extends RecyclerView.Adapter<RecipeRecyclerViewAdapter.ViewHolder> {
    private final List<Recipe> recipes;
    private final OnItemClickListener clickListener;

    public RecipeRecyclerViewAdapter(List<Recipe> recipes, OnItemClickListener clickListener) {
        this.recipes = recipes;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_exercise_item, parent, false);
        return new ViewHolder(view);
    }

    public interface OnItemClickListener {
        void onItemClick(Recipe recipe);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        // Bind data to views in ViewHolder
        holder.bind(recipe);
        holder.itemView.setOnClickListener(view -> clickListener.onItemClick(recipe));
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
         final TextView tvRecipe;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRecipe = itemView.findViewById(R.id.tvExercise);
        }

        public void bind(Recipe recipe) {
            tvRecipe.setText(MessageFormat.format("{0} , {1}kcal", recipe.getName(), recipe.getCalories()));

        }
    }
}