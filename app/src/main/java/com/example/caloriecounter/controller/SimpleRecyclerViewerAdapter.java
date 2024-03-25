package com.example.caloriecounter.controller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caloriecounter.model.DAO.FoodItem;
import com.example.caloriecounter.R;

import java.util.ArrayList;
import java.util.List;

public class SimpleRecyclerViewerAdapter extends RecyclerView.Adapter<SimpleRecyclerViewerAdapter.ViewHolder> {
    private List<FoodItem> foodItemList;
    private List<FoodItem> filteredList;

    public SimpleRecyclerViewerAdapter(List<FoodItem> foodItemList) {
        this.foodItemList = foodItemList;
        this.filteredList = new ArrayList<>(foodItemList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
        return new SimpleRecyclerViewerAdapter.ViewHolder(view);
    }

    public void applyFilter(boolean isVegetarian, float maxProtein) {
        filteredList.clear();

        for (FoodItem item : foodItemList) {
            if (item.isVegetarian() == isVegetarian && item.getProtein() <= maxProtein) {
                filteredList.add(item);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodItem foodItem = foodItemList.get(position);

        holder.tvName.setText(foodItem.getName());
        holder.tvServingSize.setText("Serving Size: " + foodItem.getServingSize());
        holder.tvCalories.setText("Calories: " + foodItem.getCaloriesPerServing());
        holder.tvProtein.setText("Protein: " + foodItem.getProtein());
        holder.cbVegetarian.setChecked(foodItem.isVegetarian());

    }

    @Override
    public int getItemCount() {
        return foodItemList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvName, tvServingSize, tvCalories, tvProtein;
        CheckBox cbVegetarian;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvFoodItemName);
            tvCalories = itemView.findViewById(R.id.tvCaloriesPerServing);
            tvProtein = itemView.findViewById(R.id.tvProtein);
            tvServingSize = itemView.findViewById(R.id.tvServingSize);
            cbVegetarian = itemView.findViewById(R.id.cbVegetarian);



        }
    }

}
