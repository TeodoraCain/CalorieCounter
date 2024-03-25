package com.example.caloriecounter;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class LanguageActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{

    RadioGroup radioGroup;
    ImageView ivFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        radioGroup = findViewById(R.id.rgChooseLanguage);
        radioGroup.setOnCheckedChangeListener(this);

        ivFlag = findViewById(R.id.ivFlag);

        String languageCode = getResources().getConfiguration().locale.getLanguage();
        int imageResId;
        switch (languageCode) {

            case "fr":
                imageResId = R.drawable.france;
                break;
            case "es":
                imageResId = R.drawable.spain;
                break;
            case "ro":
                imageResId = R.drawable.romania;
                break;
            default:
                imageResId = R.drawable.us;
                break;
        }
        ivFlag.setImageResource(imageResId);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rbFrench:
                setLocale("fr");
                break;
            case R.id.rbEnglish:
                setLocale("en");
                break;
            case R.id.rbSpanish:
                setLocale("es");
                break;
            case R.id.rbRomanian:
                setLocale("ro");
                break;
        }
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        finish();
        startActivity(getIntent());
    }
}