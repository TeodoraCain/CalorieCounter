package com.example.caloriecounter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private boolean passwordVisible;
    EditText password;
    EditText email;
    Button loginButton;
    private FirebaseAuth authProfile;

    private Context mContext;
    private final String TAG = "LoginActivity";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setUpViews();
        passwordVisibilityButtonSet();
        setUpFirebase();
        startLogin();
    }

    private void setUpFirebase() {
        authProfile = FirebaseAuth.getInstance();
    }

    private void setUpViews() {
        mContext = LoginActivity.this;
        email = findViewById(R.id.etEmailLogin);
        password = findViewById(R.id.etPasswordLogin);
        loginButton = findViewById(R.id.btnLogin);
    }

    void startLogin() {
        loginButton.setOnClickListener(v ->{
                    if(loginCheck())
                        loginWithEmailAndPassword(email.getText().toString(), password.getText().toString());
                }

        );
    }

    boolean loginCheck() {
        String emailTxt = email.getText().toString();
        String passwordTxt = password.getText().toString();

        if (emailTxt.isEmpty() ||emailTxt == null) {
            //Toast.makeText(LoginActivity.this, "Please enter email!", Toast.LENGTH_SHORT).show();
            email.setError("Please enter email!");
            email.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches()) {
            //Toast.makeText(LoginActivity.this, "Email is incorrect!", Toast.LENGTH_SHORT).show();
            email.setError("Email is incorrect!");
            email.requestFocus();
            return false;
        } else if (passwordTxt.isEmpty() ||passwordTxt == null) {
            //Toast.makeText(LoginActivity.this, "Please enter password!", Toast.LENGTH_SHORT).show();
            password.setError("Please enter password!");
            password.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    void loginWithEmailAndPassword(String emailTxt, String passwordTxt) {
        authProfile.signInWithEmailAndPassword(emailTxt, passwordTxt).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Logging in user..");
                Toast.makeText(mContext, "Welcome", Toast.LENGTH_SHORT).show();
                Intent login = new Intent(mContext, SplashActivity.class);
                startActivity(login);
            } else {
                try {
                    throw Objects.requireNonNull(task.getException());
                } catch (FirebaseAuthInvalidUserException e) {
                    email.setError("Invalid user. Please try again.");
                    email.requestFocus();
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    email.setError("Invalid credentials. Please try again.");
                    email.requestFocus();
                } catch (Exception e) {
                    Log.e("LoginActivity", e.getMessage());
                    Toast.makeText(mContext, "Something went wrong. Try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void passwordVisibilityButtonSet() {
        password.setOnTouchListener((v, event) -> {
            final int right = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= password.getRight() - password.getCompoundDrawables()[right].getBounds().width()) {
                    int selection = password.getSelectionEnd();
                    if (passwordVisible) {
                        password.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_password, 0, R.drawable.ic_visibility_off, 0);
                        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        passwordVisible = false;
                    } else {
                        password.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_password, 0, R.drawable.ic_visibility, 0);
                        password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        passwordVisible = true;
                    }
                    password.setSelection(selection);
                    return true;
                }
            }
            return false;
        });
    }

    public void onStartDashboardPage(View view) {
        Intent login = new Intent(mContext, DashboardActivity.class);
        startActivity(login);
    }
}