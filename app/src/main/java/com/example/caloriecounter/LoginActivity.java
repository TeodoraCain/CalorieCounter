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

    private final String TAG = "LoginActivity";
    EditText password;
    EditText email;
    Button loginButton;
    private Context context;
    private FirebaseAuth authProfile;
    private boolean passwordVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initContext();
        setUpFirebase();
        setUpViews();
        setUpListeners();
    }

    @SuppressWarnings("unused")
    public void onStartDashboardPage(View view) {
        Intent login = new Intent(context, DashboardActivity.class);
        startActivity(login);
    }

    /********************************* INIT ACTIVITY **********************************************/
    private void initContext() {
        context = LoginActivity.this;
    }
    private void setUpFirebase() {
        authProfile = FirebaseAuth.getInstance();
    }

    private void setUpViews() {
        email = findViewById(R.id.etEmailLogin);
        password = findViewById(R.id.etPasswordLogin);
        loginButton = findViewById(R.id.btnLogin);
    }

    /********************************* SET UP LISTENERS *******************************************/

    private void setUpListeners() {
        setUpPassVisibilityListener();
        setUpLoginListener();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpPassVisibilityListener() {
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

    void setUpLoginListener() {
        loginButton.setOnClickListener(v -> {
                    if (isLoginInputCorrect())
                        loginWithEmailAndPassword(email.getText().toString(), password.getText().toString());
                }
        );
    }

    boolean isLoginInputCorrect() {
        String emailTxt = email.getText().toString();
        String passwordTxt = password.getText().toString();

        if (emailTxt.isEmpty()) {
            //Toast.makeText(LoginActivity.this, "Please enter email!", Toast.LENGTH_SHORT).show();
            email.setError("Please enter email!");
            email.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches()) {
            //Toast.makeText(LoginActivity.this, "Email is incorrect!", Toast.LENGTH_SHORT).show();
            email.setError("Email is incorrect!");
            email.requestFocus();
            return false;
        } else if (passwordTxt.isEmpty()) {
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
                Toast.makeText(context, "Welcome", Toast.LENGTH_SHORT).show();
                Intent login = new Intent(context, SplashActivity.class);
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
                    Toast.makeText(context, "Something went wrong. Try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}