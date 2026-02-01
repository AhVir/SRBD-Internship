# SRBD Internship - Assessment Tasks

# Task 03 - Blog
## Health Sensor Pro (Task 2)

A professional Android application that goes beyond basic sensor reading by implementing health metrics calculation and activity recognition.

---

## Project Overview

This project was developed as part of the **Samsung Internship Assessment (Task 2)**. While the requirement was simply to *“read any sensor’s data”,*, I chose to build something more meaningful and practical, a **health monitoring application** that processes raw sensor data into actionable health insights.

### Why This Approach?

Instead of just displaying raw sensor values, this project demonstrates:

- **Practical application** of sensor data  
- **Data processing skills** (steps → calories → distance)  
- **Real-world relevance** (similar to Samsung Health)  
- **Professional architecture** (clean code, separation of concerns)

---

## Features

### 1. Health Metrics Calculation
- **Step Counting** using the built-in step counter sensor  
- **Calorie Estimation** based on steps and detected activity  
- **Distance Tracking** from step count  
- **Activity Recognition** (Stationary / Walking / Running)

### 2. Multiple Sensor Integration
- Accelerometer (activity detection)
- Step Counter (primary step tracking)
- Automatic detection of all available sensors
- Graceful degradation if sensors are unavailable

### 3. Professional Architecture
- **MVVM Pattern** with ViewModel and LiveData  
- **Repository Pattern** for sensor abstraction  
- **Clean separation** (UI, Business Logic, Data)  
- **Material Design 3** modern UI

### 4. User Experience
- Real-time data updates  
- Clean and professional interface  
- Activity visualisation with icons  
- Sensor availability display  
- Start / Pause / Reset controls  

---

# HealthSensorPro

```
HealthSensorPro/
├── app/
│   └── src/
│       └── main/
│           ├── java/com/example/healthsensorpro/
│           │   ├── data/
│           │   │   ├── model/
│           │   │   │   └── Data classes (SensorData, HealthMetrics)
│           │   │   └── repository/
│           │   │       └── SensorRepository (sensor operations)
│           │   ├── presentation/
│           │   │   ├── viewmodel/
│           │   │   │   └── SensorViewModel (business logic)
│           │   │   └── ui/
│           │   │       └── UI components (Compose)
│           │   └── utils/
│           │       └── Helper classes
│           └── res/
│               └── Resources (layouts, strings, drawables, etc.)
└── ...
```


## How It Works

### 1. Sensor Data Collection
- Uses Android's `SensorManager` to access hardware sensors
- Registers listeners for accelerometer and step counter
- Handles callbacks inside the repository layer

### 2. Data Processing Algorithms

### Steps → Calories

```kotlin
val caloriesPerStep = 0.04  // Average metabolic estimate
val calories = steps * caloriesPerStep
```

### Steps → Distance

```kotlin
val stepLength = 0.762  // meters
val distanceKm = (steps * stepLength) / 1000
```

### Activity Recognition

```kotlin
val magnitude = sqrt(x*x + y*y + z*z)

val activity = when {
    magnitude < 10.5 -> "Stationary"
    magnitude < 13.0 -> "Walking"
    else -> "Running"
}
```

## Architecture Flow

```
Sensors → SensorRepository → SensorViewModel → UI (Compose)
           ↓                       ↓
       Raw Data            Processed Metrics
```

## Screens

### Dashboard Screen
- Health metric cards (Steps, Calories, Distance, Activity)
- Activity indicator with visual feedback
- Control buttons (Start / Pause / Reset)
- Available sensors list

### Real-Time Updates
- Live step counting
- Continuous calorie calculation
- Instant activity detection

## Technical Implementation

### Key Components

#### SensorRepository.kt
- Manages all sensor interactions
- Handles sensor listeners and callbacks
- Processes raw sensor data
- Ensures proper cleanup

#### SensorViewModel.kt
- Business logic layer
- LiveData for reactive UI updates
- Coroutines for background processing
- Error handling and state management

#### UI Components (Jetpack Compose)
- Declarative UI
- Reusable components (MetricCard, ActivityIndicator)
- Material Design 3 theming

## Permissions Required

```xml
<uses-permission android:name="android.permission.ACTIVITY_RECOGNITION" />
<uses-permission android:name="android.permission.BODY_SENSORS" />
```

## Setup & Installation

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/HealthSensorPro.git
```

### 2. Open in Android Studio
- Open Android Studio
- Select Open → choose project folder
- Wait for Gradle sync

### 3. Run the App
- Connect Android device (USB debugging enabled)
- Click Run ▶
- Grant permissions when prompted

### 4. Test the App
- Walk with your phone to see step counting
- Try walking vs running
- View available sensors on your device

## Algorithms Explained

### Why These Constants?
- **0.04 calories/step**: Average metabolic estimate
- **0.762 meters/step**: Average adult step length
- **Accelerometer thresholds**: Empirically chosen

### Accuracy Considerations
- Step counter is hardware-based and accurate
- Calories are estimates (vary by person)
- Activity detection uses simple heuristics (ML can improve this)

## Code Quality Features

✔ SOLID principles  
✔ Clean architecture  
✔ Graceful error handling  
✔ Proper resource management  
✔ Modern Android stack (Compose, ViewModel, Coroutines)  
✔ Professional UI/UX (Material Design 3)

## Design Decisions

### Why not display raw sensor values?
- Raw accelerometer data is meaningless to users
- Health metrics are intuitive and actionable
- Shows understanding beyond basic API usage

### Why MVVM?
- Industry-standard Android architecture
- Testable and maintainable
- Essential for scalable apps

### Why activity recognition?
- Demonstrates sensor fusion
- Adds real-world value
- Relevant to Samsung's health & wearable ecosystem

## Project Learnings

- Android Sensor Framework (deep API understanding)
- Health metric algorithms
- Modern Android architecture (MVVM, Coroutines)
- Material Design 3
- Production considerations (permissions, battery, lifecycle)

## ¶ Future Enhancements

- Machine Learning for better activity recognition
- Historical data and trends
- User profiles (age, weight, height)
- Wear OS integration
- Samsung Health SDK integration
