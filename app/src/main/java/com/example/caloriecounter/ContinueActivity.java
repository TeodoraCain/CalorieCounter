package com.example.caloriecounter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ContinueActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Button continueBtn = findViewById(R.id.btnContinue);
        continueBtn.setOnClickListener(this::onContinueToSignUp);

    }

    public void onContinueToSignUp(View view){
        Intent signUp = new Intent(this, RegisterActivity.class );
        startActivity(signUp);
    }
}