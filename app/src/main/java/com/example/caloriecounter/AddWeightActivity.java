package com.example.caloriecounter;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

public class AddWeightActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_weight);
        setToolbar();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("Weight Log");
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//
//        ImageView saveImageView = findViewById(R.id.ivSave);
//
//        saveImageView.setOnClickListener(v -> save());
    }
}