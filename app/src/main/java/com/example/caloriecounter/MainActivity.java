package com.example.caloriecounter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private Button loginBtn;
    private Button signupBtn;

    private Context mContext;
    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpViews();
        setOnclickListeners();
    }

    private void setOnclickListeners() {
        loginBtn.setOnClickListener(this::onStartLoginPage);
        signupBtn.setOnClickListener(this::onStartSignUpPage);
    }

    private void setUpViews() {
        mContext = MainActivity.this;
        loginBtn = findViewById(R.id.btnLogin);
        signupBtn = findViewById(R.id.btnSignUp);
    }

    @Override
    protected void onStart(){
        super.onStart();

        FirebaseAuth authProfile = FirebaseAuth.getInstance();
        if(authProfile.getCurrentUser() != null){
            startActivity(new Intent(mContext, DashboardActivity.class) );
        }
    }

    public void onStartLoginPage(View view){
        Log.d(TAG, "Logging in..");
        Intent login = new Intent(mContext, LoginActivity.class );
        startActivity(login);
    }

    public void onStartSignUpPage(View view){
        Log.d(TAG, "Signing up..");
        Intent login = new Intent(mContext, ContinueActivity.class );
        startActivity(login);
    }

}