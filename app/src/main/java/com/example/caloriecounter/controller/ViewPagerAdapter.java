package com.example.caloriecounter.controller;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.caloriecounter.R;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ViewPagerAdapter extends PagerAdapter {
    private final int[] onboardingLayouts;
    private final List<String> goals;
    private final List<String> activityLevel;
    private final ViewPager viewPager;

    private boolean passwordVisible;
    private boolean correctPassword;

    public ViewPagerAdapter(ViewPager viewPager, int[] onboardingLayouts) {
        this.onboardingLayouts = onboardingLayouts;
        this.viewPager = viewPager;

        String[] goalsArray = viewPager.getContext().getResources().getStringArray(R.array.goals_array);
        goals = Arrays.asList(goalsArray);

        String[] activityLevelArray = viewPager.getContext().getResources().getStringArray(R.array.activity_level_array);
        activityLevel = Arrays.asList(activityLevelArray);
    }

    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View layout = inflater.inflate(onboardingLayouts[position], container, false);

        if (layout.findViewById(R.id.rgActivityLevelInput) != null) {
            RadioGroup radioGroup = layout.findViewById(R.id.rgActivityLevelInput);
            RadioGroup.LayoutParams layoutParams;

            for (int i = 0; i < activityLevel.size(); i++) {
                RadioButton radioButton = new RadioButton(container.getContext());

                radioButton.setText(activityLevel.get(i));
                radioButton.setBackgroundResource(R.drawable.select_button);
                radioButton.setPadding(32, 32, 32, 32);
                radioButton.setButtonDrawable(R.drawable.select_button);
                radioButton.setTextSize(18);
                radioButton.setTextColor(ResourcesCompat.getColor(container.getContext().getResources(), R.color.white, container.getContext().getTheme()));
                Typeface typeface = ResourcesCompat.getFont(container.getContext(), R.font.lora_font);
                radioButton.setTypeface(typeface);

                layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT);
                layoutParams.setMargins(8, 4, 8, 4);
                radioGroup.addView(radioButton, layoutParams);
            }
        }
        if (layout.findViewById(R.id.rgFitnessGoalsInput) != null) {
            RadioGroup radioGroup = layout.findViewById(R.id.rgFitnessGoalsInput);
            RadioGroup.LayoutParams layoutParams;

            for (int i = 0; i < goals.size(); i++) {
                RadioButton radioButton = new RadioButton(container.getContext());

                radioButton.setText(goals.get(i));
                radioButton.setBackgroundResource(R.drawable.select_button);
                radioButton.setPadding(32, 32, 32, 32);
                radioButton.setButtonDrawable(R.drawable.select_button);
                radioButton.setTextSize(18);
                radioButton.setTextColor(ResourcesCompat.getColor(container.getContext().getResources(), R.color.white, container.getContext().getTheme()));
                Typeface typeface = ResourcesCompat.getFont(container.getContext(), R.font.lora_font);
                radioButton.setTypeface(typeface);

                layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT);
                layoutParams.setMargins(8, 4, 8, 4);
                radioGroup.addView(radioButton, layoutParams);
            }
        }

        Spinner spinner;
        if (layout.findViewById(R.id.spCountryInput) != null) {
            spinner = layout.findViewById(R.id.spCountryInput);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(container.getContext(), R.array.countries_array, R.layout.custom_spinner);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinner.setAdapter(adapter);
            spinner.setSelection(227, true);
        }

        if (layout.findViewById(R.id.spHeightUnit) != null) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(container.getContext(), R.array.height_units_array, R.layout.custom_list_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinner = layout.findViewById(R.id.spHeightUnit);
            spinner.setAdapter(adapter);
            spinner.setSelection(0, true);
        }

        if (layout.findViewById(R.id.spWeightUnit) != null) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(container.getContext(), R.array.weight_units_array, R.layout.custom_list_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinner = layout.findViewById(R.id.spWeightUnit);
            spinner.setAdapter(adapter);
            spinner.setSelection(0, true);
        }

        if (layout.findViewById(R.id.etDOB) != null) {
            EditText etDOB = layout.findViewById(R.id.etDOB);
            etDOB.setOnClickListener(v -> {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                DatePickerDialog picker = new DatePickerDialog(container.getContext(), (view, year1, month1, dayOfMonth) -> etDOB.setText(String.format(Locale.ENGLISH, "%d/%d/%d", dayOfMonth, month1 + 1, year1)), year, month, day);
                picker.show();
            });
        }

        if (layout.findViewById(R.id.etPassword) != null) {
            EditText password = layout.findViewById(R.id.etPassword);
            password.setOnTouchListener((v, event) -> {
                View passwordRulesGroup = layout.findViewById(R.id.grPasswordRules);
                Transition transition = new Fade();
                transition.setDuration(1000);
                transition.addTarget(passwordRulesGroup);

                TransitionManager.beginDelayedTransition((ViewGroup) passwordRulesGroup.getParent(), transition);
                passwordRulesGroup.setVisibility(View.VISIBLE);

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
            password.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    correctPassword = checkCorrectPassword(layout);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        container.addView(layout);
        return layout;
    }

    private boolean checkCorrectPassword(View layout) {
        CardView cardOne = layout.findViewById(R.id.cardOne),
                cardTwo = layout.findViewById(R.id.cardTwo),
                cardThree = layout.findViewById(R.id.cardThree),
                cardFour = layout.findViewById(R.id.cardFour);

        boolean isPasswordCorrect = true;
        EditText etPassword = layout.findViewById(R.id.etPassword);
        String password = etPassword.getText().toString();
        //check for 8 characters
        if (password.length() >= 8) {
            cardOne.setCardBackgroundColor(ResourcesCompat.getColor(viewPager.getContext().getResources(), R.color.eatwellgreenlight, viewPager.getContext().getTheme()));
        } else {
            cardOne.setCardBackgroundColor(ResourcesCompat.getColor(viewPager.getContext().getResources(), R.color.lightgray, viewPager.getContext().getTheme()));
            isPasswordCorrect = false;
        }
        //check for uppercase letter
        if (password.matches("(.*[A-Z].*)")) {
            cardTwo.setCardBackgroundColor(ResourcesCompat.getColor(viewPager.getContext().getResources(), R.color.eatwellgreenlight, viewPager.getContext().getTheme()));
        } else {
            cardTwo.setCardBackgroundColor(ResourcesCompat.getColor(viewPager.getContext().getResources(), R.color.lightgray, viewPager.getContext().getTheme()));
            isPasswordCorrect = false;
        }
        //check for at least one number
        if (password.matches("(.*[0-9].*)")) {
            cardThree.setCardBackgroundColor(ResourcesCompat.getColor(viewPager.getContext().getResources(), R.color.eatwellgreenlight, viewPager.getContext().getTheme()));
        } else {
            cardThree.setCardBackgroundColor(ResourcesCompat.getColor(viewPager.getContext().getResources(), R.color.lightgray, viewPager.getContext().getTheme()));
            isPasswordCorrect = false;
        }
        //check for symbol
        if (password.matches("^.*[^a-zA-Z0-9].*$")) {
            cardFour.setCardBackgroundColor(ResourcesCompat.getColor(viewPager.getContext().getResources(), R.color.eatwellgreenlight, viewPager.getContext().getTheme()));
        } else {
            cardFour.setCardBackgroundColor(ResourcesCompat.getColor(viewPager.getContext().getResources(), R.color.lightgray, viewPager.getContext().getTheme()));
            isPasswordCorrect = false;
        }

        return isPasswordCorrect;
    }


    @Override
    public int getCount() {
        return onboardingLayouts.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    public boolean hasCorrectPassword() {
        return correctPassword;
    }
}