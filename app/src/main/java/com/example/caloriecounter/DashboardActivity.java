package com.example.caloriecounter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.caloriecounter.R.id;
import com.example.caloriecounter.models.dataModel.IntentKeys;
import com.example.caloriecounter.utils.DialogHelper;
import com.example.caloriecounter.utils.NavigationHelper;
import com.example.caloriecounter.view.fragments.dashboard.DiaryFragment;
import com.example.caloriecounter.view.fragments.dashboard.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "DashboardActivity";
    private Context context;
    private FirebaseAuth auth;
    private LinearLayout llMenu, llProfile, llGoals, llAboutUs, llLogout;//llShare,
    // drawer
    private DrawerLayout drawerLayout;
    // bottom navigation
    private FloatingActionButton floatingActionButton;
    private BottomNavigationView bottomNavigationView;

    public DashboardActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initActivity();
        setUpDrawerNavigation();
        setUpBottomNavigation();
        setUpViewFragments();

    }

    //region Init Activity
    private void initFirebase() {
        auth = FirebaseAuth.getInstance();
    }

    private void initViews() {
        context = DashboardActivity.this;
        bottomNavigationView = findViewById(id.bottomNavigationView);
        floatingActionButton = findViewById(R.id.fab);
        drawerLayout = findViewById(id.drawerLayout);
        llMenu = findViewById(R.id.toolbar);
        llProfile = findViewById(R.id.nav_profile);
        llGoals = findViewById(R.id.nav_goals);
        llLogout = findViewById(R.id.nav_logout);
        llAboutUs = findViewById(id.nav_about);

        ImageView ivNotifications = findViewById(id.ivNotifications);
        ivNotifications.setOnClickListener(v -> goToActivity(NotificationsActivity.class));
    }

    //endregion
    private void initActivity() {
        initFirebase();
        initViews();
    }

    //region Activity SetUp
    private void setUpViewFragments() {
        boolean fragment_flag = getIntent().getBooleanExtra(IntentKeys.NAVIGATE_TO_DIARY_FRAGMENT, false);

        if (fragment_flag) {
            replaceFragment(new DiaryFragment());
        } else {
            replaceFragment(new HomeFragment());
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
    //endregion

    //region Left Drawer SetUp
    public void initDrawerHeader() {
        //set user name in drawer header
        TextView userName = findViewById(R.id.tvUserName);
        userName.setText(Objects.requireNonNull(auth.getCurrentUser()).getDisplayName());

        //set user e-mail in drawer header
        TextView email = findViewById(R.id.tvUserEmail);
        email.setText(auth.getCurrentUser().getEmail());
    }

    private void setDrawerOnClickListeners() {
        llMenu.setOnClickListener(v -> NavigationHelper.openDrawer(drawerLayout));//openDrawer(drawerLayout));
        llProfile.setOnClickListener(v -> goToActivity(ProfileActivity.class));
        llGoals.setOnClickListener(v -> goToActivity(GoalsActivity.class));
        llAboutUs.setOnClickListener(v-> goToActivity(AboutUsActivity.class));
        llLogout.setOnClickListener(v -> showLogoutConfirmation());
    }

    private void goToActivity(Class<?> activity) {
        NavigationHelper.redirectTo(DashboardActivity.this, activity);
        NavigationHelper.closeDrawer(drawerLayout);
    }

    private void showLogoutConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.logoutMessage);
        builder.setPositiveButton(R.string.yes, (dialog, which) -> signOutUser());
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
        });
        builder.show();
    }

    private void signOutUser() {
        Log.d(TAG, "Signing out user..");
        auth.signOut();

        Intent mainActivity = new Intent(context, MainActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivity);
        finish();
    }

    //endregion
    private void setUpDrawerNavigation() {
        initDrawerHeader();
        setDrawerOnClickListeners();
    }

    //region Bottom Navigation
    private void setUpBottomNavigation() {
        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.diary) {
                replaceFragment(new DiaryFragment());
            } else {
                return false;
            }
            return true;
        });

        floatingActionButton.setOnClickListener(view -> showBottomDialog());
    }

    private void showBottomDialog() {
        DialogHelper.showBottomSheetDialog(context, meal -> {
            Intent recipeIntent = new Intent(context, AddRecipeActivity.class);
            recipeIntent.putExtra(IntentKeys.MEAL, meal);
            startActivity(recipeIntent);
        });
    }

    //region Lifecycle Overrides
    @Override
    protected void onPause() {
        super.onPause();
        NavigationHelper.closeDrawer(drawerLayout);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        initDrawerHeader();
    }
    //endregion

}


