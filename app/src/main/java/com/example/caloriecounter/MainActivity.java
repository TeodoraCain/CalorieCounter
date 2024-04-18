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

    private final String TAG = "MainActivity";
    private Button loginBtn;
    private Button signupBtn;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initContext();
        setUpViews();
        setOnclickListeners();
    }
    /********************************* INIT ACTIVITY **********************************************/
    private void initContext() {
        context = MainActivity.this;
    }
    private void setUpViews() {
        loginBtn = findViewById(R.id.btnLogin);
        signupBtn = findViewById(R.id.btnSignUp);
    }
    /********************************* SET UP LISTENERS *******************************************/
    private void setOnclickListeners() {
        loginBtn.setOnClickListener(this::onStartLoginPage);
        signupBtn.setOnClickListener(this::onStartSignUpPage);
    }
    @SuppressWarnings("unused")
    public void onStartLoginPage(View view) {
        Log.d(TAG, "Logging in..");
        Intent login = new Intent(context, LoginActivity.class);
        startActivity(login);
    }

    @SuppressWarnings("unused")
    public void onStartSignUpPage(View view) {
        Log.d(TAG, "Signing up..");
        Intent login = new Intent(context, ContinueActivity.class);
        startActivity(login);
    }

    /********************************* LIFECYCLE OVERRIDE *******************************************/
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseAuth authProfile = FirebaseAuth.getInstance();
        if (authProfile.getCurrentUser() != null) {
            startActivity(new Intent(context, DashboardActivity.class));
        }
    }

}