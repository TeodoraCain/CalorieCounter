package com.example.caloriecounter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
    private LinearLayout llMenu, llProfile, llSettings, llShare, llAboutUs, llLogout;
    // drawer
    private DrawerLayout drawerLayout;
    // bottom navigation
    private FloatingActionButton floatingActionButton;
    private BottomNavigationView bottomNavigationView;
    //launchers
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;

//        auth.signInWithCustomToken(mCustomToken)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCustomToken:success");
//                            FirebaseUser user = auth.getCurrentUser();
//                            updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCustomToken:failure", task.getException());
//                            Toast.makeText(CustoauthActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
//                        }
//                    }
//                });
    //
//    }

    public DashboardActivity() {
    }

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
        initLaunchers();
    }


    /********************************* SET UP ACTIVITY *********************************************/
    //region Activity SetUp
    private void setUpViews() {
        context = DashboardActivity.this;
        bottomNavigationView = findViewById(id.bottomNavigationView);
        floatingActionButton = findViewById(R.id.fab);
        drawerLayout = findViewById(id.drawerLayout);
        llMenu = findViewById(R.id.toolbar);
        llProfile = findViewById(R.id.nav_profile);
        llSettings = findViewById(R.id.nav_settings);
        llLogout = findViewById(R.id.nav_logout);
        llShare = findViewById(R.id.nav_share);
    }

    private void setUpFirebase() {
        auth = FirebaseAuth.getInstance();
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
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    private void initLaunchers() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bitmap image = (Bitmap) result.getData().getExtras().get("data");
                        // handle image here
                    }
                });

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        //TODO: handle gallery result
                    }
                });
    }
    //endregion

    /********************************* SET UP DRAWER ***********************************************/
    //region Drawer SetUp
    public void changeDrawerHeader() {
        //set user name in drawer header
        TextView userName = findViewById(R.id.tvUserName);
        userName.setText(Objects.requireNonNull(auth.getCurrentUser()).getDisplayName());

        //set user e-mail in drawer header
        TextView email = findViewById(R.id.tvUserEmail);
        email.setText(auth.getCurrentUser().getEmail());
    }

    private void setOnClickListeners() {
        llMenu.setOnClickListener(v -> NavigationHelper.openDrawer(drawerLayout));//openDrawer(drawerLayout));
        llProfile.setOnClickListener(this::goToUserProfile);
        llSettings.setOnClickListener(this::goToGoalSettings);
        llLogout.setOnClickListener(this::showLogoutConfirmation);
    }

    @SuppressWarnings("unused")
    private void goToGoalSettings(View v) {
        Log.d(TAG, "Staring GoalsActivity..");
        NavigationHelper.redirectTo(this, GoalsActivity.class);//redirectActivity(this, GoalsActivity.class);
        NavigationHelper.closeDrawer(drawerLayout);
    }

    @SuppressWarnings("unused")
    private void goToUserProfile(View v) {
        Log.d(TAG, "Staring ProfileActivity..");
        NavigationHelper.redirectTo(this, ProfileActivity.class);
        NavigationHelper.closeDrawer(drawerLayout);
    }

    @SuppressWarnings("unused")
    private void showLogoutConfirmation(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.logoutMessage);
        builder.setPositiveButton(R.string.yes, (dialog, which) -> signOutUser());
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
        });
        builder.show();
    }

    private void signOutUser() {
        Log.d(TAG, "Signing out..");
        auth.signOut();

        Intent mainActivity = new Intent(context, MainActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivity);
        finish();
    }
    //endregion

    /********************************* SET UP BOTTOM NAVIGATION ***********************************/
    //region Bottom Navigation
    @SuppressLint("NonConstantResourceId")
    private void setUpBottomNavigation() {
        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.diary:
                    replaceFragment(new DiaryFragment());
                    break;
                default:
                    //throw new IllegalStateException("Unexpected value: " + item.getItemId());
            }

            return true;
        });

        floatingActionButton.setOnClickListener(view -> showBottomDialog());
    }

    private void showBottomDialog() {
        //            @Override
//            public void onAddFromCameraSelected() {
//                DialogHelper.showImageSourceDialog(context, new DialogHelper.MediaClickListener() {
//                    @Override
//                    public void onCameraSelected() {
//                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
//                                != PackageManager.PERMISSION_GRANTED) {
//                            ActivityCompat.requestPermissions(
//                                    DashboardActivity.this,
//                                    new String[]{Manifest.permission.CAMERA},
//                                    CAMERA_PERMISSION_CODE
//                            );
//                        } else {
//                            openCamera();
//                        }
//                    }
//
//                    @Override
//                    public void onGallerySelected() {
//                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
//                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES)
//                                    != PackageManager.PERMISSION_GRANTED) {
//                                ActivityCompat.requestPermissions(
//                                        DashboardActivity.this,
//                                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
//                                        GALLERY_PERMISSION_CODE
//                                );
//                            } else {
//                                openGallery();
//                            }
//                        } else {
//                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
//                                    != PackageManager.PERMISSION_GRANTED) {
//                                ActivityCompat.requestPermissions(
//                                        DashboardActivity.this,
//                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                                        GALLERY_PERMISSION_CODE
//                                );
//                            } else {
//                                openGallery();
//                            }
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void onAddRecipeSelected() {
//                Intent recipeIntent = new Intent(context, NewRecipeActivity.class);
//                startActivity(recipeIntent);
//            }
        DialogHelper.showBottomSheetDialog(context, meal -> {
            Intent recipeIntent = new Intent(context, AddRecipeActivity.class);
            recipeIntent.putExtra(IntentKeys.MEAL, meal);
            startActivity(recipeIntent);
        });
    }

//    private void openGallery() {
//        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        galleryLauncher.launch(galleryIntent);
//    }
//
//    private void openCamera() {
//        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        cameraLauncher.launch(cameraIntent);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == CAMERA_PERMISSION_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                openCamera();
//            } else {
//                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
    //endregion

    /********************************* LIFECYCLE OVERRIDES *****************************************/
    //region Lifecycle Overrides
    @Override
    protected void onPause() {
        super.onPause();
        NavigationHelper.closeDrawer(drawerLayout);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        changeDrawerHeader();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if (resultCode == RESULT_OK) {
//            if (requestCode == 1) {
//                Bitmap image = (Bitmap) data.getExtras().get("data");
//                int dimension = Math.min(image.getWidth(), image.getHeight());
//                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
//                //imageView.setImageBitmap(image);
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
    //endregion
}

//    @Override
//    public void onStart() {
//        super.onStart();
////         Check if user is signed in (non-null) and update UI accordingly.
//        auth = FirebaseAuth.getInstance();
//        FirebaseUser currentUser = auth.getCurrentUser();
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
