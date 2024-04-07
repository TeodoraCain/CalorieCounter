package com.example.caloriecounter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.caloriecounter.model.DAO.GoalDAOImpl;
import com.example.caloriecounter.model.DAO.GoalData;
import com.example.caloriecounter.model.DAO.GoalDataDAO;
import com.example.caloriecounter.model.DAO.UserDetails;
import com.example.caloriecounter.model.dataHolder.GoalDataHolder;
import com.example.caloriecounter.model.dataHolder.UserDetailsHolder;
import com.example.caloriecounter.view.dialog.ChangeGoalsDialog;
import com.example.caloriecounter.view.dialog.SuccessDialog;

import java.text.MessageFormat;
import java.util.Objects;

public class GoalsActivity extends AppCompatActivity implements ChangeGoalsDialog.GoalsDialogListener {

    private TextView tvCalorieGoal, tvExerciseGoal, tvWeightGoal, tvWaterGoal, tvStepGoal;

    private GoalData goalData;
    private boolean savedChanges = true;

    private Context mContext;
    private final String TAG = "GoalsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);

        setToolbar();
        setUpViews();
        setOnClickListeners();
        setGoalDataToUI();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setOnClickListeners() {
        tvCalorieGoal.setOnClickListener(v -> openDialog("Are you sure you want to change your Calorie Goal?", "Change Calorie Goal Dialog", tvCalorieGoal));
        tvExerciseGoal.setOnClickListener(v -> openDialog("Are you sure you want to change your Exercise Goal?", "Change Exercise Goal Dialog", tvExerciseGoal));
        tvWeightGoal.setOnClickListener(v -> openDialog("Are you sure you want to change your Weight Goal?", "Change Weight Goal Dialog", tvWeightGoal));
        tvWaterGoal.setOnClickListener(v -> openDialog("Are you sure you want to change your Water Goal?", "Change Water Goal Dialog", tvWaterGoal));
        tvStepGoal.setOnClickListener(v -> openDialog("Are you sure you want to change your Step Goal?", "Change Step Goal Dialog", tvStepGoal));
    }

    private void setUpViews() {
        mContext = GoalsActivity.this;
        tvCalorieGoal = findViewById(R.id.tvCalorieGoal);
        tvExerciseGoal = findViewById(R.id.tvExerciseGoal);
        tvWeightGoal = findViewById(R.id.tvWeightGoal);
        tvWaterGoal = findViewById(R.id.tvWaterGoal);
        tvStepGoal = findViewById(R.id.tvStepGoal);
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

    private void setGoalDataToUI() {
        goalData = GoalDataHolder.getInstance().getData();
        UserDetails userDetails = UserDetailsHolder.getInstance().getData();

        tvCalorieGoal.setText(MessageFormat.format("{0} kcal", goalData.getCalorieGoal()));
        tvExerciseGoal.setText(MessageFormat.format("{0} min", goalData.getExerciseTimeGoal()));
        tvWeightGoal.setText(MessageFormat.format("{0} {1}", goalData.getWeightGoal(), userDetails.getWeightUnit()));
        tvWaterGoal.setText(MessageFormat.format("{0} ml", goalData.getWaterIntakeGoal()));
        tvStepGoal.setText(MessageFormat.format("{0} steps", goalData.getStepGoal()));

    }

    public void onSaveProfileData(View view) {
        GoalDataDAO goalDataDAO = new GoalDAOImpl();
        if (!savedChanges) {
            Log.d(TAG, "Saving data to db..");
            getGoalDataFromView();
            goalDataDAO.update(goalData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    GoalDataHolder.getInstance().setData(goalData);
                    SuccessDialog successDialog = new SuccessDialog(mContext);
                    successDialog.show();

                    new Handler().postDelayed(successDialog::cancel, 2000);
                }
            });
            savedChanges = true;
        } else {
            Toast.makeText(mContext, "Nothing to save. No changes made", Toast.LENGTH_SHORT).show();
        }
    }

    private void getGoalDataFromView() {
       // GoalData goalData = GoalDataHolder.getInstance().getData();

        goalData.setCalorieGoal(tvCalorieGoal.getText().toString().replaceAll("[^0-9]", ""));
        goalData.setExerciseTimeGoal(tvExerciseGoal.getText().toString().replaceAll("[^0-9]", ""));
        goalData.setWeightGoal(tvWeightGoal.getText().toString().replaceAll("[^0-9]", ""));
        goalData.setWaterIntakeGoal(tvWaterGoal.getText().toString().replaceAll("[^0-9]", ""));
        goalData.setStepGoal(tvStepGoal.getText().toString().replaceAll("[^0-9]", ""));

        //GoalDataHolder.getInstance().setData(goalData);
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

    @Override
    public void applyText(String text, TextView textView) {
        textView.setText(text);
    }
}