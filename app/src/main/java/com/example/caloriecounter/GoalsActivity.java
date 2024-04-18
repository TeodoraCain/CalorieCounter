package com.example.caloriecounter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.caloriecounter.models.dao.GoalDAOImpl;
import com.example.caloriecounter.models.dao.GoalData;
import com.example.caloriecounter.models.dao.GoalDataDAO;
import com.example.caloriecounter.models.dao.UserDetails;
import com.example.caloriecounter.models.dataHolders.GoalDataHolder;
import com.example.caloriecounter.models.dataHolders.UserDetailsHolder;
import com.example.caloriecounter.view.dialog.ChangeGoalsDialog;
import com.example.caloriecounter.view.dialog.SuccessDialog;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GoalsActivity extends AppCompatActivity implements ChangeGoalsDialog.GoalsDialogListener {

    private final String TAG = "GoalsActivity";
    GoalData goalData;
    TextView tvCalorieGoal,
            tvExerciseGoal,
            tvWeightGoal,
            tvWaterGoal,
            tvStepGoal;
    private Context context;
    private boolean savedChanges = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);
        initContext();
        setToolbar();
        setUpViews();
        setOnClickListeners();

    }

    /********************************* INIT ACTIVITY **********************************************/
    private void initContext() {
        context = GoalsActivity.this;
    }

    void setUpViews() {
        tvCalorieGoal = findViewById(R.id.tvCalorieGoal);
        tvExerciseGoal = findViewById(R.id.tvExerciseGoal);
        tvWeightGoal = findViewById(R.id.tvWeightGoal);
        tvWaterGoal = findViewById(R.id.tvWaterGoal);
        tvStepGoal = findViewById(R.id.tvStepGoal);
    }

    void setGoalDataToUI() {
        goalData = GoalDataHolder.getInstance().getData();
        UserDetails userDetails = UserDetailsHolder.getInstance().getData();

        if (tvCalorieGoal != null && goalData != null) {
            tvCalorieGoal.setText(MessageFormat.format("{0} kcal", goalData.getCalorieGoal()));
        }
        if (tvExerciseGoal != null && goalData != null) {
            tvExerciseGoal.setText(MessageFormat.format("{0} min", goalData.getExerciseTimeGoal()));
        }
        if (tvWeightGoal != null && goalData != null && userDetails != null) {
            tvWeightGoal.setText(MessageFormat.format("{0} {1}", goalData.getWeightGoal(), userDetails.getWeightUnit()));
        }
        if (tvWaterGoal != null && goalData != null) {
            tvWaterGoal.setText(MessageFormat.format("{0} ml", goalData.getWaterIntakeGoal()));
        }
        if (tvStepGoal != null && goalData != null) {
            tvStepGoal.setText(MessageFormat.format("{0} steps", goalData.getStepGoal()));
        }
    }

    /********************************* SET UP LISTENERS *******************************************/
    @SuppressWarnings("unused")
    public void onSaveProfileData(View view) {
        GoalDataDAO goalDataDAO = new GoalDAOImpl();
        if (!savedChanges) {
            Log.d(TAG, "Saving data to db..");
            getGoalDataFromView();
            goalDataDAO.update(goalData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    GoalDataHolder.getInstance().setData(goalData);
                    showSuccessDialog();
                }
            });
            savedChanges = true;
        } else {
            Toast.makeText(context, "Nothing to save. No changes made", Toast.LENGTH_SHORT).show();
        }
    }

    void getGoalDataFromView() {
        goalData.setCalorieGoal(tvCalorieGoal.getText().toString().replaceAll("[^0-9]", ""));
        goalData.setExerciseTimeGoal(tvExerciseGoal.getText().toString().replaceAll("[^0-9]", ""));
        goalData.setWeightGoal(tvWeightGoal.getText().toString().replaceAll("[^0-9]", ""));
        goalData.setWaterIntakeGoal(tvWaterGoal.getText().toString().replaceAll("[^0-9]", ""));
        goalData.setStepGoal(tvStepGoal.getText().toString().replaceAll("[^0-9]", ""));
    }

    //<editor-fold desc="Showing success dialog">
    private void showSuccessDialog() {
        SuccessDialog successDialog = new SuccessDialog(context);
        successDialog.show();

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(successDialog::cancel, 2000, TimeUnit.MILLISECONDS);
    }
    //</editor-fold>

    private void setOnClickListeners() {
        tvCalorieGoal.setOnClickListener(v -> openDialog("Are you sure you want to change your Calorie Goal?", "Change Calorie Goal Dialog", tvCalorieGoal));
        tvExerciseGoal.setOnClickListener(v -> openDialog("Are you sure you want to change your Exercise Goal?", "Change Exercise Goal Dialog", tvExerciseGoal));
        tvWeightGoal.setOnClickListener(v -> openDialog("Are you sure you want to change your Weight Goal?", "Change Weight Goal Dialog", tvWeightGoal));
        tvWaterGoal.setOnClickListener(v -> openDialog("Are you sure you want to change your Water Goal?", "Change Water Goal Dialog", tvWaterGoal));
        tvStepGoal.setOnClickListener(v -> openDialog("Are you sure you want to change your Step Goal?", "Change Step Goal Dialog", tvStepGoal));
    }

    private void openDialog(String message, String tag, TextView textView) {
        Log.d(TAG, "Opening dialog " + tag);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("ok", (dialog, which) -> {
            ChangeGoalsDialog changeGoalsDialog = new ChangeGoalsDialog(textView);
            changeGoalsDialog.show(getSupportFragmentManager(), tag);
            savedChanges = false;
        });
        alertDialog.setNegativeButton("cancel", (dialog, which) -> {

        });

        alertDialog.create().show();
    }

    /********************************* TOOLBAR ****************************************************/
    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (!savedChanges) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setMessage("Changes are not saved. Are you sure you want to exit without saving?");
                alertDialog.setPositiveButton("ok", (dialog, which) -> {
                    savedChanges = true;
                    finish();
                });
                alertDialog.setNegativeButton("cancel", (dialog, which) -> savedChanges = false);
                alertDialog.create().show();
            }
        }
        if (savedChanges) {
            this.finish();
            return true;
        } else {
            return false;
        }
    }

    /********************************* LISTENER OVERRIDE ******************************************/

    @Override
    public void applyText(String text, TextView textView) {
        textView.setText(text);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setGoalDataToUI();
    }
}