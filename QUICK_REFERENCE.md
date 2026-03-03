# 🚀 FiTrack - Quick Reference

## ⚡ Quick Start

### Initialize & Push to GitHub
```bash
cd ~/android-projects/fitrack
./setup-github.sh
```

### Or Manual Setup
```bash
cd ~/android-projects/fitrack
git init
git add .
git commit -m "Initial commit"
git remote add origin https://github.com/YOUR_USERNAME/fitrack.git
git push -u origin main
```

---

## 📥 Getting the APK

### Option 1: GitHub Actions (Recommended)
1. Push code to GitHub
2. Go to repo → **Actions** tab
3. Wait for build (~5-10 min)
4. Click build → Download **"fitrack-debug-apk"** artifact
5. Install on phone

### Option 2: Local Build (if Android Studio installed)
```bash
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 🏷️ Creating a Release

```bash
git tag v1.0.0
git push origin v1.0.0
```

→ Automatically creates GitHub Release with APK!

---

## 📱 App Features

### Fitness Tab (Green)
- Log workout: name, duration, calories
- View today's workout count

### Nutrition Tab (Orange)
- Enter food name (e.g., "burger", "pizza", "salad")
- Click **Analyze** → See calories & macros
- Log meal to track daily calories

---

## 🎯 Supported Foods (Mock Database)

The app can estimate calories for:
- burger, pizza, salad, chicken
- rice, pasta, apple, banana
- egg, bread, milk, coffee
- water, ice cream, chocolate, fries

**Unknown foods:** Default to 200 cal, 10g protein, 20g carbs, 8g fat

---

## 🛠️ Development Shortcuts

### Clean Build
```bash
./gradlew clean assembleDebug
```

### Run Lint
```bash
./gradlew lintDebug
```

### Check Dependencies
```bash
./gradlew app:dependencies
```

### Update Gradle Wrapper
```bash
./gradlew wrapper --gradle-version=8.0
```

---

## 📁 Important Files

| File | Purpose |
|------|---------|
| `MainActivity.kt` | Main app logic |
| `Database.kt` | Room database setup |
| `NutritionApi.kt` | Mock nutrition service |
| `activity_main.xml` | UI layout |
| `android-build.yml` | GitHub Actions CI/CD |
| `build.gradle.kts` | App dependencies |

---

## 🔧 Common Issues

### Build Fails on ARM
**Problem:** AAPT2 errors on Raspberry Pi  
**Solution:** Use GitHub Actions for cloud builds ✅

### Gradle Out of Memory
```bash
export GRADLE_OPTS="-Xmx2g"
./gradlew assembleDebug
```

### Git Issues
```bash
git status
git log --oneline
git remote -v
```

---

## 📚 Documentation

| Document | Purpose |
|----------|---------|
| `README.md` | Project overview |
| `GITHUB_ACTIONS_SETUP.md` | Detailed CI/CD guide |
| `PROJECT_OVERVIEW.md` | Complete architecture docs |
| `QUICK_REFERENCE.md` | This file |

---

## 🎨 Adding More Foods

Edit `app/src/main/java/com/fitrack/app/api/NutritionApi.kt`:

```kotlin
private val foodDatabase = mapOf(
    "your_food" to NutritionData(calories, protein, carbs, fat),
    // Example:
    "avocado" to NutritionData(160, 2f, 9f, 15f)
)
```

---

## 🔑 Future API Integration

Get free API keys:
- **Edamam:** https://developer.edamam.com/
- **Nutritionix:** https://www.nutritionix.com/business/api

Then update `NutritionApi.kt` to make real API calls.

---

## 📞 Need Help?

1. Check GitHub Actions logs for build errors
2. Review documentation in project folder
3. Search Android/Sdk build logs
4. Open issue on GitHub

---

**Quick Tips:**
- ✅ Always commit before pushing
- ✅ Tag releases for production APKs
- ✅ Test on real device before release
- ✅ Check Actions tab for build status

---

**Happy Coding! 🎉**
