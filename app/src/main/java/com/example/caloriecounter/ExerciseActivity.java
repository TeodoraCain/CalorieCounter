package com.example.caloriecounter;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caloriecounter.controller.FirebaseExerciseRecyclerViewerAdapter;
import com.example.caloriecounter.controller.RecyclerViewInterface;
import com.example.caloriecounter.model.DAO.Exercise;
import com.example.caloriecounter.model.DAO.ExerciseDAO;
import com.example.caloriecounter.model.DAO.ExerciseDAOImpl;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.Objects;

public class ExerciseActivity extends AppCompatActivity implements RecyclerViewInterface {
    private RecyclerView rvExercises;
    private ExerciseDAO exerciseDAO;
    private FirebaseExerciseRecyclerViewerAdapter rvAdapter;
    private EditText etSearchExercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        init();

        etSearchExercise.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                searchByQuery(query);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        rvExercises.setLayoutManager(new LinearLayoutManager(this));
    }

    private void init() {
        setToolbar();

        etSearchExercise = findViewById(R.id.etSearchExercise);
        rvExercises = (RecyclerView) findViewById(R.id.rvExercises);
        exerciseDAO = new ExerciseDAOImpl();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void searchByQuery(String str) {
        if (str.length() > 0) {
            str = Character.toUpperCase(str.charAt(0)) + str.substring(1);
        }
        FirebaseRecyclerOptions<Exercise> options = new FirebaseRecyclerOptions.Builder<Exercise>()
                .setQuery(exerciseDAO.get().orderByChild("name").startAt(str).endAt(str + "~"), Exercise.class)
                .build();

        rvAdapter = new FirebaseExerciseRecyclerViewerAdapter(options, this);
        rvAdapter.startListening();
        rvExercises.setAdapter(rvAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rvAdapter != null) {
            rvAdapter.stopListening();
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position) {
        final String EXERCISE = "EXERCISE";
        final String CALORIES = "CALORIES";

        Intent intent = new Intent(ExerciseActivity.this, ExerciseRecorderActivity.class);

        intent.putExtra(EXERCISE, rvAdapter.getItem(position).getName());
        intent.putExtra(CALORIES, String.valueOf(rvAdapter.getItem(position).getCalories()));

        startActivity(intent);
    }
}