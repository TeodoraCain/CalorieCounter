package com.example.caloriecounter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.caloriecounter.model.DAO.GoalData;
import com.example.caloriecounter.model.DAO.UserDetails;
import com.example.caloriecounter.model.DAO.WeightLog;
import com.example.caloriecounter.model.DAO.WeightLogDAO;
import com.example.caloriecounter.model.DAO.WeightLogDAOImpl;
import com.example.caloriecounter.model.dataHolder.GoalDataHolder;
import com.example.caloriecounter.model.dataHolder.UserDetailsHolder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class WeightLogActivity extends AppCompatActivity {


    private final String TAG = "WeightLogActivity";
    private Context mContext;

    private PointsGraphSeries<DataPoint> series;
    private LineGraphSeries<DataPoint> lineSeries;
    private GraphView weightChart;
    private GoalData goalData;
    private WeightLog weightLog;
    private UserDetails userDetails;
    private List<String> dates;
    private List<WeightLog> weightLogs;

    private TextView tvWeightDifference,
            tvCurrentWeight,
            tvStartWeight,
            tvGoalWeight,
            tvCurrentBMI;
    private ProgressBar pbWeight,
            pbBMI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_log);
        setToolbar();
        setUpViews();
        setUpUserData();
    }

    private void setDataToUI() {
        if (userDetails == null)
            return;

        final String weightUnit;
        final String heightUnit;
        int height;
        double bmi;
        DataPoint[] dataPoints = getDataPoint();
        double startWeight, goalWeight, weight;

        weightUnit = userDetails.getWeightUnit();
        weight = Double.parseDouble(userDetails.getWeight());
        heightUnit = userDetails.getHeightUnit();
        height = Integer.parseInt(userDetails.getHeight());

        tvCurrentWeight.setText(MessageFormat.format("Now {0} {1}", weight, weightUnit));

        startWeight = dataPoints[0].getY();
        tvStartWeight.setText(MessageFormat.format("Start: {0} {1}", startWeight, weightUnit));

        if (goalData != null) {
            goalWeight = Double.parseDouble(goalData.getWeightGoal());
            tvGoalWeight.setText(MessageFormat.format("Goal: {0} {1}", goalWeight, weightUnit));
            pbWeight.setMax((int) Math.abs(goalWeight - startWeight));
            pbWeight.setProgress((int) Math.abs(weight - startWeight));
        }
        if (weight > startWeight) {
            tvWeightDifference.setText(MessageFormat.format("+{0} {1}", weight - startWeight, weightUnit));
            tvWeightDifference.setTextColor(Color.RED);
            pbWeight.setProgress(0);
        } else if (weight < startWeight) {
            tvWeightDifference.setTextColor(ContextCompat.getColor(mContext, R.color.eatwellgreen));
            tvWeightDifference.setText(MessageFormat.format("-{0} {1}", startWeight - weight, weightUnit));
        }

        //bmi
        bmi = calculateBMI(height, weight, heightUnit, weightUnit);
        String formattedBMI = String.format(Locale.ENGLISH, "%.1f", bmi);
        tvCurrentBMI.setText(MessageFormat.format("Now {0}", formattedBMI));
        pbBMI.setMax(40);
        pbBMI.setProgress((int) bmi);
    }

    private double calculateBMI(int height, double weight, String heightUnit, String weightUnit) {
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

    private void setUpWeightHistoryChart() {
        setUpPointsGraph();
        setUpLineSeries();
        formatGraphDate();
    }

    private void formatGraphDate() {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.ENGLISH);

        GridLabelRenderer gridLabelRenderer = weightChart.getGridLabelRenderer();
        gridLabelRenderer.setGridStyle(GridLabelRenderer.GridStyle.BOTH);
        gridLabelRenderer.setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    return dateFormat.format(new Date((long) value));
                }
                return super.formatLabel(value, isValueX);
            }
        });
    }

    private void setUpPointsGraph() {
        series = new PointsGraphSeries<>(getDataPoint());
        series.setOnDataPointTapListener((series, dataPoint) -> Toast.makeText(WeightLogActivity.this.getApplicationContext(), "Series1: On Data Point clicked: " + dataPoint, Toast.LENGTH_SHORT).show());
        series.setSize(9);
        series.setColor(ContextCompat.getColor(mContext, R.color.pistachio));
    }

    private void setUpLineSeries() {
        lineSeries = new LineGraphSeries<>(getDataPoint());
        lineSeries.setColor(ContextCompat.getColor(mContext, R.color.eatwellgreenmediumlight));
        weightChart.addSeries(lineSeries);
        weightChart.addSeries(series);
    }

    private DataPoint[] getDataPoint() {
        DataPoint[] dp = new DataPoint[weightLogs.size()];
        int i = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        for (WeightLog weightLog : weightLogs) {
            try {
                Date date = dateFormat.parse(weightLog.getDate());
                dp[i++] = new DataPoint(date, weightLog.getWeight());
            } catch (ParseException e) {
                // Handle parsing errors
                e.printStackTrace();
            }
        }
        return dp;
    }

    private void setUpUserData() {
        goalData = GoalDataHolder.getInstance().getData();
        userDetails = UserDetailsHolder.getInstance().getData();
        getWeightLogs();
    }

    private void getWeightLogs() {
        WeightLogDAO weightLogDAO = new WeightLogDAOImpl();
        weightLogDAO.get().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> keys = new ArrayList<>();
                weightLogs = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    WeightLog weightLog = dataSnapshot.getValue(WeightLog.class);
                    weightLogs.add(weightLog);
                    keys.add(dataSnapshot.getKey());
                }
                dates = keys;
                setUpWeightHistoryChart();
                setDataToUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setUpViews() {
        mContext = WeightLogActivity.this;
        weightChart = findViewById(R.id.weightChart);
        tvWeightDifference = findViewById(R.id.tvWeightDifference);
        tvCurrentWeight = findViewById(R.id.tvCurrentWeight);

        tvStartWeight = findViewById(R.id.tvStartWeight);
        tvGoalWeight = findViewById(R.id.tvGoalWeight);
        tvCurrentBMI = findViewById(R.id.tvCurrentBMI);
        pbWeight = findViewById(R.id.pbWeight);
        pbBMI = findViewById(R.id.pbBMI);
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("Weight Log");
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//
//        ImageView saveImageView = findViewById(R.id.ivSave);
//
//        saveImageView.setOnClickListener(v -> save());
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