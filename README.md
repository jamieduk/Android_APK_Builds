# 🏋️ FiTrack - Fitness & Nutrition Tracker

**Your AI-powered fitness companion for tracking workouts and estimating food calories!**

## 🎯 Features

### 👤 User Profile & Bio
- Complete profile setup (name, DOB, gender, height, weight)
- Activity level & fitness goals
- Target weight tracking
- **BMI auto-calculation** from your stats
- Monthly weight logging for progress tracking

### 📊 Health Metrics & Calculators
- **BMI Calculator** - Body Mass Index with category & personalized advice
- **BMR Calculator** - Basal Metabolic Rate (Mifflin-St Jeor equation)
- **TDEE Calculator** - Total Daily Energy Expenditure
- **Body Fat Estimator** - Estimate body fat % from BMI
- **Target Heart Rate Zones** - Optimize your cardio workouts
- **Daily Calorie Recommendations** - Personalized for your goals
- **Water Intake Calculator** - Stay properly hydrated
- **Ideal Weight Range** - Healthy weight for your height

### 💪 Fitness Tracking
- Log workouts with name, duration, and calories burned
- Track daily workout count
- Weight logging with history
- View workout trends
- Quick fitness tips

### 🍎 Nutrition & Calorie Estimation
- **AI-powered calorie estimation** from food descriptions
- Track macronutrients (protein, carbs, fat)
- Log meals automatically
- Daily calorie counter
- Personalized macro recommendations
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
- **Database:** Room (SQLite) - 5 tables
- **Async:** Kotlin Coroutines + Flow
- **HTTP:** Retrofit 2
- **UI:** Material Design Components
- **Health Calculators:** BMI, BMR, TDEE, Body Fat, Heart Rate
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 34 (Android 14)

---

## 📱 How to Use

### 1. Set Up Your Profile 👤
1. Go to **Profile** tab
2. Enter your details:
   - Name, Date of Birth, Gender
   - Height (cm) and Weight (kg)
   - Activity level
   - Fitness goal (lose/maintain/gain)
   - Target weight
3. Tap **Save Profile**
4. Your **BMI** is automatically calculated!

### 2. Use Health Calculators 🧮
1. Go to **Tools/Calculators** tab
2. Choose a calculator:
   - **BMI Calculator**: Enter height & weight
   - **Daily Calorie Needs**: Enter age, gender, activity, goal
   - **Body Fat Estimator**: Enter your BMI & age
   - **Heart Rate Zones**: Automatic based on your age
3. Tap **Calculate** to see detailed results with advice

### 3. Track a Workout 💪
1. Go to **Fitness** tab
2. Enter workout name, duration, calories burned
3. Tap **Log Workout**
4. Log your weight monthly to track progress

### 4. Estimate Food Calories 🍎
1. Go to **Nutrition** tab
2. Enter food name (e.g., "burger", "pizza", "salad")
3. Tap **Analyze**
4. View calories, protein, carbs, fat
5. Tap **Log This Meal** to save

### 5. View Daily Stats
- Top dashboard shows:
  - Workouts completed today
  - Total calories consumed
  - Latest logged weight

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
