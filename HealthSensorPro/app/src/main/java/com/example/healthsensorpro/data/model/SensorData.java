package com.example.healthsensorpro.data.model;

public class SensorData {
    private long timestamp;
    private int steps;
    private double calories;
    private double distance;
    private Float heartRate;
    private float accelerometerX;
    private float accelerometerY;
    private float accelerometerZ;
    private String activityType;

    public SensorData() {
        this.timestamp = System.currentTimeMillis();
        this.steps = 0;
        this.calories = 0.0;
        this.distance = 0.0;
        this.heartRate = null;
        this.accelerometerX = 0f;
        this.accelerometerY = 0f;
        this.accelerometerZ = 0f;
        this.activityType = "Unknown";
    }

    // Getters and Setters
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public int getSteps() { return steps; }
    public void setSteps(int steps) { this.steps = steps; }

    public double getCalories() { return calories; }
    public void setCalories(double calories) { this.calories = calories; }

    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }

    public Float getHeartRate() { return heartRate; }
    public void setHeartRate(Float heartRate) { this.heartRate = heartRate; }

    public float getAccelerometerX() { return accelerometerX; }
    public void setAccelerometerX(float accelerometerX) { this.accelerometerX = accelerometerX; }

    public float getAccelerometerY() { return accelerometerY; }
    public void setAccelerometerY(float accelerometerY) { this.accelerometerY = accelerometerY; }

    public float getAccelerometerZ() { return accelerometerZ; }
    public void setAccelerometerZ(float accelerometerZ) { this.accelerometerZ = accelerometerZ; }

    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }
}