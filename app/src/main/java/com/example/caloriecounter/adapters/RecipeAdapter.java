package com.example.caloriecounter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.caloriecounter.R;
import com.example.caloriecounter.models.dao.Food;
import com.example.caloriecounter.models.dao.Recipe;

import java.text.MessageFormat;
import java.util.List;

public class RecipeAdapter extends ArrayAdapter<Recipe> {

    private final Context context;
    private final List<Recipe> recipeList;

    public RecipeAdapter(Context context, List<Recipe> recipeList) {
        super(context, 0, recipeList);
        this.context = context;
        this.recipeList = recipeList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.cutom_list_item, parent, false);
        }

        Recipe recipe = recipeList.get(position);

        TextView tvWorkoutName = listItem.findViewById(R.id.tvItemName);
        TextView tvServingSize = listItem.findViewById(R.id.tvItemDetail1);
        TextView tvCalories = listItem.findViewById(R.id.tvItemDetail2);

        if (recipe != null) {
            double servingSize = 0;
            List<Food> ingredients = recipe.getIngredients();
            for (Food ingredient : ingredients) {
                servingSize += ingredient.getServing_size();
            }
            tvServingSize.setText(MessageFormat.format("{0} grams", servingSize));
            tvWorkoutName.setText(recipe.getName());
            tvCalories.setText(MessageFormat.format("{0} kcal", recipe.getCalories()));
        }

        return listItem;
    }
}