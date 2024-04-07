package com.example.caloriecounter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.caloriecounter.model.DAO.GoalDAOImpl;
import com.example.caloriecounter.model.DAO.GoalData;
import com.example.caloriecounter.model.DAO.GoalDataDAO;
import com.example.caloriecounter.model.DAO.UserDAO;
import com.example.caloriecounter.model.DAO.UserDAOImpl;
import com.example.caloriecounter.model.DAO.UserDetails;
import com.example.caloriecounter.controllers.ViewPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.HashMap;
import java.util.Objects;

//
public class RegisterActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private final int[] layouts = {R.layout.view_pager_1, R.layout.view_pager_2, R.layout.view_pager_3, R.layout.view_pager_4, R.layout.view_pager_5};
    private HashMap<String, String> userInputs;
    private ViewPager viewPager;
    private ProgressBar progressBar;

    private TextView tvNext;
    private ViewPagerAdapter adapter;

    private Context mContext;
    private final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setUpViews();
        initializeUserInputs();
        setUpViewPager();
        setUpDotsIndicator();
        setUpViewpagerNavigation(adapter);
    }

    private void initializeUserInputs() {
        userInputs = new HashMap<>();
    }

    private void setUpViewPager() {
        viewPager = findViewById(R.id.viewPager);
        adapter = new ViewPagerAdapter(viewPager, layouts);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
    }

    private void setUpDotsIndicator() {
        DotsIndicator dotsIndicator = findViewById(R.id.dots_indicator);
        //noinspection deprecation
        dotsIndicator.setViewPager(viewPager);
    }

    private void setUpViewpagerNavigation(ViewPagerAdapter adapter) {
        tvNext.setOnClickListener(v -> {
            int nextItem = viewPager.getCurrentItem() + 1;
            if (nextItem < adapter.getCount()) {
                viewPager.setCurrentItem(nextItem);
            } else {
                if (checkRegistrationStepsCompleted()) {
                    if (progressBar != null) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    registerUser();
                }
            }
        });
    }

    private void setUpViews() {
        mContext = RegisterActivity.this;
        tvNext = findViewById(R.id.tvNext);
        progressBar = findViewById(R.id.pbRegisterProgress);
    }

    private void registerUser() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(Objects.requireNonNull(userInputs.get(getString(R.string.email))), Objects.requireNonNull(userInputs.get(getString(R.string.password)))).addOnCompleteListener(RegisterActivity.this, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(mContext, "Registration successful!", Toast.LENGTH_SHORT).show();
                FirebaseUser firebaseUser = auth.getCurrentUser();

                //set the name of the user
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(userInputs.get(getString(R.string.name)))
                        .build();

                assert firebaseUser != null;
                firebaseUser.updateProfile(profileUpdates);

                UserDetails writeUserDetails = new UserDetails(userInputs, String.valueOf(calculateBMI()));
                UserDAO userDao = new UserDAOImpl();
                Log.d(TAG, "Attempting to register..");
                userDao.add(writeUserDetails).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        //Send verification email
                        Objects.requireNonNull(firebaseUser).sendEmailVerification();
                        GoalDataDAO goalDataDAO = new GoalDAOImpl();
                        GoalData goalData = new GoalData();
                        calculateGoals(goalData);
                        goalData.setWeightGoal(writeUserDetails.getWeight());

                        goalDataDAO.add(goalData);
                        Toast.makeText(mContext, "User registered successfully. Please verify your email", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Registration successful..");
                        Intent intent = new Intent(mContext, SplashActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        //finish();
                    } else {
                        Log.d(TAG, "Registration was unsuccessful..");
                        Toast.makeText(mContext, "Registration failed! Please try again", Toast.LENGTH_SHORT).show();
                    }

                });

            } else {
                try {
                    throw Objects.requireNonNull(task.getException());
                } catch (FirebaseAuthUserCollisionException e) {
                    alertIncomplete("Email already in use", 4);
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    alertIncomplete("Your email is invalid", 4);
                } catch (Exception e) {
                    Log.e("Registration", e.getMessage());
                    Toast.makeText(mContext, "Registration incomplete!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private double calculateBMI() {
        int height =  Integer.parseInt(Objects.requireNonNull(userInputs.get(getString(R.string.height))));
        double weight = Double.parseDouble(Objects.requireNonNull(userInputs.get(getString(R.string.weight))));
        String heightUnit = userInputs.get(getString(R.string.heightUnit));
        String weightUnit = userInputs.get(getString(R.string.weightUnit));

        if (Objects.equals(heightUnit, "cm") && Objects.equals(weightUnit, "kg")) {
            double heightMeters = height / 100.0;
            return weight / (heightMeters * heightMeters);
        }
        if (Objects.equals(heightUnit, "in") && Objects.equals(weightUnit, "lbs")) {
            double heightMeters = height * 0.0254;
            return (weight / (heightMeters * heightMeters)) * 703;
        }
        return 0;
    }

    private void calculateGoals(GoalData goalData) {
        if (Objects.equals(userInputs.get(getString(R.string.gender)), getString(R.string.female_text))) {
            goalData.setCalorieGoal("2000");
        } else {
            goalData.setCalorieGoal("2500");
        }
        String RECOMMENDED_STEP_GOAL = "6000";
        goalData.setStepGoal(RECOMMENDED_STEP_GOAL);
        String RECOMMENDED_WATER_GOAL = "2000";
        goalData.setWaterIntakeGoal(RECOMMENDED_WATER_GOAL);
        String RECOMMENDED_EXERCISE_TIME_GOAL = "30";
        goalData.setExerciseTimeGoal(RECOMMENDED_EXERCISE_TIME_GOAL);
    }

    private boolean checkRegistrationStepsCompleted() {
        String fitnessGoal = userInputs.get(getString(R.string.fitnessGoal));
        String activityLevel = userInputs.get(getString(R.string.activityLevel));
        String gender = userInputs.get(getString(R.string.gender));
        String height = userInputs.get(getString(R.string.height));
        String weight = userInputs.get(getString(R.string.weight));
        String heightUnit = userInputs.get(getString(R.string.heightUnit));
        String weightUnit = userInputs.get(getString(R.string.weightUnit));
        String dob = userInputs.get(getString(R.string.dob));
        String country = userInputs.get(getString(R.string.country));
        String name = userInputs.get(getString(R.string.name));
        String email = userInputs.get(getString(R.string.email));
        String password = userInputs.get(getString(R.string.password));

        if (fitnessGoal == null) {
            alertIncomplete("Please go back and select a fitness goal.", 0);
            return false;
        }
        if (activityLevel == null) {
            alertIncomplete("Please go back and select an activity level.", 1);
            return false;
        }
        if (height == null || height.isEmpty()) {
            alertIncomplete("Please go back and input your height.", 2);
            return false;
        }
        if (heightUnit == null) {
            String[] heightUnits = getResources().getStringArray(R.array.height_units_array);
            userInputs.put(getString(R.string.heightUnit), heightUnits[0]);
        }
        if (weight == null || weight.isEmpty()) {
            alertIncomplete("Please go back and input your weight.", 2);
            return false;
        }
        if (weightUnit == null) {
            String[] weightUnits = getResources().getStringArray(R.array.weight_units_array);
            userInputs.put(getString(R.string.weightUnit), weightUnits[0]);
        }
        if (gender == null) {
            alertIncomplete("Please go back and select your gender", 3);
            return false;
        }
        if (dob == null || dob.isEmpty()) {
            alertIncomplete("Please go back and input your date of birth.", 3);
            return false;
        }
        if (country == null) {
            String[] countries = getResources().getStringArray(R.array.countries_array);
            userInputs.put(getString(R.string.country), countries[227]);
        }
        if (name == null || name.isEmpty()) {
            alertIncomplete("Please input your name.", 4);
            return false;
        }
        if (email == null || email.isEmpty()) {
            alertIncomplete("Please input your email.", 4);
            return false;
        }
        if (password == null || password.isEmpty()) {
            alertIncomplete("Please input your password.", 4);
            return false;
        }

        ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();
        if (adapter != null && !adapter.hasCorrectPassword()) {
            alertIncomplete("Please input a correct password.", 4);
            return false;
        }

        return true;
    }

    private void alertIncomplete(String message, int viewPagerItem) {
        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle("Customization incomplete");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", (dialog, which) -> {
            viewPager.setCurrentItem(viewPagerItem);
            dialog.dismiss();
        });
        alertDialog.show();
    }

//    private int[] getOnboardingLayouts() {
//        return new int[]{R.layout.view_pager_1, R.layout.view_pager_2, R.layout.view_pager_3, R.layout.view_pager_4, R.layout.view_pager_5};
//    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        TextView tvNext = findViewById(R.id.tvNext);
        if (position == layouts.length - 1) {
            tvNext.setText(R.string.finish_text);
        } else {
            tvNext.setText(R.string.next);
        }

        // save all inputs
        saveInput(R.id.rgFitnessGoalsInput, getString(R.string.fitnessGoal));
        saveInput(R.id.rgActivityLevelInput, getString(R.string.activityLevel));
        saveInput(R.id.rgGenderInput, getString(R.string.gender));
        saveInput(R.id.etHeightInput, getString(R.string.height));
        saveInput(R.id.etWeightInput, getString(R.string.weight));
        saveInput(R.id.spHeightUnit, getString(R.string.heightUnit));
        saveInput(R.id.spWeightUnit, getString(R.string.weightUnit));
        saveInput(R.id.etDOB, getString(R.string.dob));
        saveInput(R.id.spCountryInput, getString(R.string.country));
        saveInput(R.id.etName, getString(R.string.name));
        saveInput(R.id.etEmail, getString(R.string.email));
        saveInput(R.id.etPassword, getString(R.string.password));
        saveInput(R.id.etName, getString(R.string.name));
    }

    private void saveInput(int id, String inputKey) {
        View view = findViewById(id);

        if (view == null) {
            return; // Exit the method if the view is not found
        }

        if (view instanceof EditText) {
            // Handle EditText input
            EditText editText = (EditText) view;
            String currentValue = editText.getText().toString();

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String currentValue = editText.getText().toString();
                    String savedValue = userInputs.get(inputKey);
                    if (savedValue == null || !savedValue.equals(currentValue)) {
                        userInputs.put(inputKey, currentValue);
                    }
                }
            });

            String savedValue = userInputs.get(inputKey);
            if (!currentValue.equals(savedValue)) {
                editText.setText(savedValue);
            }


        } else if (view instanceof RadioGroup) {
            // Handle RadioGroup input
            RadioGroup radioGroup = (RadioGroup) view;
            int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
            String storedValue = userInputs.get(inputKey);

            if (checkedRadioButtonId == -1 && storedValue != null) {
                // Remember the user choice
                for (int i = 0; i < radioGroup.getChildCount(); i++) {
                    RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
                    if (radioButton.getText().toString().equals(storedValue)) {
                        radioButton.setChecked(true);
                        break;
                    }
                }
            } else if (checkedRadioButtonId != -1) {
                // update inputs if a new value is found
                RadioButton checkedRadioButton = radioGroup.findViewById(checkedRadioButtonId);
                userInputs.put(inputKey, checkedRadioButton.getText().toString());
            }
        } else if (view instanceof Spinner) {
            // Handle Spinner input
            Spinner spinner = (Spinner) view;
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedValue = spinner.getSelectedItem().toString();
                    String savedValue = userInputs.get(inputKey);
                    if (savedValue == null || !savedValue.equals(selectedValue)) {
                        userInputs.put(inputKey, selectedValue);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            String savedValue = userInputs.get(inputKey);

            for (int i = 0; i < spinner.getCount(); i++) {
                if (spinner.getItemAtPosition(i).toString().equals(savedValue)) {
                    spinner.setSelection(i, true);
                }
            }
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
