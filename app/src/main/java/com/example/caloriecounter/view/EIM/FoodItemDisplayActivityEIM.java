package com.example.caloriecounter.view.EIM;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.caloriecounter.R;

//Clasă obiecte cu 5 date membre de tipuri diferite:
// inițializare cel putin 5 obiecte,
// afișarea tuturor obiectelor initializate in interfata aplicatiei

/**
 * Activity class for displaying a list of food items.
 * This class initializes a RecyclerView to display food items.
 *
 * @author cc458
 */

public class FoodItemDisplayActivityEIM extends AppCompatActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_item_display_eim);
        context = this.getApplicationContext();

        Button btnFilter = findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(v -> {
            Intent intent = new Intent(context, FilterResultsActivityEIM.class);
            startActivity(intent);
        });

        Button btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(context, WriteFoodToFileActivityEIM.class);
            startActivity(intent);
        });

        Button tvShowAll = findViewById(R.id.btnShowAll);
        tvShowAll.setOnClickListener(v -> {
            Intent intent = new Intent(context, ShowFoodsActivityEIM.class);
            intent.putExtra("SHOW", "ALL");
            startActivity(intent);
        });

    }



//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        // citeste filtrele din fisier intern
//        String[] textFromFile = null;
//        try {
//            textFromFile = readDataFromFile();
//        } catch (IOException e) {
//            Toast.makeText(context, "Citirea fisierului nu a putut fi realizata", Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }
//        if (textFromFile != null) {
//            isVegetarian = Boolean.parseBoolean(textFromFile[0].trim());
//            minProtein = textFromFile[1].trim();
//            maxProtein = textFromFile[2].trim();
//        }
//
//    }


}