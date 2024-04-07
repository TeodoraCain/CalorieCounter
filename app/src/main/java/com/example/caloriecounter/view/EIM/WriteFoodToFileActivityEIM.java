package com.example.caloriecounter.view.EIM;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.caloriecounter.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class WriteFoodToFileActivityEIM extends AppCompatActivity {

    private EditText etName;
    private EditText etServingSize;
    private EditText etCalories;
    private EditText etProtein;
    private CheckBox cbVegetarian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_food_to_file_eim);

        etName = findViewById(R.id.etName);
        etServingSize = findViewById(R.id.etServingSize);
        etCalories = findViewById(R.id.etCalories);
        etProtein = findViewById(R.id.etProtein);
        cbVegetarian = findViewById(R.id.cbVegetarian);

        Button btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeToExternalFile();
                finish();
            }
        });
    }

    public void writeToExternalFile() {
        String data = etName.getText().toString() + ","
                + etServingSize.getText().toString() + ","
                + etCalories.getText().toString() + ","
                + etProtein.getText().toString() + ","
                + (cbVegetarian.isChecked() ? "Vegetarian" : "Non-Vegetarian");

        try {
            File file = new File(getExternalFilesDir(null), "food_data.txt");
            FileOutputStream fos = new FileOutputStream(file, true); // Append mode
            fos.write(data.getBytes());
            fos.write("\n".getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Eroare la scrierea in fisierul extern", Toast.LENGTH_SHORT).show();
        }
    }

}