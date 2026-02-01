package com.example.healthsensorpro.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.healthsensorpro.data.model.HealthMetrics;
import com.example.healthsensorpro.data.repository.SensorRepository;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SensorViewModel extends AndroidViewModel {
    private SensorRepository repository;
    private MutableLiveData<HealthMetrics> healthMetrics = new MutableLiveData<>();
    private MutableLiveData<List<String>> availableSensors = new MutableLiveData<>();
    private MutableLiveData<Boolean> isMonitoring = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private Timer updateTimer;

    public SensorViewModel(Application application) {
        super(application);
        repository = new SensorRepository(application);
        loadAvailableSensors();
        startMonitoring();
    }

    public LiveData<HealthMetrics> getHealthMetrics() {
        return healthMetrics;
    }

    public LiveData<List<String>> getAvailableSensors() {
        return availableSensors;
    }

    public LiveData<Boolean> getIsMonitoring() {
        return isMonitoring;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    private void loadAvailableSensors() {
        availableSensors.setValue(repository.getAvailableSensors());
    }

    public void startMonitoring() {
        isMonitoring.setValue(true);

        if (updateTimer != null) {
            updateTimer.cancel();
        }

        updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateMetrics();
            }
        }, 0, 1000); // Update every second
    }

    public void stopMonitoring() {
        isMonitoring.setValue(false);

        if (updateTimer != null) {
            updateTimer.cancel();
            updateTimer = null;
        }
    }

    private void updateMetrics() {
        try {
            HealthMetrics metrics = repository.getHealthMetrics();
            healthMetrics.postValue(metrics);
        } catch (Exception e) {
            errorMessage.postValue("Error updating metrics: " + e.getMessage());
        }
    }

    public void resetData() {
        repository.resetCounters();
        updateMetrics();
    }

    public void clearError() {
        errorMessage.setValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (updateTimer != null) {
            updateTimer.cancel();
        }
        repository.cleanup();
    }
}