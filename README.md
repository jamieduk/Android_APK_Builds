# 🏋️ FiTrack - Fitness & Nutrition Tracker

**Your AI-powered fitness companion for tracking workouts and estimating food calories!**

## 🎯 Features

### 💪 Fitness Tracking
- Log workouts with name, duration, and calories burned
- Track daily workout count
- View workout history
- Quick fitness tips

### 🍎 Nutrition & Calorie Estimation
- **AI-powered calorie estimation** from food descriptions
- Track macronutrients (protein, carbs, fat)
- Log meals automatically
- Daily calorie counter
- Nutrition tips

### 📱 App Architecture
- **Room Database** - Local storage for workouts and food logs
- **Retrofit** - Ready for API integration (currently uses mock data)
- **Material Design** - Beautiful, modern UI
- **MVVM Pattern** - Clean architecture
- **Kotlin Coroutines** - Asynchronous operations

---

## 🏗️ Project Structure

```
fitrack/
├── app/
│   ├── src/main/
│   │   ├── java/com/fitrack/app/
│   │   │   ├── MainActivity.kt          # Main UI and logic
│   │   │   ├── data/
│   │   │   │   └── Database.kt          # Room database, DAOs, entities
│   │   │   └── api/
│   │   │       └── NutritionApi.kt      # API interfaces + mock service
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   └── activity_main.xml    # Main UI layout
│   │   │   ├── values/
│   │   │   │   ├── strings.xml
│   │   │   │   ├── colors.xml
│   │   │   │   └── themes.xml
│   │   │   └── xml/
│   │   │       └── file_paths.xml       # FileProvider config
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
└── settings.gradle.kts
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- JDK 17+
- Android SDK 34
- Minimum SDK: 24 (Android 7.0)

### Build Instructions

1. **Open in Android Studio**
   ```bash
   cd ~/android-projects/fitrack
   # Open Android Studio and import this project
   ```

2. **Sync Gradle**
   - Android Studio will automatically sync
   - Wait for dependencies to download

3. **Build APK**
   ```bash
   ./gradlew assembleDebug
   ```

4. **Install on Device**
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

---

## 🔧 Configuration

### Nutrition API (Future Enhancement)

Currently, the app uses a **mock nutrition database** for demonstration. To integrate a real nutrition API:

1. **Edamam API** (Free tier available)
   - Get API key at: https://developer.edamam.com/
   - Update `NutritionApi.kt` with your credentials
   - Uncomment API calls

2. **Nutritionix API** (Alternative)
   - Get API key at: https://www.nutritionix.com/business/api
   - Similar integration process

---

## 📸 Image-Based Calorie Estimation (Roadmap)

**Coming soon:** Real AI-powered food recognition from photos!

Future implementation options:
- **Google Cloud Vision API** + Nutrition API
- **TensorFlow Lite** on-device model
- **Clarifai Food Detection**
- Custom ML model trained on food datasets

---

## 🎨 Current Features

✅ Workout logging with duration & calories
✅ Food calorie estimation (keyword-based mock)
✅ Daily stats dashboard
✅ Local database (Room)
✅ Material Design UI
✅ Tab navigation (Fitness/Nutrition)
✅ Quick tips cards

🚧 Photo capture & analysis
🚧 Real API integration
🚧 Historical charts
🚧 Goal setting
🚧 User profiles

---

## 🛠️ Tech Stack

- **Language:** Kotlin
- **Architecture:** MVVM
- **Database:** Room (SQLite)
- **Async:** Kotlin Coroutines + Flow
- **HTTP:** Retrofit 2
- **UI:** Material Design Components
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 34 (Android 14)

---

## 📱 How to Use

### 1. Track a Workout
1. Go to **Fitness** tab
2. Enter workout name (e.g., "Morning Run")
3. Enter duration in minutes
4. Enter estimated calories burned
5. Tap **Log Workout**

### 2. Estimate Food Calories
1. Go to **Nutrition** tab
2. Enter food name or description (e.g., "burger", "salad", "pizza")
3. Tap **Analyze**
4. View estimated calories and macros
5. Tap **Log This Meal** to save

### 3. View Daily Stats
- Top cards show:
  - Number of workouts today
  - Total calories consumed today

---

## 📝 Notes

- **Mock Data:** Currently uses a simple keyword matching system for calorie estimation
- **No Internet Required:** Everything works offline with mock data
- **Camera:** Placeholder ready for future implementation
- **Database:** All data stored locally in Room database

---

## 🤝 Contributing

This is a starter project! Ideas for improvements:
- Implement real nutrition API
- Add photo-based food recognition
- Create workout categories
- Add historical charts
- Implement goal tracking
- Add water intake tracker
- Create user profiles

---

## 📄 License

This project is open source. Use it however you like!

---

## 🎯 Built with ❤️ on Raspberry Pi

This app was created on a **Raspberry Pi 5** (16GB RAM) running Ubuntu 24.04 ARM64, proving you can develop Android apps on ARM hardware!

---

**Happy Tracking! 🏃‍♂️🍏💪**
