package com.example.caloriecounter;

import static com.example.caloriecounter.R.id.diary;
import static com.example.caloriecounter.R.id.fab;
import static com.example.caloriecounter.R.id.frameLayout;
import static com.example.caloriecounter.R.id.home;
import static com.example.caloriecounter.R.id.layoutBreakfast;
import static com.example.caloriecounter.R.id.layoutDinner;
import static com.example.caloriecounter.R.id.layoutLunch;
import static com.example.caloriecounter.R.id.layoutSnack;
import static com.example.caloriecounter.R.id.nav_logout;
import static com.example.caloriecounter.R.id.nav_profile;
import static com.example.caloriecounter.R.id.nav_settings;
import static com.example.caloriecounter.R.id.nav_share;
import static com.example.caloriecounter.R.id.toolbar;
import static com.example.caloriecounter.R.id.tvUserEmail;
import static com.example.caloriecounter.R.id.tvUserName;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
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

import java.util.Objects;

public class DashboardActivity extends AppCompatActivity {

    private final String TAG = "DashboardActivity";
    private Context mContext;
    private FirebaseAuth mAuth;

    private LinearLayout llMenu, llProfile, llSettings, llShare, llAboutUs, llLogout;
    // drawer
    private DrawerLayout drawerLayout;
    // bottom navigation
    private FloatingActionButton floatingActionButton;
    private BottomNavigationView bottomNavigationView;

    public DashboardActivity() {
    }

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
//    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        setUpFirebase();
        setUpViews();
        setOnClickListeners();
        changeDrawerHeader();
        setUpBottomNavigation();
        setUpViewFragment();
    }

    /********************************* SET UP ACTIVITY *********************************************/
    private void setUpViews() {
        mContext = DashboardActivity.this;
        bottomNavigationView = findViewById(id.bottomNavigationView);
        floatingActionButton = findViewById(fab);
        drawerLayout = findViewById(id.drawerLayout);
        llMenu = findViewById(toolbar);
        llProfile = findViewById(nav_profile);
        llSettings = findViewById(nav_settings);
        llLogout = findViewById(nav_logout);
        llShare = findViewById(nav_share);
    }

    private void setUpFirebase() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void setUpViewFragment() {
        boolean fragment_flag = getIntent().getBooleanExtra("NAVIGATE_TO_DIARY_FRAGMENT", false);

        if (fragment_flag) {
            replaceFragment(new DiaryFragment());
        } else {
            replaceFragment(new HomeFragment());
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(frameLayout, fragment);
        fragmentTransaction.commit();
    }

    /********************************* SET UP DRAWER ***********************************************/
    public void changeDrawerHeader() {
        //set user name in drawer header
        TextView userName = findViewById(tvUserName);
        userName.setText(Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName());

        //set user e-mail in drawer header
        TextView email = findViewById(tvUserEmail);
        email.setText(mAuth.getCurrentUser().getEmail());
    }

    private void setOnClickListeners() {
        llMenu.setOnClickListener(v -> openDrawer(drawerLayout));
        llProfile.setOnClickListener(this::goToUserProfile);
        llSettings.setOnClickListener(this::goToGoalSettings);
        llLogout.setOnClickListener(this::showLogoutConfirmation);

        // ***************** EIM ************************

//        share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, EIMMenu.class);
//                startActivity(intent);
//            }
//        });
//
//        LinearLayout eim2 = findViewById(nav_eimRss);
//        eim2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, RssFeederActivityEIM.class);
//                startActivity(intent);
//            }
//        });
//        LinearLayout eim3 = findViewById(nav_eimLanguage);
//        eim3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, LanguageActivityEIM.class);
//                startActivity(intent);
//            }
//        });
//
//        LinearLayout eim4 = findViewById(nav_eimFragment);
//        eim4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, SecondFragmentEIM.class);
//                startActivity(intent);
//            }
//        });

    }

    @SuppressWarnings("unused")
    private void goToGoalSettings(View v) {
        Log.d(TAG, "Staring GoalsActivity..");
        redirectActivity(this, GoalsActivity.class);
        closeDrawer(drawerLayout);
    }

    @SuppressWarnings("unused")
    private void goToUserProfile(View v) {
        Log.d(TAG, "Staring ProfileActivity..");
        redirectActivity(this, ProfileActivity.class);
        closeDrawer(drawerLayout);
    }

    public static void redirectActivity(Activity activity, Class<?> destinationActivity) {
        Intent intent = new Intent(activity, destinationActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        activity.startActivity(intent);
    }

    @SuppressWarnings("unused")
    private void showLogoutConfirmation(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("yes", (dialog, which) -> signOutUser());
        builder.setNegativeButton("cancel", (dialog, which) -> {
        });
        builder.show();
    }

    private void signOutUser() {
        Log.d(TAG, "Signing out..");
        mAuth.signOut();

        Intent mainActivity = new Intent(mContext, MainActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivity);
        finish();
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    /********************************* SET UP BOTTOM NAVIGATION ***********************************/
    @SuppressLint("NonConstantResourceId")
    private void setUpBottomNavigation() {
        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case home:
                    replaceFragment(new HomeFragment());
                    break;
                case diary:
                    replaceFragment(new DiaryFragment());
                    break;
//                case R.id.subscriptions:
//                    replaceFragment(new SubscriptionFragment());
//                    break;
//                case R.id.library:
//                    replaceFragment(new LibraryFragment());
//                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + item.getItemId());
            }

            return true;
        });

        floatingActionButton.setOnClickListener(view -> showBottomDialog());
    }

    private void showBottomDialog() {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.cutom_layout_bottom_sheet);

        LinearLayout breakfastLayout = dialog.findViewById(layoutBreakfast);
        LinearLayout lunchLayout = dialog.findViewById(layoutLunch);
        LinearLayout dinnerLayout = dialog.findViewById(layoutDinner);
        LinearLayout snackLayout = dialog.findViewById(layoutSnack);
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
        Log.d(TAG, "Starting FoodActivity..");
        Intent intent = new Intent(mContext, AddFoodActivity.class);
        intent.putExtra("MEAL", meal);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /********************************* LIFECYCLE OVERRIDES *****************************************/
    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        changeDrawerHeader();
    }
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
