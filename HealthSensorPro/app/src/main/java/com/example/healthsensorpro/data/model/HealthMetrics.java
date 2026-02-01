package com.example.healthsensorpro.data.model;

public class HealthMetrics {
    private int totalSteps;
    private double totalCalories;
    private double totalDistance;
    private Float avgHeartRate;
    private String currentActivity;

    public HealthMetrics() {
        this.totalSteps = 0;
        this.totalCalories = 0.0;
        this.totalDistance = 0.0;
        this.avgHeartRate = null;
        this.currentActivity = "Stationary";
    }

    // Getters and Setters
    public int getTotalSteps() { return totalSteps; }
    public void setTotalSteps(int totalSteps) { this.totalSteps = totalSteps; }

    public double getTotalCalories() { return totalCalories; }
    public void setTotalCalories(double totalCalories) { this.totalCalories = totalCalories; }

    public double getTotalDistance() { return totalDistance; }
    public void setTotalDistance(double totalDistance) { this.totalDistance = totalDistance; }

    public Float getAvgHeartRate() { return avgHeartRate; }
    public void setAvgHeartRate(Float avgHeartRate) { this.avgHeartRate = avgHeartRate; }

    public String getCurrentActivity() { return currentActivity; }
    public void setCurrentActivity(String currentActivity) { this.currentActivity = currentActivity; }
}