package com.example.healthsensorpro.data.repository;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import com.example.healthsensorpro.data.model.HealthMetrics;
import com.example.healthsensorpro.data.model.SensorData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SensorRepository implements SensorEventListener {
    private Context context;
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private Sensor accelerometerSensor;
    private Sensor heartRateSensor;

    private int stepCount = 0;
    private int lastStepCount = 0;
    private int initialSteps = 0;
    private boolean isInitialized = false;
    private boolean useSimulatedSteps = true; // For emulator/device without step counter

    private long lastAccelerometerReading = 0L;
    private List<String> activityBuffer = new ArrayList<>();
    private Handler handler = new Handler(Looper.getMainLooper());
    private Random random = new Random();

    // For calculations
    private final double CALORIES_PER_STEP = 0.04;
    private final double STEP_LENGTH = 0.762;
    private float[] lastAccelerometerValues = new float[3];

    // Activity tracking
    private String currentDetectedActivity = "Stationary";
    private long lastActivityChangeTime = 0;
    private float lastMagnitude = 0;

    // Step simulation
    private Runnable stepSimulator = new Runnable() {
        @Override
        public void run() {
            if (useSimulatedSteps && !currentDetectedActivity.equals("Stationary")) {
                // Simulate steps based on activity
                int stepsToAdd = 0;

                switch (currentDetectedActivity) {
                    case "Walking":
                        stepsToAdd = 1 + random.nextInt(2); // 1-2 steps per second
                        break;
                    case "Running":
                        stepsToAdd = 2 + random.nextInt(3); // 2-4 steps per second
                        break;
                }

                stepCount += stepsToAdd;

                // Update UI
                if (stepCount > lastStepCount) {
                    lastStepCount = stepCount;
                }
            }

            // Schedule next update
            long interval = 1000; // 1 second
            handler.postDelayed(this, interval);
        }
    };

    public SensorRepository(Context context) {
        this.context = context;
        initializeSensors();
        startStepSimulation();
    }

    private void initializeSensors() {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

            // Check if we have a real step counter
            if (stepCounterSensor == null) {
                useSimulatedSteps = true;
            } else {
                useSimulatedSteps = false;
            }

            // Register listeners
            if (accelerometerSensor != null) {
                sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }

            if (stepCounterSensor != null && !useSimulatedSteps) {
                sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
            }

            if (heartRateSensor != null) {
                sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
    }

    private void startStepSimulation() {
        // Start step simulation only if using simulated steps
        if (useSimulatedSteps) {
            handler.postDelayed(stepSimulator, 1000);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_STEP_COUNTER:
                handleStepCounter(event);
                break;
            case Sensor.TYPE_ACCELEROMETER:
                handleAccelerometer(event);
                break;
            case Sensor.TYPE_HEART_RATE:
                // Handle heart rate data
                break;
        }
    }

    private void handleStepCounter(SensorEvent event) {
        if (!isInitialized) {
            initialSteps = (int) event.values[0];
            isInitialized = true;
        }

        int currentSteps = (int) event.values[0];
        stepCount = currentSteps - initialSteps;

        if (stepCount > lastStepCount) {
            lastStepCount = stepCount;
        }
    }

    private void handleAccelerometer(SensorEvent event) {
        lastAccelerometerReading = System.currentTimeMillis();
        lastAccelerometerValues[0] = event.values[0];
        lastAccelerometerValues[1] = event.values[1];
        lastAccelerometerValues[2] = event.values[2];

        // Calculate magnitude
        double magnitude = Math.sqrt(
                event.values[0] * event.values[0] +
                        event.values[1] * event.values[1] +
                        event.values[2] * event.values[2]
        );

        // Store for comparison
        lastMagnitude = (float) magnitude;

        // Improved activity recognition with hysteresis
        String newActivity;
        if (magnitude < 10.0) {
            newActivity = "Stationary";
        } else if (magnitude < 12.5) {
            newActivity = "Walking";
        } else {
            newActivity = "Running";
        }

        // Only update if activity changed
        if (!newActivity.equals(currentDetectedActivity)) {
            currentDetectedActivity = newActivity;
            lastActivityChangeTime = System.currentTimeMillis();
        }

        updateActivityBuffer(newActivity);

        // For simulation: Add steps when movement is detected
        if (useSimulatedSteps && !newActivity.equals("Stationary")) {
            simulateStepsFromMovement(magnitude);
        }
    }

    private void simulateStepsFromMovement(double magnitude) {
        long currentTime = System.currentTimeMillis();

        // Add steps based on movement intensity
        if (currentTime - lastActivityChangeTime > 1000) { // At least 1 second between step bursts
            int steps = 0;

            if (currentDetectedActivity.equals("Walking")) {
                // Walking: 60-100 steps per minute = 1-2 steps per second
                if (random.nextFloat() > 0.3f) { // 70% chance to add a step
                    steps = 1;
                }
            } else if (currentDetectedActivity.equals("Running")) {
                // Running: 150-180 steps per minute = 2.5-3 steps per second
                steps = 2 + random.nextInt(2); // 2-3 steps
            }

            stepCount += steps;
            lastStepCount = stepCount;
        }
    }

    private void updateActivityBuffer(String activity) {
        activityBuffer.add(activity);
        if (activityBuffer.size() > 10) {
            activityBuffer.remove(0);
        }
    }

    public String getCurrentActivity() {
        if (activityBuffer.isEmpty()) {
            return "Unknown";
        }

        // Find most frequent activity
        Map<String, Integer> frequency = new HashMap<>();
        for (String activity : activityBuffer) {
            frequency.put(activity, frequency.getOrDefault(activity, 0) + 1);
        }

        String mostCommon = "Unknown";
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : frequency.entrySet()) {
            if (entry.getValue() > maxCount) {
                mostCommon = entry.getKey();
                maxCount = entry.getValue();
            }
        }

        return mostCommon;
    }

    public HealthMetrics getHealthMetrics() {
        String currentActivity = getCurrentActivity();
        double activityMultiplier;

        switch (currentActivity) {
            case "Walking":
                activityMultiplier = 1.2;
                break;
            case "Running":
                activityMultiplier = 1.8;
                break;
            default:
                activityMultiplier = 1.0;
        }

        double caloriesBurned = stepCount * CALORIES_PER_STEP * activityMultiplier;
        double distanceKm = (stepCount * STEP_LENGTH) / 1000;

        HealthMetrics metrics = new HealthMetrics();
        metrics.setTotalSteps(stepCount);
        metrics.setTotalCalories(caloriesBurned);
        metrics.setTotalDistance(distanceKm);

        // Simulate heart rate based on activity
        Float heartRate = null;
        if (!currentActivity.equals("Stationary")) {
            int baseHeartRate = 70;
            if (currentActivity.equals("Walking")) {
                heartRate = (float) (baseHeartRate + random.nextInt(20)); // 70-90 bpm
            } else if (currentActivity.equals("Running")) {
                heartRate = (float) (baseHeartRate + 30 + random.nextInt(40)); // 100-140 bpm
            }
        }
        metrics.setAvgHeartRate(heartRate);
        metrics.setCurrentActivity(currentActivity);

        return metrics;
    }

    public SensorData getSensorData() {
        HealthMetrics metrics = getHealthMetrics();

        SensorData data = new SensorData();
        data.setSteps(metrics.getTotalSteps());
        data.setCalories(metrics.getTotalCalories());
        data.setDistance(metrics.getTotalDistance() * 1000);
        data.setActivityType(metrics.getCurrentActivity());
        data.setAccelerometerX(lastAccelerometerValues[0]);
        data.setAccelerometerY(lastAccelerometerValues[1]);
        data.setAccelerometerZ(lastAccelerometerValues[2]);

        if (metrics.getAvgHeartRate() != null) {
            data.setHeartRate(metrics.getAvgHeartRate());
        }

        return data;
    }

    public List<String> getAvailableSensors() {
        List<String> sensors = new ArrayList<>();

        if (sensorManager != null) {
            List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

            // Check for specific sensors
            boolean hasAccelerometer = false;
            boolean hasStepCounter = false;
            boolean hasHeartRate = false;

            for (Sensor sensor : sensorList) {
                switch (sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        hasAccelerometer = true;
                        break;
                    case Sensor.TYPE_STEP_COUNTER:
                        hasStepCounter = true;
                        break;
                    case Sensor.TYPE_HEART_RATE:
                        hasHeartRate = true;
                        break;
                    case Sensor.TYPE_GYROSCOPE:
                        sensors.add("Gyroscope");
                        break;
                    case Sensor.TYPE_LIGHT:
                        sensors.add("Light Sensor");
                        break;
                    case Sensor.TYPE_PROXIMITY:
                        sensors.add("Proximity Sensor");
                        break;
                }
            }

            if (hasAccelerometer) sensors.add(0, "Accelerometer");
            if (hasStepCounter) {
                sensors.add(0, "Step Counter");
            } else {
                sensors.add(0, "Step Counter (Simulated)");
            }
            if (hasHeartRate) sensors.add(0, "Heart Rate");
        }

        return sensors;
    }

    public void resetCounters() {
        stepCount = 0;
        lastStepCount = 0;
        initialSteps = 0;
        isInitialized = false;
        activityBuffer.clear();
        currentDetectedActivity = "Stationary";
    }

    public void cleanup() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }

        // Stop step simulation
        handler.removeCallbacks(stepSimulator);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes if needed
    }
}