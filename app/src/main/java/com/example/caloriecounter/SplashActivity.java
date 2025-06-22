package com.example.caloriecounter;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.caloriecounter.models.dao.DailyData;
import com.example.caloriecounter.models.dao.DailyDataDAO;
import com.example.caloriecounter.models.dao.DailyDataDAOImpl;
import com.example.caloriecounter.models.dao.Food;
import com.example.caloriecounter.models.dao.FoodDAO;
import com.example.caloriecounter.models.dao.FoodDAOImpl;
import com.example.caloriecounter.models.dao.GoalDAOImpl;
import com.example.caloriecounter.models.dao.GoalData;
import com.example.caloriecounter.models.dao.GoalDataDAO;
import com.example.caloriecounter.models.dao.Recipe;
import com.example.caloriecounter.models.dao.RecipeDAO;
import com.example.caloriecounter.models.dao.RecipeDAOImpl;
import com.example.caloriecounter.models.dao.UserDAO;
import com.example.caloriecounter.models.dao.UserDAOImpl;
import com.example.caloriecounter.models.dao.UserDetails;
import com.example.caloriecounter.models.dataHolders.DailyDataHolder;
import com.example.caloriecounter.models.dataHolders.FoodListHolder;
import com.example.caloriecounter.models.dataHolders.GoalDataHolder;
import com.example.caloriecounter.models.dataHolders.RecipeListHolder;
import com.example.caloriecounter.models.dataHolders.UserDetailsHolder;
import com.example.caloriecounter.models.dataHolders.WorkoutListHolder;
import com.example.caloriecounter.services.StepDataUploadWorker;
import com.example.caloriecounter.services.StepService;
import com.example.caloriecounter.utils.AlarmUtils;
import com.example.caloriecounter.utils.UserUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final int TOTAL_DATA_RETRIEVAL_TASKS = 4;
    private final String TAG = "SplashActivity";
    private Context context;
    private int dataRetrievalCount = 0;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initContext();
        setUpViews();
        checkLoggedUser();

        //step counter service
        Intent intent = new Intent(this, StepService.class);
        startForegroundService(intent);
        scheduleDailyStepUpload(this);

        //notifications
        createNotificationChannel();
        AlarmUtils.scheduleWaterReminder(this);
    }

    //region Notifications
    private void createNotificationChannel() {
        CharSequence name = "WaterReminderChannel";
        String description = "Channel for Water Intake Reminders";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel("WATER_REMINDER_CHANNEL", name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    public void scheduleDailyStepUpload(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build();

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                StepDataUploadWorker.class,
                1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "step_upload_work",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
        );
    }

    private long calculateInitialDelay() {
        Calendar now = Calendar.getInstance();
        Calendar nextRun = (Calendar) now.clone();
        nextRun.set(Calendar.HOUR_OF_DAY, 23);
        nextRun.set(Calendar.MINUTE, 50);
        nextRun.set(Calendar.SECOND, 0);

        if (nextRun.before(now)) {
            nextRun.add(Calendar.DAY_OF_MONTH, 1);
        }

        return nextRun.getTimeInMillis() - now.getTimeInMillis();
    }

    //endregion

    /********************************* INIT ACTIVITY **********************************************/
    private void initContext() {
        context = SplashActivity.this;
    }

    private void setUpViews() {
        progressBar = findViewById(R.id.progressBar);
    }


    private void checkLoggedUser() {
        FirebaseAuth authProfile = FirebaseAuth.getInstance();
        if (authProfile.getCurrentUser() != null) {
            Log.d(TAG, "User is connected..");
            initAppData();
        } else {
            Log.d(TAG, "User is not connected. Starting MainActivity..");
            progressBar.setVisibility(View.GONE);
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
                finish();
            }, 3000);
        }
    }


    private void checkAndStartNewIntent() {
        dataRetrievalCount++;
        if (dataRetrievalCount == TOTAL_DATA_RETRIEVAL_TASKS) {
            // All data retrieval tasks are completed, start the new intent
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            finish(); // Finish the splash activity to prevent going back to it
        }
    }

    /********************************* INIT APP DATA **********************************************/
    private void initAppData() {
        initUserDetails();
        initGoalData();
        initFoodList();
        initDailyData();
        initRecipeData();
    }

    private void initRecipeData() {
        RecipeDAO recipeDAO = new RecipeDAOImpl();
        recipeDAO.get().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Recipe data initiated.");
                List<Recipe> recipeList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Recipe recipe = dataSnapshot.getValue(Recipe.class);
                    recipeList.add(recipe);
                }
                RecipeListHolder.getInstance().setData(recipeList);
                checkAndStartNewIntent();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Recipe data failed to initiate.");
            }
        });
    }

    private void initFoodList() {
        FoodDAO foodDAO = new FoodDAOImpl();
        foodDAO.get().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Food data initiated.");
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
                Log.d(TAG, "Food data failed to initiate.");
            }
        });
    }

    private void initGoalData() {
        GoalDataDAO goalDataDAO = new GoalDAOImpl();
        goalDataDAO.get().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Goal data initiated.");
                GoalData goalData = snapshot.getValue(GoalData.class);
                if (goalData == null) {
                    goalData = new GoalData();
                }
                GoalDataHolder.getInstance().setData(goalData);
                checkAndStartNewIntent();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Goal data failed to initiate.");
            }
        });

    }

    private void initUserDetails() {
        UserDAO userDAO = new UserDAOImpl();
        userDAO.get().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "User data initiated.");
                UserDetails userDetails = snapshot.getValue(UserDetails.class);
                if (userDetails != null) {
                    UserDetailsHolder.getInstance().setData(userDetails);
                }
                checkAndStartNewIntent();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "User data failed to initiate.");
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
                    Log.d(TAG, "User profile picture retrieved.");
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        SharedPreferences sharedPreferences = Objects.requireNonNull(context).getSharedPreferences(UserUtils.getFirebaseUID(), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putString("imageUrl", imageUrl);
                        editor.apply();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "User data failed to initialize.");
            }
        });
    }

    private void initDailyData() {
        DailyDataDAO dailyDataDAO = new DailyDataDAOImpl();

        dailyDataDAO.get().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Daily data initiated.");
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
                Log.d(TAG, "Daily data failed to initialize.");
            }
        });
    }
}