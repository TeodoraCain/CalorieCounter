package com.example.caloriecounter;

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
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.caloriecounter.model.DAO.UserDetails;
import com.example.caloriecounter.model.DAO.WeightLog;
import com.example.caloriecounter.model.DAO.WeightLogDAO;
import com.example.caloriecounter.model.DAO.WeightLogDAOImpl;
import com.example.caloriecounter.model.dataHolder.UserDetailsHolder;
import com.example.caloriecounter.view.dialog.SuccessDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AddWeightActivity extends AppCompatActivity {

    private final String TAG = "AddWeightActivity";
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
        ivDeleteFrontPic.setOnClickListener(v-> deletePicture(ivDeleteFrontPic, ivPhotoFront, defaultFrontPicUri));
        ivDeleteSidePic.setOnClickListener(v-> deletePicture(ivDeleteSidePic, ivPhotoSide, defaultSidePicUri));
        ivDeleteBackPic.setOnClickListener(v-> deletePicture(ivDeleteBackPic, ivPhotoBack, defaultBackPicUri));
    }

    private void deletePicture(ImageView ivDelete, ImageView imageView, Uri defaultUri ) {
        imageView.setImageURI(defaultUri);
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
    }

    private void initializeWeight() {
        UserDetails userDetails = UserDetailsHolder.getInstance().getData();
        if (userDetails != null) {
            etWeight.setText(String.format(Locale.ENGLISH, "%.1f", Double.parseDouble(userDetails.getWeight())));
            tvWeightUnit.setText(userDetails.getWeightUnit());
        }
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
        Log.d(TAG, "Saving weight log entry..");

        String date = String.valueOf(etDate.getText());
        double weight = Double.parseDouble(String.valueOf(etWeight.getText()));

        // Get URIs of the images from the image views
        String frontPhotoUriStr = getPhotoUriStr(ivPhotoFront, defaultFrontPicUri);
        String sidePhotoUriStr = getPhotoUriStr(ivPhotoSide, defaultSidePicUri);
        String backPhotoUriStr = getPhotoUriStr(ivPhotoBack, defaultBackPicUri);

        WeightLog weightLog = new WeightLog(date, weight, frontPhotoUriStr, sidePhotoUriStr, backPhotoUriStr);

        WeightLogDAO weightLogDAO = new WeightLogDAOImpl();
        weightLogDAO.add(weightLog).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Weight log entry saved successfully..");
                SuccessDialog successDialog = new SuccessDialog(mContext);
                successDialog.show();
                new Handler().postDelayed(successDialog::cancel, 2000);

                savedChanges = true;
            } else {
                Log.d(TAG, "Weight log entry was not saved..");
            }
        });
    }

    private String getPhotoUriStr(ImageView imageView, Uri defaultUri) {
        Uri photoUri = getUriFromImageView(imageView);

        if (photoUri != null && photoUri != defaultUri) {
            return uploadImageToFirebaseStorage(photoUri);
        } else {
            return "";
        }
    }

    private String uploadImageToFirebaseStorage(Uri imageUri) {
        StorageReference storageRef = storageReference.child("weightPictures/" + firebaseUser.getUid() + ".jpg");
        final String[] uriStr = new String[1];
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> uriStr[0] = uri.toString()))
                .addOnFailureListener(exception -> Log.e(TAG, "Failed to upload image to Firebase Storage: " + exception.getMessage()));

        Log.d(TAG, uriStr[0]);
        return uriStr[0];
    }

    private Uri getUriFromImageView(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            // Convert bitmap to URI
            return getImageUri(mContext, bitmap);
        } else {
            return null; // Handle the case where the image view doesn't have an image
        }
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
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
        ActivityResultLauncher<Intent> imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null && data.getData() != null) {
                    Uri selectedImageUri = data.getData();
                    imageView.setImageURI(selectedImageUri);
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