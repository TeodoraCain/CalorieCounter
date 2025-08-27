package com.example.caloriecounter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;

import com.example.caloriecounter.ml.Model1;
import com.example.caloriecounter.models.dataModel.IntentKeys;
import com.example.caloriecounter.models.dataModel.IntentResults;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AddFromCameraActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int GALLERY_PERMISSION_CODE = 102;
    private final String TAG = "AddFromCameraActivity";
    private final int imageSize = 224;

    private ImageView ivCamera, ivGallery, ivPreview;
    private LinearLayout llResult;
    private Context context;

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_from_camera);

        initContext();
        setUpViews();
        setToolbar();
        initLaunchers();
        setOnClickListeners();
    }

    private void initContext() {
        context = AddFromCameraActivity.this;
    }

    private void setUpViews() {
        ivCamera = findViewById(R.id.ivCamera);
        ivGallery = findViewById(R.id.ivGallery);
        ivPreview = findViewById(R.id.ivPreview);
        llResult = findViewById(R.id.llResult);
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setOnClickListeners() {
        ivCamera.setOnClickListener(this::onStartCamera);
        ivGallery.setOnClickListener(this::onStartGallery);
    }

    private void initLaunchers() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        if (photoUri != null) {
                            try {
                                Log.d(TAG, "Detecting food from Camera...");
                                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), photoUri);
                                Bitmap image = ImageDecoder.decodeBitmap(source);

                                int dimension = Math.min(image.getWidth(), image.getHeight());
                                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                                ivPreview.setImageBitmap(image);

                                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                                detectFoodFromImage(image);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });


        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            try {
                                Log.d(TAG, "Detecting food from Gallery...");
                                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), imageUri);
                                Bitmap image = ImageDecoder.decodeBitmap(source);

                                int dimension = Math.min(image.getWidth(), image.getHeight());
                                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                                ivPreview.setImageBitmap(image);

                                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                                detectFoodFromImage(image);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    public Bitmap convertToMutableBitmap(Bitmap sourceBitmap) {
        if (sourceBitmap.getConfig() == Bitmap.Config.HARDWARE) {
            return sourceBitmap.copy(Bitmap.Config.ARGB_8888, true);
        }
        return sourceBitmap;
    }

    @SuppressLint("SetTextI18n")
    private void detectFoodFromImage(Bitmap image) {
        try {
            image = convertToMutableBitmap(image); // Makes image mutable
            Model1 model = Model1.newInstance(context);

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, imageSize, imageSize, 3}, DataType.FLOAT32); // Model input of size 1 image x size X size on 3 color channels (RGB)
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3); // 4 bytes for float, 3 for RGB
            byteBuffer.order(ByteOrder.nativeOrder());
            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, imageSize, 0, 0, imageSize, imageSize);// written in intValues, number of pixels, starting point and dimension of extracted area

            //Extracts RGB values from each pixel and normalizes it into [0,1]
            int pixel = 0;
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++];
                    byteBuffer.putFloat(((val >> 16) & 0xFF) / 255.f);
                    byteBuffer.putFloat(((val >> 8) & 0xFF) / 255.f);
                    byteBuffer.putFloat((val & 0xFF) / 255.f);
                }
            }

            inputFeature0.loadBuffer(byteBuffer); // Passes image to the model

            // Runs model inference and gets result
            Model1.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidence = outputFeature0.getFloatArray();
            String[] classes = {
                    "Apple", "Avocado", "Baked Potato", "Banana", "Bell Pepper", "Broccoli", "Burger", "Carrot",
                    "Cauliflower", "Cheesecake", "Cherry", "Cucumber", "Eggplant", "Fries", "Garlic", "Grapes",
                    "Ice Cream", "Kiwi", "Lemon", "Mango", "Mushroom", "Omelette", "Onion", "Orange", "Pear",
                    "Pineapple", "Pizza", "Potato", "Spinach", "Strawberries", "Sweetcorn", "Tomato", "Watermelon"
            };

            // Creates pairs of prediction + confidence
            List<Pair<Integer, Float>> predList = new ArrayList<>();
            for (int i = 0; i < confidence.length; i++) {
                predList.add(new Pair<>(i, confidence[i]));
            }
            predList.sort((a, b) -> Float.compare(b.second, a.second)); // Sorting the pairs
            llResult.removeAllViews();
            for (int i = 0; i < 3 && i < predList.size(); i++) {
                int idx = predList.get(i).first;
                float conf = predList.get(i).second;

                Button btn = new Button(this);
                btn.setText(classes[idx] + " - " + String.format(Locale.ENGLISH, "%.2f", conf * 100) + "%");
                btn.setAllCaps(false);
                btn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_animation));
                btn.setTextColor(getColor(R.color.white));
                btn.setTypeface(ResourcesCompat.getFont(this, R.font.lora_font), Typeface.ITALIC);
                btn.setTextSize(18);
                btn.setPadding(16, 16, 16, 16);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                int marginInPx = (int) (8 * getResources().getDisplayMetrics().density + 0.5f);
                params.setMargins(0, 0, 0, marginInPx);

                btn.setLayoutParams(params);

                btn.setOnClickListener(v -> {
                    Intent intent = new Intent();
                    intent.putExtra(IntentKeys.FOOD_NAME, classes[idx]);
                    setResult(IntentResults.RESULT_GET_FROM_CAMERA, intent);
                    finish();
                });

                llResult.addView(btn);
            }


            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Eroare la procesarea imaginii pentru clasificare", e);

            Toast.makeText(context, "A apărut o eroare la detectarea imaginii. Încearcă din nou.", Toast.LENGTH_LONG).show();

        }

    }

    private void onStartCamera(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            openCamera();
        }
    }

    private void onStartGallery(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES}, GALLERY_PERMISSION_CODE);
            } else {
                openGallery();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERMISSION_CODE);
            } else {
                openGallery();
            }
        }
    }

    private void openCamera() {
        File photoFile;
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "JPEG_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            photoFile = File.createTempFile(fileName, ".jpg", storageDir);
            photoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
        } catch (IOException ex) {
            Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        cameraLauncher.launch(intent);
    }


    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(galleryIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == GALLERY_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Gallery permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}