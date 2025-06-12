package com.example.caloriecounter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.caloriecounter.adapters.WeightLogSliderItem;
import com.example.caloriecounter.adapters.WeightLogSliderItemAdapter;
import com.example.caloriecounter.models.dao.WeightLog;
import com.example.caloriecounter.models.dataHolders.UserDetailsHolder;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WeightLogDisplay extends AppCompatActivity {

    private final String TAG = "WeightLogDisplay";
    //private Context context;
    private WeightLog weightLog;
    private ViewPager2 viewPager2;
    private TextView tvWeight;
    private TextView tvDate;
    private TextView tvNoPhotos;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_log_display);

        initActivity();
        setDataToUI();
        setUpViewPager();
    }

    /********************************* INIT ACTIVITY *****************************************************/
    private void initActivity() {
        //initContext();
        parseIntentExtras();

        if (weightLog != null) {
            setToolbar();
            initViews();
            startProgressBar();
        } else {
            finish();
        }
    }

    private void startProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void initViews() {
        tvWeight = findViewById(R.id.tvWeight);
        tvDate = findViewById(R.id.tvDate);
        tvNoPhotos = findViewById(R.id.tvNoPhotos);
        viewPager2 = findViewById(R.id.vpWeightLogSlider);
        progressBar = findViewById(R.id.progressBar);
    }

//    private void initContext() {
//       // context = WeightLogDisplay.this;
//    }

    private void parseIntentExtras() {
        Intent intent = this.getIntent();
        weightLog = intent.getParcelableExtra("WEIGHT_LOG");
    }


    private void setDataToUI() {
        tvWeight.setText(MessageFormat.format("{0} {1}", String.valueOf(weightLog.getWeight()), UserDetailsHolder.getInstance().getData().getWeightUnit()));
        tvDate.setText(weightLog.getDate());
    }

    /********************************* SET UP VIEWPAGER *****************************************************/
    private void setUpViewPager() {
        List<WeightLogSliderItem> sliderItems = new ArrayList<>();
        if (weightLog.getFrontPictureUri() != null)
            sliderItems.add(new WeightLogSliderItem(weightLog.getFrontPictureUri()));

        if (weightLog.getSidePictureUri() != null)
            sliderItems.add(new WeightLogSliderItem(weightLog.getSidePictureUri()));

        if (weightLog.getBackPictureUri() != null)
            sliderItems.add(new WeightLogSliderItem(weightLog.getBackPictureUri()));

        if (sliderItems.isEmpty()) {
            tvNoPhotos.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            Log.d(TAG, "No photos for this weight log.");
        }

        viewPager2.setAdapter(new WeightLogSliderItemAdapter(sliderItems, progressBar));
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(.85f + r * 0.15f);
            Log.d(TAG, "Photos for this weight log loaded.");
        });
        viewPager2.setPageTransformer(compositePageTransformer);
    }

    /********************************* TOOLBAR *****************************************************/
    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}