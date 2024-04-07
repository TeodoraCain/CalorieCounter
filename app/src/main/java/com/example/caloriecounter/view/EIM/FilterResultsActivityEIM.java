package com.example.caloriecounter.view.EIM;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.caloriecounter.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;

// O lista cu 6 obiecte (pot fi din clasa creata la tema anterioara),
// se vor citi in interfata 2 conditii
// si se vor afisa doar obiectele din lista care corespund celor doua conditii.

/**
 * This class applies filters on a list of food items based on certain criteria.
 *
 * @author cc458
 */

public class FilterResultsActivityEIM extends AppCompatActivity {

    private EditText etMinProtein;
    private EditText etMaxProtein;
    private CheckBox cbVegetarian;

    private Context context;
    private final String FILENAME = "filter_values.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_results_eim);

        context = this.getApplicationContext();
        etMaxProtein = findViewById(R.id.etMaxProtein);
        etMinProtein = findViewById(R.id.etMinProtein);
        cbVegetarian = findViewById(R.id.cbVegetarian);


        Button btnFilter = findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(v -> {
            boolean isVegetarian = cbVegetarian.isChecked();
            String minProteinString = String.valueOf(etMinProtein.getText());
            String maxProteinString = String.valueOf(etMaxProtein.getText());

            float minProtein = minProteinString.isEmpty() ? 0 : Float.parseFloat(minProteinString);
            float maxProtein = maxProteinString.isEmpty() ? 0 : Float.parseFloat(maxProteinString);

            String textToWrite = MessageFormat.format("{0},{1},{2}", isVegetarian, minProtein, maxProtein);

            try {
                writeToFile(textToWrite);
            } catch (IOException e) {
                Toast.makeText(context, "Scrierea fisierului nu a putut fi realizata", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            startIntentFilter();
        });
    }

    private void writeToFile(String text) throws IOException {
        File path = context.getFilesDir();
        FileOutputStream writer = new FileOutputStream(new File(path, FILENAME));
        writer.write(text.getBytes());
        writer.close();
    }

    private void startIntentFilter() {
        Intent intent = new Intent(FilterResultsActivityEIM.this, ShowFoodsActivityEIM.class);
        intent.putExtra("SHOW", "FILTER");
        startActivity(intent);
    }


}