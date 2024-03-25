package com.example.caloriecounter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

// O lista cu 6 obiecte (pot fi din clasa creata la tema anterioara),
// se vor citi in interfata 2 conditii
// si se vor afisa doar obiectele din lista care corespund celor doua conditii.

/**
 This class applies filters on a list of food items based on certain criteria.

 @author cc458
 */

public class FilterResultsActivity extends AppCompatActivity {

    private EditText etMinProtein;
    private EditText etMaxProtein;
    private CheckBox cbVegetarian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_results);

        etMaxProtein = findViewById(R.id.etMaxProtein);
        etMinProtein = findViewById(R.id.etMinProtein);
        cbVegetarian = findViewById(R.id.cbVegetarian);



        Button btnFilter = findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isVegetarian = cbVegetarian.isChecked();
                String minProtein = String.valueOf(etMinProtein.getText());
                String maxProtein = String.valueOf(etMaxProtein.getText());

                Intent intent = new Intent(FilterResultsActivity.this, FoodItemDisplayActivity.class);
                intent.putExtra("ISVEGETARIAN", isVegetarian);
                intent.putExtra("MINPROTEIN", minProtein);
                intent.putExtra("MAXPROTEIN", maxProtein);
                startActivity(intent);
            }
        });


    }


}