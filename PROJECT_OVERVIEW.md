# 📱 FiTrack - Complete Project Overview

**Fitness & Nutrition Tracker Android App**  
Built on Raspberry Pi 5 (ARM64) • Cloud Build with GitHub Actions

---

## 🎯 What is FiTrack?

FiTrack is a fully-functional Android app that helps users:
- **Track workouts** - Log exercises with duration and calories burned
- **Estimate food calories** - AI-powered nutrition analysis from food descriptions
- **Monitor daily progress** - Real-time stats dashboard
- **Build healthy habits** - Quick tips for fitness and nutrition

---

## 🏗️ Project Architecture

### Tech Stack
- **Language:** Kotlin
- **Architecture:** MVVM (Model-View-ViewModel)
- **Database:** Room (SQLite abstraction)
- **HTTP Client:** Retrofit 2
- **UI:** Material Design Components
- **Async:** Kotlin Coroutines + Flow
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 34 (Android 14)

### Project Structure
```
fitrack/
├── .github/
│   └── workflows/
│       └── android-build.yml       # CI/CD pipeline for auto-builds
├── .vscode/
│   ├── extensions.json             # Recommended VS Code extensions
│   └── settings.json               # VS Code configuration
├── app/
│   └── src/main/
│       ├── java/com/fitrack/app/
│       │   ├── MainActivity.kt     # Main UI controller
│       │   ├── data/
│       │   │   └── Database.kt     # Room database, DAOs, entities
│       │   └── api/
│       │       └── NutritionApi.kt # API interfaces + mock service
│       ├── res/
│       │   ├── layout/
│       │   │   └── activity_main.xml  # Main UI layout
│       │   ├── values/
│       │   │   ├── strings.xml        # String resources
│       │   │   ├── colors.xml         # Color definitions
│       │   │   └── themes.xml         # App theme
│       │   └── xml/
│       │       └── file_paths.xml     # FileProvider config
│       └── AndroidManifest.xml        # App permissions & config
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar        # Gradle wrapper
│       └── gradle-wrapper.properties # Gradle version config
├── build.gradle.kts                  # Root build configuration
├── app/build.gradle.kts              # App module build configuration
├── gradle.properties                 # Gradle settings
├── settings.gradle.kts               # Project settings
├── local.properties                  # SDK location (git-ignored)
├── .gitignore                        # Git ignore rules
├── setup-github.sh                   # Quick GitHub setup script
├── README.md                         # Project readme
├── GITHUB_ACTIONS_SETUP.md           # Detailed CI/CD guide
└── PROJECT_OVERVIEW.md               # This file
```

---

## 🎨 Features

### Implemented ✅

#### 1. Fitness Tracking
- Log workouts with:
  - Name (e.g., "Morning Run")
  - Duration (minutes)
  - Calories burned
- View daily workout count
- Persistent storage in Room database

#### 2. Nutrition Estimation
- Mock AI-powered calorie estimation
- Supports common foods: burger, pizza, salad, chicken, rice, etc.
- Displays:
  - Calories (kcal)
  - Protein (g)
  - Carbohydrates (g)
  - Fat (g)
- Log meals to database

#### 3. Dashboard
- Today's stats:
  - Number of workouts
  - Total calories consumed
- Beautiful Material Design cards
- Tab navigation (Fitness / Nutrition)

#### 4. Database
- Room database with 2 tables:
  - `workouts` - All logged workouts
  - `food_logs` - All logged meals
- Coroutines Flow for reactive updates
- Date range queries

#### 5. UI/UX
- Material Design 3
- Green theme for fitness, orange for nutrition
- Responsive layouts
- Toast notifications
- Input validation
- Quick tips cards

### Future Enhancements 🚧

- [ ] **Camera Integration** - Real photo-based food recognition
- [ ] **ML Model** - TensorFlow Lite for on-device food detection
- [ ] **Real API** - Integrate Edamam/Nutritionix API
- [ ] **Charts** - Historical data visualization (MPAndroidChart)
- [ ] **Goals** - Daily/weekly fitness goals with progress tracking
- [ ] **Water Tracker** - Daily water intake logging
- [ ] **User Profiles** - Multiple user support
- [ ] **Dark Mode** - Theme switching
- [ ] **Export Data** - CSV/PDF export of workouts and meals
- [ ] **Social Sharing** - Share achievements

---

## 🔧 Development

### Prerequisites
- JDK 17
- Android SDK 34
- Minimum SDK 24

### Build Commands

**Debug Build:**
```bash
./gradlew assembleDebug
```

**Release Build:**
```bash
./gradlew assembleRelease
```

**Clean Build:**
```bash
./gradlew clean assembleDebug
```

**Run Lint:**
```bash
./gradlew lintDebug
```

### Install on Device
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## ☁️ Cloud Build (GitHub Actions)

### How It Works

```
Git Push → GitHub Actions → Ubuntu Runner → Gradle Build → APK Artifact
```

### Workflow Steps

1. **Checkout** code
2. **Setup JDK 17**
3. **Setup Gradle** with caching
4. **Build Debug APK**
5. **Upload as Artifact**
6. **If Release Tag** → Build Release + Create GitHub Release

### Usage

1. Push to `main` branch → Automatic debug build
2. Tag release: `git tag v1.0.0 && git push origin v1.0.0`
3. Download APK from Actions tab or Releases page

### Build Time
- First build: ~8-12 minutes
- Subsequent builds: ~4-6 minutes (cached)

---

## 📊 API Integration (Mock vs Real)

### Current: Mock Service
The app uses `MockNutritionService.kt` which:
- Contains a hardcoded database of ~15 common foods
- Uses keyword matching for estimation
- Returns approximate nutrition data
- Works offline
- No API key required

### Future: Real API Integration

**Option 1: Edamam Nutrition Analysis API**
- Free tier: 100 requests/day
- Get key at: https://developer.edamam.com/
- Supports detailed nutrition data for 1M+ foods

**Option 2: Nutritionix API**
- Free tier: 500 requests/day
- Get key at: https://www.nutritionix.com/business/api
- Database of 800K+ foods

**Implementation:**
Update `NutritionApi.kt` to use real endpoints instead of mock data.

---

## 🎓 Learning Resources

### Kotlin
- [Kotlin Docs](https://kotlinlang.org/docs/home.html)
- [Kotlin Koans](https://play.kotlinlang.org/koans)

### Android Development
- [Android Developers Guide](https://developer.android.com/guide)
- [Android Basics with Kotlin](https://developer.android.com/courses/android-basics-kotlin/course)

### Room Database
- [Room Guide](https://developer.android.com/training/data-storage/room)

### Material Design
- [Material Design for Android](https://material.io/develop/android)

### GitHub Actions
- [Actions Documentation](https://docs.github.com/en/actions)

---

## 🐛 Known Issues & Limitations

1. **AAPT2 on ARM** - Android resource compiler doesn't work on ARM64 Linux
   - **Solution:** Use GitHub Actions for builds
   
2. **Mock Nutrition Data** - Limited to ~15 hardcoded foods
   - **Solution:** Integrate real nutrition API
   
3. **No Camera Feature** - Photo capture placeholder only
   - **Solution:** Implement CameraX + ML model
   
4. **Single User** - No user profile system
   - **Solution:** Add Firebase Auth or local profiles

---

## 📈 Metrics

| Metric | Value |
|--------|-------|
| Lines of Code | ~1,500+ |
| Kotlin Files | 4 |
| Layout Files | 1 |
| Resource Files | 5 |
| Dependencies | ~20 |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 34 (Android 14) |
| APK Size (Debug) | ~20-30 MB |
| Build Time | 5-10 min (cloud) |

---

## 🤝 Contributing

This is an open-source project! Ideas for contributions:

1. Implement real nutrition API
2. Add camera-based food recognition
3. Create beautiful charts for progress
4. Add workout categories and templates
5. Implement goal tracking
6. Add dark mode
7. Internationalization (translations)
8. Unit tests
9. UI/UX improvements

---

## 📝 License

MIT License - Use freely for personal or commercial projects.

---

## 🙏 Acknowledgments

- Built on a **Raspberry Pi 5** (16GB RAM)
- Ubuntu 24.04 ARM64
- GitHub Actions for cloud builds
- Android Open Source Project

---

## 📧 Contact

Have questions or suggestions?

- Open an issue on GitHub
- Check existing documentation
- Review GitHub Actions logs

---

**Built with ❤️ on ARM • Powered by ☁️ CI/CD**

*FiTrack - Your journey to a healthier life starts here!* 🏃‍♂️🍏💪
