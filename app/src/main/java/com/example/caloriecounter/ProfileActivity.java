package com.example.caloriecounter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.caloriecounter.model.DAO.UserDAO;
import com.example.caloriecounter.model.DAO.UserDAOImpl;
import com.example.caloriecounter.model.DAO.UserDetails;
import com.example.caloriecounter.model.dataHolder.UserDetailsHolder;
import com.example.caloriecounter.view.dialog.ChangeProfileInfoDialog;
import com.example.caloriecounter.view.dialog.SuccessDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements ChangeProfileInfoDialog.DialogListener {

    private TextView tvName, tvEmail, tvDOB, tvGender, tvCountry, tvPassword;
    private String gender;
    private CircleImageView ivProfilePicture;
    private boolean imagePickerOpen = false;
    private boolean savedChanges = true;

    private UserDAO userDAO;
    private UserDetails userDetails;

    private ActivityResultLauncher<Intent> imagePickLauncher;
    private Uri selectedImageUri;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;

    private final String TAG = "EditProfileActivity";
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setToolbar();
        init();

        if (firebaseUser == null) {
            Toast.makeText(ProfileActivity.this, "Something went wrong! User details not available.", Toast.LENGTH_SHORT).show();
        } else {
            showUserProfile(firebaseUser);
        }

//      change profile picture
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null && data.getData() != null) {
                    selectedImageUri = data.getData();
                    ivProfilePicture.setImageURI(selectedImageUri);
                }
            }
        });

        ivProfilePicture.setOnClickListener(v -> {
            if (!imagePickerOpen) {
                imagePickerOpen = true;
                Intent photoPicker = new Intent();
                photoPicker.setAction(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                imagePickLauncher.launch(photoPicker);
                imagePickerOpen = false;
                savedChanges = false;
            }
        });

        tvName.setOnClickListener(v -> openDialog("Are you sure you want to change your name?", "Change Name Dialog", tvName));
        tvDOB.setOnClickListener(v -> openDialog("Are you sure you want to change your date of birth?", "Change DOB Dialog", tvDOB));
        tvGender.setOnClickListener(v -> openDialog("Are you sure you want to change your gender?\nThis will affect your BMI and calorie intake calculations.", "Change Gender Dialog", tvGender));
        tvCountry.setOnClickListener(v -> openDialog("Are you sure you want to change your country of residence?", "Change Country Dialog", tvCountry));
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void init() {
        context = this;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        userDAO = new UserDAOImpl();

        userDetails = UserDetailsHolder.getInstance().getData();

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvDOB = findViewById(R.id.tvDOB);
        tvGender = findViewById(R.id.tvGender);
        tvPassword = findViewById(R.id.tvPassword);
        tvCountry = findViewById(R.id.tvCountry);

        ivProfilePicture = findViewById(R.id.ivProfilePicture);
    }

    private void openDialog(String message, String tag, TextView textView) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("ok", (dialog, which) -> {
            ChangeProfileInfoDialog changeNameDialog = new ChangeProfileInfoDialog(textView);
            changeNameDialog.show(getSupportFragmentManager(), tag);
            savedChanges = false;
        });
        alertDialog.setNegativeButton("cancel", (dialog, which) -> {

        });

        alertDialog.create().show();
    }

    private void showUserProfile(FirebaseUser user) {
        if (userDetails != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            String doB = userDetails.getDob();
            gender = userDetails.getGender();
            String country = userDetails.getCountry();

            tvName.setText(name);
            tvEmail.setText(email);
            tvDOB.setText(doB);
            tvGender.setText(gender);
            tvCountry.setText(country);
            tvPassword.setText("********");
            tvPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }

        SharedPreferences sharedPreferences = getSharedPreferences(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), Context.MODE_PRIVATE);
        String imageUrl = sharedPreferences.getString("imageUrl", "");
        if (!imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl)
                    .rotate(90)
                    .into(ivProfilePicture);
        } else {
            setBaseProfileImage(gender);
        }

    }

    private void setBaseProfileImage(String gender) {
        if (gender.equals(getString(R.string.female_text))) {
            ivProfilePicture.setImageResource(R.drawable.profile_icon_female);
        } else {
            ivProfilePicture.setImageResource(R.drawable.profile_icon_male);
        }
    }


    public void onSaveProfileData(View view) {
        Toast.makeText(ProfileActivity.this, "Saving changes..", Toast.LENGTH_SHORT).show();
        if (!savedChanges) {
            UserDetails writeUserDetails = userDetails;
            writeUserDetails.setCountry(tvCountry.getText().toString());
            writeUserDetails.setDob(tvDOB.getText().toString());
            writeUserDetails.setGender(tvGender.getText().toString());

            userDAO.update(writeUserDetails).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    SuccessDialog successDialog = new SuccessDialog(context);
                    successDialog.show();

                    new Handler().postDelayed(successDialog::cancel, 2000);
                }
            });

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(tvName.getText().toString())
//                    .setPhotoUri(selectedImageUri)
                    .build();

            firebaseUser.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> Log.d(TAG, "User profile updated."));

            if (selectedImageUri != null) {
                uploadImageToFirebaseStorage(selectedImageUri);
                Log.d(TAG, "Selected Image Uri: " + selectedImageUri.toString());
            }
            savedChanges = true;

        }
    }

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        StorageReference storageRef = storageReference.child("profilePicture/" + firebaseUser.getUid() + ".jpg");

        // Upload the image file to Firebase Storage
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully
                    // Get the download URL of the uploaded image
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        userDAO.get().child("imageUrl").setValue(imageUrl);
                    });
                })
                .addOnFailureListener(exception -> Log.e(TAG, "Failed to upload image to Firebase Storage: " + exception.getMessage()));
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