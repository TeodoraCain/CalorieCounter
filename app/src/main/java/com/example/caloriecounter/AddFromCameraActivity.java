package com.example.caloriecounter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class AddFromCameraActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_CODE = 101;

    private ImageView ivCamera, ivGallery;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_from_camera);
        initContext();
        setUpViews();
        setOnclickListeners();
    }

    private void initContext() {
        context = AddFromCameraActivity.this;
    }

    private void setUpViews() {
        ivCamera = findViewById(R.id.ivCamera);
        ivGallery = findViewById(R.id.ivGallery);
    }

    private void setOnclickListeners() {
        ivCamera.setOnClickListener(this::onStartCamera);
        ivGallery.setOnClickListener(this::onStartGallery);
    }

    private void onStartGallery(View view) {
    }

    private void onStartCamera(View view) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_PERMISSION_CODE);
        }
    }

}