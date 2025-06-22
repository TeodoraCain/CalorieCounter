package com.example.caloriecounter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caloriecounter.adapters.FirebaseExerciseRecyclerViewerAdapter;
import com.example.caloriecounter.models.dao.Exercise;
import com.example.caloriecounter.models.dao.ExerciseDAO;
import com.example.caloriecounter.models.dao.ExerciseDAOImpl;
import com.example.caloriecounter.models.dataModel.IntentKeys;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.Objects;

public class AddExerciseActivity extends AppCompatActivity implements FirebaseExerciseRecyclerViewerAdapter.RecyclerViewInterface {
    private RecyclerView rvExercises;
    private ExerciseDAO exerciseDAO;
    private FirebaseExerciseRecyclerViewerAdapter rvAdapter;
    private EditText etSearchExercise;

    private Context context;
    private final String TAG = "AddExerciseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);

        setToolbar();
        setUpViews();
        setUpTextWatcherForSearch();
        setUpRecyclerView();
    }
    /********************************* SET UP VIEWS ***********************************************/
    private void setUpViews() {
        context = this;
        etSearchExercise = findViewById(R.id.etSearchExercise);
        rvExercises = findViewById(R.id.rvExercises);
        exerciseDAO = new ExerciseDAOImpl();
    }
    private void setUpTextWatcherForSearch() {
        Log.d(TAG, "Setting up the text watcher for search..");
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
    }
    private void setUpRecyclerView() {
        rvExercises.setLayoutManager(new LinearLayoutManager(this));
        searchByQuery(""); // Initial search
    }

    private void searchByQuery(String query) {
        if (query.length() > 0) {
            query = Character.toUpperCase(query.charAt(0)) + query.substring(1);
        }
        try {
            FirebaseRecyclerOptions<Exercise> options = new FirebaseRecyclerOptions.Builder<Exercise>()
                    .setQuery(exerciseDAO.get().orderByChild("name").startAt(query).endAt(query + "~"), Exercise.class).build();

Log.d(TAG, options.getSnapshots().toString());
        rvAdapter = new FirebaseExerciseRecyclerViewerAdapter(options, this);
        rvAdapter.startListening();
        rvExercises.setAdapter(rvAdapter);
        } catch (Exception ex){
            Log.d(TAG, ex.getMessage());
        }
    }

    /********************************* RecyclerViewInterface OVERRIDE *****************************/
    @Override
    public void onItemClick(int position) {
        Log.d(TAG, "ItemClick: Clicked on " + rvAdapter.getItem(position).getName());

        Intent intent = new Intent(context, ExerciseRecorderActivity.class);
        intent.putExtra(IntentKeys.FROMDIARY, this.getIntent().getBooleanExtra(IntentKeys.FROMDIARY, false));
        intent.putExtra(IntentKeys.DATE, this.getIntent().getStringExtra(IntentKeys.DATE));
        intent.putExtra(IntentKeys.EXERCISE, rvAdapter.getItem(position).getName());
        intent.putExtra(IntentKeys.CALORIES, String.valueOf(rvAdapter.getItem(position).getCalories()));

        startActivity(intent);
        this.finish();
    }

    /********************************* SET UP TOOLBAR *********************************************/
    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /********************************* LIFECYCLE OVERRIDES ****************************************/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rvAdapter != null) {
            rvAdapter.stopListening();
        }
    }




}