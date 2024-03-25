package com.example.caloriecounter;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class SuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(com.airbnb.lottie.R.style.AlertDialog_AppCompat_Light);
        setContentView(R.layout.activity_success);

        LottieAnimationView animSuccess = findViewById(R.id.animSuccess);
        animSuccess.setVisibility(View.VISIBLE);
        animSuccess.playAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);


    }
}