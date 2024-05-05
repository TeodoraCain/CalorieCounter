package com.example.caloriecounter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.caloriecounter.models.dao.GoalData;
import com.example.caloriecounter.models.dao.UserDetails;
import com.example.caloriecounter.models.dao.WeightLog;
import com.example.caloriecounter.models.dao.WeightLogDAO;
import com.example.caloriecounter.models.dao.WeightLogDAOImpl;
import com.example.caloriecounter.models.dataHolders.GoalDataHolder;
import com.example.caloriecounter.models.dataHolders.UserDetailsHolder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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

public class WeightLogActivity extends AppCompatActivity {


    private final String TAG = "WeightLogActivity";
    private Context context;
    // views
    private TextView tvWeightDifference,
            tvCurrentWeight,
            tvStartWeight,
            tvGoalWeight,
            tvCurrentBMI;
    private ProgressBar pbWeight,
            pbBMI;
    private GraphView weightChart;
    // attributes
    private GoalData goalData;
    private UserDetails userDetails;

    private DataPoint[] dataPoints;
    private PointsGraphSeries<DataPoint> series;
    private List<WeightLog> weightLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_log);
        initContext();
        setToolbar();
        setUpViews();
        initUserData();
    }

    /********************************* INIT VIEWS **************************************************/
    private void initContext() {
        context = WeightLogActivity.this;
    }

    private void setUpViews() {
        weightChart = findViewById(R.id.weightChart);
        tvWeightDifference = findViewById(R.id.tvWeightDifference);
        tvCurrentWeight = findViewById(R.id.tvCurrentWeight);

        tvStartWeight = findViewById(R.id.tvStartWeight);
        tvGoalWeight = findViewById(R.id.tvGoalWeight);
        tvCurrentBMI = findViewById(R.id.tvCurrentBMI);
        pbWeight = findViewById(R.id.pbWeight);
        pbBMI = findViewById(R.id.pbBMI);
    }

    /********************************* INIT DATA ***************************************************/
    private void initUserData() {
        goalData = GoalDataHolder.getInstance().getData();
        userDetails = UserDetailsHolder.getInstance().getData();
        getWeightLogs();
    }

    private void setDataToUI() {
        if (userDetails == null)
            return;

        final String weightUnit;
        final String heightUnit;
        int height;
        double bmi;
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
            tvWeightDifference.setTextColor(ContextCompat.getColor(context, R.color.eatwellgreen));
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

    /********************************* HISTORY CHART ***********************************************/
    private void getWeightLogs() {
        Log.d(TAG, "Retrieving graph data");
        WeightLogDAO weightLogDAO = new WeightLogDAOImpl();
        weightLogDAO.get().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<DataPoint> dpList = new ArrayList<>();
                weightLogs = new ArrayList<>();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    WeightLog weightLog = dataSnapshot.getValue(WeightLog.class);
                    weightLogs.add(weightLog);
                    try {
                        Date date = dateFormat.parse(Objects.requireNonNull(dataSnapshot.getKey()));
                        if (date != null) {
                            dpList.add(new DataPoint(date, Objects.requireNonNull(weightLog).getWeight()));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }

                dpList.sort(Comparator.comparingDouble(DataPoint::getX));
                dataPoints = dpList.toArray(new DataPoint[0]);

                setUpWeightHistoryChart();
                setDataToUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, error.toString());
            }
        });
    }

    private void setUpWeightHistoryChart() {
        setUpPointsGraph();
        setUpLineSeries();
        formatGraphDate();
        setScrollableAndScalable();
    }

    private void setUpPointsGraph() {
        series = new PointsGraphSeries<>(dataPoints);
        series.setOnDataPointTapListener((series, dataPoint) -> {
            //Toast.makeText(WeightLogActivity.this.getApplicationContext(), "Series1: On Data Point clicked: " + dataPoint, Toast.LENGTH_SHORT).show();

            for(WeightLog weightLog: weightLogs){
                if(weightLog.getDate().equals(new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(dataPoint.getX()))){
                    Intent intent = new Intent(context, WeightLogDisplay.class);
                    intent.putExtra("WEIGHT_LOG", weightLog);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    break;
                }
            }

        });
        series.setSize(9);
        series.setColor(ContextCompat.getColor(context, R.color.pistachio));
    }

    private void setUpLineSeries() {
        LineGraphSeries<DataPoint> lineSeries = new LineGraphSeries<>(dataPoints);
        lineSeries.setColor(ContextCompat.getColor(context, R.color.eatwellgreenmediumlight));
        weightChart.addSeries(lineSeries);
        weightChart.addSeries(series);
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

    private void setScrollableAndScalable() {
        Viewport viewport = weightChart.getViewport();
        viewport.setScrollable(true);
        viewport.scrollToEnd();

        int lastIndex = dataPoints.length - 1;
        int firstIndex = Math.max(0, lastIndex - 6);

        // Set the visible range
        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(dataPoints[firstIndex].getX());
        viewport.setMaxX(dataPoints[lastIndex].getX());
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