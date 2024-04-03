package com.example.caloriecounter;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caloriecounter.controller.SimpleRecyclerViewerAdapter;
import com.example.caloriecounter.model.DAO.FoodItem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShowFoodsActivityEIM extends AppCompatActivity {

    private RecyclerView rvFoodItemList;
    private List<FoodItem> foodItemList;

    private boolean isVegetarian;
    private String minProtein;
    private String maxProtein;

    private String show;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_foods_eim);

        context = getApplicationContext();
        show = getIntent().getStringExtra("SHOW");

        rvFoodItemList = findViewById(R.id.rvFoodItemList);
        rvFoodItemList.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvFoodItemList.setLayoutManager(layoutManager);

        readAll();
        RecyclerView.Adapter<SimpleRecyclerViewerAdapter.ViewHolder> adapter;
        adapter = new SimpleRecyclerViewerAdapter(foodItemList);

        if (show.equals("FILTER")) {
            String[] textFromFile = null;
            try {
                textFromFile = readDataFromFile();
            } catch (IOException e) {
                Toast.makeText(context, "Citirea fisierului nu a putut fi realizata", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            if (textFromFile != null) {
                isVegetarian = Boolean.parseBoolean(textFromFile[0].trim());
                minProtein = textFromFile[1].trim();
                maxProtein = textFromFile[2].trim();
                adapter = new SimpleRecyclerViewerAdapter(applyFilters(foodItemList));
            } else {
                adapter = new SimpleRecyclerViewerAdapter(foodItemList);
            }
        }
        rvFoodItemList.setAdapter(adapter);

    }

    private void readAll() {
        foodItemList = new ArrayList<>();
        try {
            readFromExternalFile();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error reading from external file", Toast.LENGTH_SHORT).show();

            foodItemList.add(new FoodItem(100.0, "Fruit Pie", 316, true, 3.0f));
            foodItemList.add(new FoodItem(100.0, "Yellow Tomatoes", 15, true, 0.98f));
            foodItemList.add(new FoodItem(100.0, "Chocolate Ice-cream", 216, true, 3.8f));
            foodItemList.add(new FoodItem(100.0, "McDONALD'S, Hamburger", 264, false, 12.92f));
            foodItemList.add(new FoodItem(100.0, "KFC, Popcorn Chicken", 351, false, 17.67f));
            foodItemList.add(new FoodItem(100.0, "Rye Crackers", 481, true, 9.2f));
            writeToExternalFile();
        }

    }

    private void readFromExternalFile() throws IOException {
        File file = new File(getExternalFilesDir(null), "food_data.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length == 5) {
                String name = parts[0];
                double servingSize = Double.parseDouble(parts[1]);
                int calories = (int) Float.parseFloat(parts[2]);
                float protein = Float.parseFloat(parts[3]);
                boolean vegetarian = false;
                 vegetarian = parts[4].equals("Vegetarian");
                foodItemList.add(new FoodItem(servingSize, name, calories, vegetarian, protein));
            }
        }
        br.close();
    }

    public void writeToExternalFile() {
        try {
            File file = new File(getExternalFilesDir(null), "food_data.txt");
            FileOutputStream fos = new FileOutputStream(file);
            for (FoodItem item : foodItemList) {
                String data = item.getName() + "," +
                        item.getServingSize() + "," +
                        item.getCaloriesPerServing() + "," +
                        item.getProtein() + "," +
                        (item.isVegetarian() ? "Vegetarian" : "Non-Vegetarian");
                fos.write(data.getBytes());
                fos.write("\n".getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Eroare la scrierea in fisierul extern", Toast.LENGTH_SHORT).show();
        }
    }

    public List<FoodItem> applyFilters(List<FoodItem> foodItemList) {
        List<FoodItem> filteredList = new ArrayList<>();
        Float minProteinNum = Float.parseFloat(minProtein);
        Float maxProteinNum = Float.parseFloat(maxProtein);

        if (minProteinNum == 0 && maxProteinNum == 0) {
            for (FoodItem item : foodItemList) {
                if (item.isVegetarian() == isVegetarian) {
                    filteredList.add(item);
                }
            }
        } else if (maxProteinNum > 0 && minProteinNum == 0) {

            for (FoodItem item : foodItemList) {
                if (item.isVegetarian() == isVegetarian && item.getProtein() <= Float.parseFloat(maxProtein)) {
                    filteredList.add(item);
                }
            }
        } else if (minProteinNum > 0 && maxProteinNum == 0) {
            for (FoodItem item : foodItemList) {
                if (item.isVegetarian() == isVegetarian && item.getProtein() >= Float.parseFloat(minProtein)) {
                    filteredList.add(item);
                }
            }
        } else {
            for (FoodItem item : foodItemList) {
                if (item.isVegetarian() == isVegetarian && item.getProtein() >= Float.parseFloat(minProtein) && item.getProtein() <= Float.parseFloat(maxProtein)) {
                    filteredList.add(item);
                }
            }
        }

        return filteredList;
    }

    private String[] readDataFromFile() throws IOException {
        File path = context.getFilesDir();
        String FILENAME = "filter_values.txt";
        File reader = new File(path, FILENAME);

        byte[] text = new byte[(int) reader.length()];

        FileInputStream stream = new FileInputStream(reader);
        stream.read(text);

        return new String(text).split(",");

    }
}