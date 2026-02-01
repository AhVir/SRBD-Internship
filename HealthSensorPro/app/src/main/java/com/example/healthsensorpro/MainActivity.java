package com.example.healthsensorpro;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.healthsensorpro.utils.PermissionsHelper;
import com.google.android.material.card.MaterialCardView;
import com.example.healthsensorpro.data.model.HealthMetrics;
import com.example.healthsensorpro.ui.components.MetricCardView;
import com.example.healthsensorpro.viewmodel.SensorViewModel;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private SensorViewModel viewModel;

    // UI Components
    private MetricCardView stepsCard;
    private MetricCardView caloriesCard;
    private MetricCardView distanceCard;
    private MetricCardView heartRateCard;
    private TextView activityText;
    private ImageView activityIcon;
    private Button startButton;
    private Button resetButton;
    private MaterialCardView activityCard;
    private LinearLayout sensorsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(SensorViewModel.class);

        // Initialize UI components
        initializeViews();
        setupClickListeners();
        observeViewModel();

        // Check and request permissions
        checkPermissions();
    }

    private void initializeViews() {
        stepsCard = findViewById(R.id.steps_card);
        caloriesCard = findViewById(R.id.calories_card);
        distanceCard = findViewById(R.id.distance_card);
        heartRateCard = findViewById(R.id.heartrate_card);
        activityText = findViewById(R.id.activity_text);
        activityIcon = findViewById(R.id.activity_icon);
        startButton = findViewById(R.id.start_button);
        resetButton = findViewById(R.id.reset_button);
        activityCard = findViewById(R.id.activity_card);
        sensorsContainer = findViewById(R.id.sensors_container);

        // Configure metric cards
        stepsCard.setTitle("Steps");
        stepsCard.setValue("0");
        stepsCard.setUnit("steps");
        stepsCard.setCardColor(Color.parseColor("#E3F2FD"));

        caloriesCard.setTitle("Calories");
        caloriesCard.setValue("0.0");
        caloriesCard.setUnit("kcal");
        caloriesCard.setCardColor(Color.parseColor("#FCE4EC"));

        distanceCard.setTitle("Distance");
        distanceCard.setValue("0.00");
        distanceCard.setUnit("km");
        distanceCard.setCardColor(Color.parseColor("#E8F5E9"));

        heartRateCard.setTitle("Heart Rate");
        heartRateCard.setValue("--");
        heartRateCard.setUnit("bpm");
        heartRateCard.setCardColor(Color.parseColor("#FFF3E0"));
    }

    private void setupClickListeners() {
        startButton.setOnClickListener(v -> {
            Boolean isMonitoring = viewModel.getIsMonitoring().getValue();
            if (isMonitoring != null && isMonitoring) {
                viewModel.stopMonitoring();
                startButton.setText("Start");
            } else {
                viewModel.startMonitoring();
                startButton.setText("Pause");
            }
        });

        resetButton.setOnClickListener(v -> {
            viewModel.resetData();
            Toast.makeText(this, "Data reset", Toast.LENGTH_SHORT).show();
        });
    }

    private void observeViewModel() {
        // Observe health metrics
        viewModel.getHealthMetrics().observe(this, metrics -> {
            if (metrics != null) {
                updateMetricsUI(metrics);
            }
        });

        // Observe available sensors
        viewModel.getAvailableSensors().observe(this, sensors -> {
            if (sensors != null) {
                updateSensorsUI(sensors);
            }
        });

        // Observe monitoring state
        viewModel.getIsMonitoring().observe(this, isMonitoring -> {
            if (isMonitoring != null) {
                updateMonitoringUI(isMonitoring);
            }
        });

        // Observe error messages
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                viewModel.clearError();
            }
        });
    }

    private void updateMetricsUI(HealthMetrics metrics) {
        // Update steps
        stepsCard.setValue(String.valueOf(metrics.getTotalSteps()));

        // Update calories
        caloriesCard.setValue(String.format(Locale.getDefault(), "%.1f", metrics.getTotalCalories()));

        // Update distance
        distanceCard.setValue(String.format(Locale.getDefault(), "%.2f", metrics.getTotalDistance()));

        // Update heart rate
        if (metrics.getAvgHeartRate() != null) {
            heartRateCard.setValue(String.format(Locale.getDefault(), "%.0f", metrics.getAvgHeartRate()));
        } else {
            heartRateCard.setValue("--");
        }

        // Update activity
        String activity = metrics.getCurrentActivity();
        activityText.setText(activity);

        // Update activity icon and color based on activity
        int color;
        int iconResId;

        switch (activity) {
            case "Walking":
                color = Color.parseColor("#4CAF50");
                iconResId = R.drawable.ic_walk;
                break;
            case "Running":
                color = Color.parseColor("#F44336");
                iconResId = R.drawable.ic_run;
                break;
            default:
                color = Color.parseColor("#9E9E9E");
                iconResId = R.drawable.ic_pause;
                break;
        }

        activityText.setTextColor(color);
        activityIcon.setImageResource(iconResId);
        activityIcon.setColorFilter(color);
    }

    private void updateSensorsUI(List<String> sensors) {
        sensorsContainer.removeAllViews();

        if (sensors.isEmpty()) {
            TextView noSensorsText = new TextView(this);
            noSensorsText.setText("No sensors detected");
            noSensorsText.setTextSize(16);
            noSensorsText.setTextColor(Color.parseColor("#666666"));
            noSensorsText.setPadding(0, 16, 0, 16);
            noSensorsText.setGravity(View.TEXT_ALIGNMENT_CENTER);
            sensorsContainer.addView(noSensorsText);
        } else {
            for (String sensor : sensors) {
                LinearLayout sensorRow = new LinearLayout(this);
                sensorRow.setOrientation(LinearLayout.HORIZONTAL);
                sensorRow.setPadding(0, 8, 0, 8);

                TextView sensorName = new TextView(this);
                sensorName.setText(sensor);
                sensorName.setTextSize(16);
                sensorName.setTextColor(Color.parseColor("#333333"));
                sensorName.setLayoutParams(new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

                TextView sensorStatus = new TextView(this);
                sensorStatus.setText("✓ Available");
                sensorStatus.setTextSize(14);
                sensorStatus.setTextColor(Color.parseColor("#4CAF50"));

                sensorRow.addView(sensorName);
                sensorRow.addView(sensorStatus);
                sensorsContainer.addView(sensorRow);
            }
        }
    }

    private void updateMonitoringUI(boolean isMonitoring) {
        if (isMonitoring) {
            startButton.setText("Pause");
            activityCard.setCardBackgroundColor(Color.parseColor("#E8F5E9"));
        } else {
            startButton.setText("Start");
            activityCard.setCardBackgroundColor(Color.WHITE);
        }
    }

    private void checkPermissions() {
        if (!PermissionsHelper.hasAllPermissions(this)) {
            if (PermissionsHelper.shouldShowRequestPermissionRationale(this)) {
                // Show explanation dialog
                new AlertDialog.Builder(this)
                        .setTitle("Permissions Required")
                        .setMessage("This app needs permissions to access health sensors for accurate tracking.")
                        .setPositiveButton("OK", (dialog, which) -> {
                            PermissionsHelper.requestPermissions(MainActivity.this);
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            } else {
                PermissionsHelper.requestPermissions(this);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PermissionsHelper.PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                Toast.makeText(this, "Permissions granted! Starting monitoring...",
                        Toast.LENGTH_SHORT).show();
                viewModel.startMonitoring();
            } else {
                Toast.makeText(this, "Some permissions denied. Some features may not work.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.stopMonitoring();
    }
}