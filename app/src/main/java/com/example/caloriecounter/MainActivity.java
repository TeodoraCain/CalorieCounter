package com.example.caloriecounter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button loginBtn = findViewById(R.id.btnLogin);
        Button signupBtn = findViewById(R.id.btnSignUp);

        loginBtn.setOnClickListener(this::onStartLoginPage);
        signupBtn.setOnClickListener(this::onStartSignUpPage);
    }

    @Override
    protected void onStart(){
        super.onStart();

        FirebaseAuth authProfile = FirebaseAuth.getInstance();
        if(authProfile.getCurrentUser() != null){
            startActivity(new Intent(MainActivity.this, DashboardActivity.class) );
        }
    }

    public void onStartLoginPage(View view){
        Intent login = new Intent(this, LoginActivity.class );
        startActivity(login);
    }

    public void onStartSignUpPage(View view){
        Intent login = new Intent(this, ContinueActivity.class );
        startActivity(login);
    }

}