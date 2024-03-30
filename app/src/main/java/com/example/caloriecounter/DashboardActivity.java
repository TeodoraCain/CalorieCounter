package com.example.caloriecounter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.caloriecounter.R.id;
import com.example.caloriecounter.view.fragments.dashboard.DiaryFragment;
import com.example.caloriecounter.view.fragments.dashboard.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class DashboardActivity extends AppCompatActivity {

    private final String TAG = "Dashboard Activity";
    private LinearLayout menu, profile, settings, share, about_us, logout;
    private FirebaseAuth mAuth;

    // Drawer activity
    private FloatingActionButton floatingActionButton;
    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;

    public DashboardActivity() {
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        boolean fragment_flag = getIntent().getBooleanExtra("NAVIGATE_TO_DIARY_FRAGMENT", false);
        Log.d("NAVGATE", String.valueOf(fragment_flag));

        mAuth = FirebaseAuth.getInstance();

        init();
        setOnClickListeners();
        changeDrawerHeader();
        setBottomNavigation();
        if (fragment_flag) {
            replaceFragment(new DiaryFragment());
        } else {
            replaceFragment(new HomeFragment());
        }


//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
        // Initialize Firebase Auth


//        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
//        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
//        drawerLayout.addDrawerListener(toggle);
//        toggle.syncState();
//
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).commit();
//            navigationView.setCheckedItem(R.id.nav_home);
//        }


//        mAuth.signInWithCustomToken(mCustomToken)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCustomToken:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCustomToken:failure", task.getException());
//                            Toast.makeText(CustomAuthActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
//                        }
//                    }
//                });
        //


    }

    private void setBottomNavigation() {
        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case id.diary:
                    replaceFragment(new DiaryFragment());
                    break;
//                case R.id.subscriptions:
//                    replaceFragment(new SubscriptionFragment());
//                    break;
//                case R.id.library:
//                    replaceFragment(new LibraryFragment());
//                    break;
            }

            return true;
        });

        floatingActionButton.setOnClickListener(view -> showBottomDialog());
    }

    private void setOnClickListeners() {
        menu.setOnClickListener(v -> openDrawer(drawerLayout));
        profile.setOnClickListener(v -> {
            goToUserProfile();
            closeDrawer(drawerLayout);
        });
        settings.setOnClickListener(v -> {
            goToGoalSettings();
            closeDrawer(drawerLayout);
        });
        logout.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
            builder.setMessage("Are you sure you want to logout?");
            builder.setPositiveButton("yes", (dialog, which) -> {
                mAuth.signOut();
                signOutUser();
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            builder.show();

        });

//        share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(DashboardActivity.this, FoodItemDisplayActivity.class);
//                startActivity(intent);
//            }
//        });

//        LinearLayout eim2 = findViewById(id.nav_eimRss);
//        eim2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(DashboardActivity.this, RssFeederActivity.class);
//                startActivity(intent);
//            }
//        });
//        LinearLayout eim3 = findViewById(id.nav_eimLanguage);
//        eim3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(DashboardActivity.this, LanguageActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        LinearLayout eim4 = findViewById(id.nav_eimFragment);
//        eim4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(DashboardActivity.this, SecondFragment.class);
//                startActivity(intent);
//            }
//        });

    }

    private void init() {
        bottomNavigationView = findViewById(id.bottomNavigationView);
        floatingActionButton = findViewById(id.fab);
        drawerLayout = findViewById(id.drawerLayout);
        menu = findViewById(id.toolbar);
        profile = findViewById(id.nav_profile);
        settings = findViewById(id.nav_settings);
        logout = findViewById(id.nav_logout);
        share = findViewById(id.nav_share);
    }

    private void goToGoalSettings() {
        Intent goalSettings = new Intent(DashboardActivity.this, GoalsActivity.class);
        startActivity(goalSettings);
    }


    private void signOutUser() {
        Intent mainActivity = new Intent(DashboardActivity.this, MainActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivity);
        finish();
    }

    public void goToUserProfile() {
        Intent userProfile = new Intent(DashboardActivity.this, ProfileActivity.class);
        startActivity(userProfile);
    }

    public void changeDrawerHeader() {
        //set user name in drawer header
        TextView userName = (TextView) findViewById(R.id.tvUserName);
        userName.setText(mAuth.getCurrentUser().getDisplayName());

        //set user e-mail in drawer header
        TextView email = (TextView) findViewById(R.id.tvUserEmail);
        email.setText(mAuth.getCurrentUser().getEmail());

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        changeDrawerHeader();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    private void showBottomDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);

        LinearLayout breakfastLayout = dialog.findViewById(R.id.layoutBreakfast);
        LinearLayout lunchLayout = dialog.findViewById(id.layoutLunch);
        LinearLayout dinnerLayout = dialog.findViewById(id.layoutDinner);
        LinearLayout snackLayout = dialog.findViewById(id.layoutSnack);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        breakfastLayout.setOnClickListener(v -> {
            dialog.dismiss();
            startFoodActivity("Breakfast");
        });

        lunchLayout.setOnClickListener(v -> {
            dialog.dismiss();
            startFoodActivity("Lunch");
        });

        dinnerLayout.setOnClickListener(v -> {
            dialog.dismiss();
            startFoodActivity("Dinner");
        });

        snackLayout.setOnClickListener(v -> {
            dialog.dismiss();
            startFoodActivity("Snacks");
        });

        cancelButton.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    private void startFoodActivity(String meal) {
        Intent intent = new Intent(DashboardActivity.this, FoodActivity.class);
        intent.putExtra("MEAL", meal);
        startActivity(intent);
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public static void redirectActivity(Activity activity, Class destinationActivity) {
        Intent intent = new Intent(activity, destinationActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }

//    @Override
//    public void onStart() {
//        super.onStart();
////         Check if user is signed in (non-null) and update UI accordingly.
//        mAuth = FirebaseAuth.getInstance();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
////         updateUI(currentUser);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (toggle.onOptionsItemSelected(item)) {
//            Log.d("NavigationItemSelected", "Item selected: " + item.getTitle());
////            switch (item.getItemId()) {
////                case R.id.nav_home:
////                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new DiaryFragment()).commit();
////                    break;
////                case R.id.nav_settings:
////                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).commit();
////                    break;
////            }
//            return true;
//
//        }
//        return super.onOptionsItemSelected(item);
//    }
//        Log.d("NavigationItemSelected", "Item selected: " + item.getTitle());
//
//        drawerLayout.closeDrawer(GravityCompat.START);
//        return true;
//}

//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//
//        switch (item.getItemId()) {
//
//            case R.id.nav_home:
//
//                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).commit();
//
//                break;
//
//            case R.id.nav_settings:
//
//                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new DiaryFragment()).commit();
//
//                break;
//
//            case R.id.nav_share:
//
//                Toast.makeText(this, "share!", Toast.LENGTH_SHORT).show();
//
//                break;
//
//            case R.id.nav_about:
//
//                Toast.makeText(this, "about!", Toast.LENGTH_SHORT).show();
//
//                break;
//
//            case R.id.nav_logout:
//
//                Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show();
//
//                break;
//
//        }
//
//        drawerLayout.closeDrawer(GravityCompat.START);
//
//        return true;
//
//    }

//    @Override
//    public void onBackPressed(){
//        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
//            drawerLayout.closeDrawer(GravityCompat.START);
//        }else{
//            super.onBackPressed();
//        }
//    }

//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        // Sync the toggle state after onRestoreInstanceState has occurred.
//        if (toggle != null) {
//            toggle.syncState();
//        }
//    }
}