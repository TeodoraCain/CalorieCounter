package com.example.caloriecounter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.caloriecounter.model.DAO.WeightLog;
import com.example.caloriecounter.model.DAO.WeightLogDAO;
import com.example.caloriecounter.model.DAO.WeightLogDAOImpl;
import com.example.caloriecounter.model.dataHolder.UserDetailsHolder;
import com.example.caloriecounter.view.dialog.SuccessDialog;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class AddWeightActivity extends AppCompatActivity {

    private final String TAG = "AddWeightActivity";
    private Uri frontPhotoUri,
            sidePhotoUri,
            backPhotoUri;
    private EditText etDate;
    private EditText etWeight;
    private ImageView ivPhotoFront,
            ivPhotoSide,
            ivPhotoBack;
    private ImageView ivDeleteFrontPic,
            ivDeleteSidePic,
            ivDeleteBackPic;
    private boolean imagePickerOpen = false;
    private Button btnSave;
    private TextView tvWeightUnit;
    private boolean savedChanges;
    private Context mContext;
    private Uri defaultFrontPicUri,
            defaultSidePicUri,
            defaultBackPicUri;
    private String frontPhotoUriStr,
            sidePhotoUriStr,
            backPhotoUriStr;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_weight);

        setUpViews();
        initializeViews();
        initializeDefaultUris();
        setUpFirebase();
        setUpDatePicker();
        setUpImagePickers();
        setUpImageRemovers();
        setUpSaveButton();
    }

    private void setUpImageRemovers() {
        ivDeleteFrontPic.setOnClickListener(v -> deletePicture(ivDeleteFrontPic, ivPhotoFront));
        ivDeleteSidePic.setOnClickListener(v -> deletePicture(ivDeleteSidePic, ivPhotoSide ));
        ivDeleteBackPic.setOnClickListener(v -> deletePicture(ivDeleteBackPic, ivPhotoBack ));
    }

    @SuppressLint("NonConstantResourceId")
    private void deletePicture(ImageView ivDelete, ImageView imageView) {
        switch (imageView.getId()) {
            case R.id.ivPhotoFront:
                imageView.setImageResource(R.drawable.woman_front);
                break;
            case R.id.ivPhotoSide:
                imageView.setImageResource(R.drawable.woman_side);
                break;
            case R.id.ivPhotoBack:
                imageView.setImageResource(R.drawable.woman_back);
        }
        ivDelete.setImageResource(R.drawable.ic_camera);
    }

    private void initializeDefaultUris() {
        defaultFrontPicUri = getUriFromImageView(ivPhotoFront);
        defaultSidePicUri = getUriFromImageView(ivPhotoSide);
        defaultBackPicUri = getUriFromImageView(ivPhotoBack);
    }

    private void setUpFirebase() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    private void initializeViews() {
        initializeDate();
        initializeWeight();
        savedChanges = true;
    }

    private void initializeWeight() {
        UserDetails userDetails = UserDetailsHolder.getInstance().getData();
        if (userDetails != null) {
            etWeight.setText(String.format(Locale.ENGLISH, "%.1f", Double.parseDouble(userDetails.getWeight())));
            tvWeightUnit.setText(userDetails.getWeightUnit());
        }
        etWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                savedChanges = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initializeDate() {
        String currentDate = getCurrentDate();
        etDate.setText(currentDate);
    }

    @NonNull
    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        return new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(date);
    }

    private void setUpSaveButton() {
        btnSave.setOnClickListener(this::save);
    }

    private void save(View view) {
        if (savedChanges) {
            Toast.makeText(mContext, "No changes made. Data is already saved.", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "Saving weight log entry..");

        String date = String.valueOf(etDate.getText());
        double weight = Double.parseDouble(String.valueOf(etWeight.getText()));

        List<Task<Uri>> uploadTasks = new ArrayList<>();
        if (frontPhotoUri != null && frontPhotoUri != defaultFrontPicUri) {
            uploadTasks.add(uploadImageToFirebaseStorage(frontPhotoUri));
        }
        if (sidePhotoUri != null && sidePhotoUri != defaultSidePicUri) {
            uploadTasks.add(uploadImageToFirebaseStorage(sidePhotoUri));
        }
        if (backPhotoUri != null && backPhotoUri != defaultBackPicUri) {
            uploadTasks.add(uploadImageToFirebaseStorage(backPhotoUri));
        }

        // Wait for all tasks to complete
        Tasks.whenAllComplete(uploadTasks)
                .addOnSuccessListener(results -> {
                    // Extract download URLs from results if needed
                    for (int i = 0; i < results.size(); i++) {
                        Task<Uri> uploadTask = uploadTasks.get(i);
                        if (uploadTask.isSuccessful()) {
                            Uri downloadUri = uploadTask.getResult();
                            switch (i) {
                                case 0:
                                    frontPhotoUriStr = downloadUri.toString();
                                    break;
                                case 1:
                                    sidePhotoUriStr = downloadUri.toString();
                                    break;
                                case 2:
                                    backPhotoUriStr = downloadUri.toString();
                                    break;
                            }

                        } else {
                            // Handle failure of individual upload task
                            Exception e = uploadTask.getException();
                            Log.e(TAG, "Failed to upload image " + (i + 1) + ": " + e.getMessage());
                        }
                    }

                    // Create weight log object with download URLs
                    WeightLog weightLog = new WeightLog(date, weight, frontPhotoUriStr, sidePhotoUriStr, backPhotoUriStr);


                    // Add weight log to database
                    WeightLogDAO weightLogDAO = new WeightLogDAOImpl();
                    weightLogDAO.add(weightLog).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Weight log entry saved successfully
                            Log.d(TAG, "Weight log entry saved successfully..");
                            savedChanges = true;
                            SuccessDialog successDialog = new SuccessDialog(mContext);
                            successDialog.show();
                            new Handler().postDelayed(successDialog::cancel, 2000);
                            if (weightLog.getDate().equals(getCurrentDate())) {
                                // Update user details if weight log date is today
                                UserDetails userDetails = UserDetailsHolder.getInstance().getData();
                                userDetails.setWeight(String.valueOf(weightLog.getWeight()));
                                UserDAO userDAO = new UserDAOImpl();
                                userDAO.update(userDetails).addOnCompleteListener(userUpdateTask -> {
                                    if (userUpdateTask.isSuccessful()) {
                                        Log.d(TAG, "User details updated with weight");
                                    }
                                });
                            }
                            savedChanges = true;
                        } else {
                            // Weight log entry was not saved
                            Log.d(TAG, "Weight log entry was not saved..");
                        }
                    });
                })
                .addOnFailureListener(exception -> {
                    Log.e(TAG, "Failed to upload one or more images to Firebase Storage: " + exception.getMessage());
                });

    }

//    private void getPhotoUriStr(ImageView imageView, Uri defaultUri) {
//        Uri photoUri = getUriFromImageView(imageView);
//
//        if (photoUri != null && photoUri != defaultUri) {
//            uploadImageToFirebaseStorage(photoUri, imageView);
//        }
//    }

    private Task<Uri> uploadImageToFirebaseStorage(Uri imageUri) {
        StorageReference storageRef = storageReference.child("weightPictures/" + firebaseUser.getUid() + "/" + UUID.randomUUID().toString() + ".jpg");
        return storageRef.putFile(imageUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful() && task.getException() != null) {
                        throw task.getException();
                    }
                    // Return the download URL of the uploaded image
                    return storageRef.getDownloadUrl();
                });
    }

    private Uri getUriFromImageView(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            // Convert bitmap to URI
            return Uri.parse(((BitmapDrawable) drawable).getBitmap().toString());
            //return getImageUri(mContext, bitmap);
        } else {
            return null; // Handle the case where the image view doesn't have an image
        }
    }
    private void setUpViews() {
        setToolbar();
        mContext = AddWeightActivity.this;
        tvWeightUnit = findViewById(R.id.tvWeightUnit);
        btnSave = findViewById(R.id.btnSave);

        etDate = findViewById(R.id.etDate);
        etWeight = findViewById(R.id.etWeight);

        ivPhotoBack = findViewById(R.id.ivPhotoBack);
        ivPhotoFront = findViewById(R.id.ivPhotoFront);
        ivPhotoSide = findViewById(R.id.ivPhotoSide);

        ivDeleteFrontPic = findViewById(R.id.ivDeleteFrontPic);
        ivDeleteSidePic = findViewById(R.id.ivDeleteSidePic);
        ivDeleteBackPic = findViewById(R.id.ivDeleteBackPic);
    }

    private void setUpImagePickers() {
        Log.d(TAG, "Setting up the image pickers..");
        setUpImagePicker(ivPhotoFront, ivDeleteFrontPic);
        setUpImagePicker(ivPhotoSide, ivDeleteSidePic);
        setUpImagePicker(ivPhotoBack, ivDeleteBackPic);
    }

    private void setUpDatePicker() {
        Log.d(TAG, "Setting up the date picker..");
        etDate.setOnClickListener(this::showDatePicker);
    }

    private void showDatePicker(View v) {
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog picker = new DatePickerDialog(mContext, (view, year1, month1, dayOfMonth) ->
                etDate.setText(String.format(Locale.ENGLISH, "%02d-%02d-%d", dayOfMonth, month1 + 1, year1)), year, month, day);
        picker.show();
        savedChanges = false;
    }

    private void setUpImagePicker(ImageView imageView, ImageView ivDeletePic) {
        @SuppressLint("NonConstantResourceId") ActivityResultLauncher<Intent> imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null && data.getData() != null) {
                    Uri selectedImageUri = data.getData();
                    imageView.setImageURI(selectedImageUri);
                    switch (imageView.getId()) {
                        case R.id.ivPhotoFront:
                            frontPhotoUri = selectedImageUri;
                            break;
                        case R.id.ivPhotoSide:
                            sidePhotoUri = selectedImageUri;
                            break;
                        case R.id.ivPhotoBack:
                            backPhotoUri = selectedImageUri;
                    }
                    savedChanges = false;
                    ivDeletePic.setImageResource(R.drawable.ic_close);
                }
            }
        });
        imageView.setOnClickListener(v -> showImagePicker(imagePickLauncher));
    }

    private void showImagePicker(ActivityResultLauncher<Intent> imagePickLauncher) {
        if (!imagePickerOpen) {
            imagePickerOpen = true;
            launchImagePicker(imagePickLauncher);
            imagePickerOpen = false;
        }
    }

    private void launchImagePicker(ActivityResultLauncher<Intent> imagePickLauncher) {
        Intent photoPicker = new Intent();
        photoPicker.setAction(Intent.ACTION_PICK);
        photoPicker.setType("image/*");
        imagePickLauncher.launch(photoPicker);
    }


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
}