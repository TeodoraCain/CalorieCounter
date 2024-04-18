package com.example.caloriecounter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.caloriecounter.models.dao.DailyData;
import com.example.caloriecounter.models.dao.GoalData;
import com.example.caloriecounter.models.dataHolders.DailyDataHolder;
import com.example.caloriecounter.models.dataHolders.GoalDataHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class StepLogActivity extends AppCompatActivity {

    private final String TAG = "StepLogActivity";
    private Context context;
    //views
    private TextView tvCurrentSteps;
    private TextView tvGoalSteps;
    private TextView tvSummarySteps;
    private TextView tvSummaryDistance;
    private TextView tvSummaryCalories;
    private ProgressBar pbSteps;
    private GraphView stepChart;
    // attributes
    private DailyData dailyData;
    private GoalData goalData;

    private DataPoint[] dataPoints;
    private PointsGraphSeries<DataPoint> series;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_log);
        initContext();
        setToolbar();
        setUpViews();
        initUserData();
    }

    /********************************* INIT VIEWS **************************************************/
    private void setUpViews() {
        tvCurrentSteps = findViewById(R.id.tvCurrentSteps);
        tvGoalSteps = findViewById(R.id.tvGoalSteps);
        tvSummarySteps = findViewById(R.id.tvSummarySteps);
        tvSummaryDistance = findViewById(R.id.tvSummaryDistance);
        tvSummaryCalories = findViewById(R.id.tvSummaryCalories);
        pbSteps = findViewById(R.id.pbSteps);
        stepChart = findViewById(R.id.stepChart);
    }

    private void initContext() {
        context = StepLogActivity.this;
    }

    /********************************* INIT DATA **************************************************/
    private void initUserData() {
        dailyData = DailyDataHolder.getInstance().getData();
        goalData = GoalDataHolder.getInstance().getData();
        getDailyDataLogs();
    }

    private void getDailyDataLogs() {
        Log.d(TAG, "Retrieving graph data");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        DatabaseReference dailyDataDatabaseReference = null;
        if (firebaseUser != null) {
            dailyDataDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid()).child("daily_data");
        }
        if (dailyDataDatabaseReference != null) {
            dailyDataDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    List<DataPoint> dpList = new ArrayList<>();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        DailyData dailyDataLog = dataSnapshot.getValue(DailyData.class);
                        try {
                            Date date = dateFormat.parse(Objects.requireNonNull(dataSnapshot.getKey()));
                            if (date != null) {
                                dpList.add(new DataPoint(date, Objects.requireNonNull(dailyDataLog).getSteps()));
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    dpList.sort(Comparator.comparingDouble(DataPoint::getX));

                    // Convert the sorted list back to an array
                    dataPoints = dpList.toArray(new DataPoint[0]);
                    //Log.d(TAG, dailyDataLogs.size() + "");

                    for (DataPoint dataPoint : dataPoints) {
                        Log.d(TAG, dataPoint.getX() + " " + dataPoint.getY());
                    }
                    setUpWeightHistoryChart();
                    setDataToUI();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void setDataToUI() {
        if (dailyData == null) return;

        int stepGoal = 6000;
        if (goalData != null) {
            stepGoal = Integer.parseInt(goalData.getStepGoal());
        }
        pbSteps.setMax(stepGoal);
        tvGoalSteps.setText(MessageFormat.format("Goal: {0} steps", stepGoal));

        int steps = dailyData.getSteps() - 1;
        pbSteps.setProgress(steps);
        tvCurrentSteps.setText(MessageFormat.format("Now {0} steps", steps));
        tvSummarySteps.setText(MessageFormat.format("{0} steps", steps));
        tvSummaryDistance.setText(MessageFormat.format("{0} km", stepsToKilometers(steps)));
        tvSummaryCalories.setText(MessageFormat.format("{0} kcal", calculateCaloriesBurned(steps)));
    }

    public double stepsToKilometers(int steps) {
        double averageStrideLengthMeters = 0.762;
        double totalDistanceMeters = steps * averageStrideLengthMeters;
        return totalDistanceMeters / 1000;
    }

    public int calculateCaloriesBurned(int steps) {
        return (int) (steps * 0.04);
    }

    /********************************* HISTORY CHART ***********************************************/
    private void setUpWeightHistoryChart() {
        setUpPointsGraph();
        setUpLineSeries();
        formatGraphDate();
        setScrollableAndScalable();
    }

    private void setScrollableAndScalable() {
        Viewport viewport = stepChart.getViewport();
        viewport.setScrollable(true);
        viewport.scrollToEnd();

        int lastIndex = dataPoints.length - 1;
        int firstIndex = Math.max(0, lastIndex - 6);

        // Set the visible range
        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(dataPoints[firstIndex].getX());
        viewport.setMaxX(dataPoints[lastIndex].getX());
    }

    private void formatGraphDate() {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.ENGLISH);

        GridLabelRenderer gridLabelRenderer = stepChart.getGridLabelRenderer();
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
        series = new PointsGraphSeries<>(dataPoints);
        series.setOnDataPointTapListener((series, dataPoint) -> Toast.makeText(context, "Series1: On Data Point clicked: " + dataPoint, Toast.LENGTH_SHORT).show());
        series.setSize(9);
        series.setColor(ContextCompat.getColor(context, R.color.pistachio));
    }

    private void setUpLineSeries() {
        LineGraphSeries<DataPoint> lineSeries = new LineGraphSeries<>(dataPoints);
        lineSeries.setColor(ContextCompat.getColor(context, R.color.eatwellgreenmediumlight));
        stepChart.addSeries(series);
        stepChart.addSeries(lineSeries);
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