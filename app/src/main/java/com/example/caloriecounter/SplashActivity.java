package com.example.caloriecounter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.caloriecounter.model.DAO.DailyData;
import com.example.caloriecounter.model.DAO.DailyDataDAO;
import com.example.caloriecounter.model.DAO.DailyDataDAOImpl;
import com.example.caloriecounter.model.DAO.Food;
import com.example.caloriecounter.model.DAO.FoodDAO;
import com.example.caloriecounter.model.DAO.FoodDAOImpl;
import com.example.caloriecounter.model.DAO.GoalDAOImpl;
import com.example.caloriecounter.model.DAO.GoalData;
import com.example.caloriecounter.model.DAO.GoalDataDAO;
import com.example.caloriecounter.model.DAO.UserDAO;
import com.example.caloriecounter.model.DAO.UserDAOImpl;
import com.example.caloriecounter.model.DAO.UserDetails;
import com.example.caloriecounter.model.dataHolder.DailyDataHolder;
import com.example.caloriecounter.model.dataHolder.FoodListHolder;
import com.example.caloriecounter.model.dataHolder.GoalDataHolder;
import com.example.caloriecounter.model.dataHolder.UserDetailsHolder;
import com.example.caloriecounter.model.dataHolder.WorkoutListHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private int dataRetrievalCount = 0;
    private static final int TOTAL_DATA_RETRIEVAL_TASKS = 4;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progressBar = findViewById(R.id.progressBar);

        FirebaseAuth authProfile = FirebaseAuth.getInstance();
        if (authProfile.getCurrentUser() != null) {
            initAppData();
        } else {
            progressBar.setVisibility(View.GONE);
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }, 3000);
        }
    }

    private void checkAndStartNewIntent() {
        dataRetrievalCount++;
        if (dataRetrievalCount == TOTAL_DATA_RETRIEVAL_TASKS) {
            // All data retrieval tasks are completed, start the new intent
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Finish the splash activity to prevent going back to it
        }
    }

    private void initFoodList() {
        FoodDAO foodDAO = new FoodDAOImpl();

        foodDAO.get().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Food> foodList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Food food = dataSnapshot.getValue(Food.class);
                    foodList.add(food);
                }
                FoodListHolder.getInstance().setData(foodList);
                checkAndStartNewIntent();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void initGoalData() {
        GoalDataDAO goalDataDAO = new GoalDAOImpl();
        goalDataDAO.get().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GoalData goalData = snapshot.getValue(GoalData.class);
                if (goalData == null) {
                    goalData = new GoalData();
                }
                GoalDataHolder.getInstance().setData(goalData);
                checkAndStartNewIntent();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SplashActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initUserDetails() {
        UserDAO userDAO = new UserDAOImpl();
        userDAO.get().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails userDetails = snapshot.getValue(UserDetails.class);
                if (userDetails != null) {
                    UserDetailsHolder.getInstance().setData(userDetails);
                }
                checkAndStartNewIntent();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SplashActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });

        retrieveUserProfilePicture();
    }

    private void retrieveUserProfilePicture() {
        UserDAO userDAO = new UserDAOImpl();
        userDAO.get().child("imageUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String imageUrl = dataSnapshot.getValue(String.class);

                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        SharedPreferences sharedPreferences = Objects.requireNonNull(SplashActivity.this).getSharedPreferences(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putString("imageUrl", imageUrl);
                        editor.apply();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SplashActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initDailyData() {
        DailyDataDAO dailyDataDAO = new DailyDataDAOImpl();

        dailyDataDAO.get().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DailyData dailyData = snapshot.getValue(DailyData.class);
                if (dailyData == null) {
                    dailyData = new DailyData();
                }
                DailyDataHolder.getInstance().setData(dailyData);
                WorkoutListHolder.getInstance().setData(dailyData.getWorkouts());
                checkAndStartNewIntent();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SplashActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initAppData() {
        initUserDetails();
        initGoalData();
        initFoodList();
        initDailyData();

    }
}