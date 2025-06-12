package com.example.caloriecounter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.caloriecounter.R;
import com.example.caloriecounter.models.dao.Food;

import java.text.MessageFormat;
import java.util.List;

public class FoodAdapter extends ArrayAdapter<Food> {

    private final Context context;
    private final List<Food> foodList;

    public FoodAdapter(Context context, List<Food> foodList) {
        super(context, 0, foodList);
        this.context = context;
        this.foodList = foodList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.cutom_list_item, parent, false);
        }

        Food food = foodList.get(position);

        TextView tvFoodName = listItem.findViewById(R.id.tvItemName);
        TextView tvServingSize = listItem.findViewById(R.id.tvItemDetail1);
        TextView tvCalories = listItem.findViewById(R.id.tvItemDetail2);

        if (food != null) {
            tvFoodName.setText(food.getName());
            tvServingSize.setText(MessageFormat.format("{0} grams", food.getServing_size()));
            tvCalories.setText(MessageFormat.format("{0} kcal", food.getCalories()));
        }

        return listItem;
    }
}
