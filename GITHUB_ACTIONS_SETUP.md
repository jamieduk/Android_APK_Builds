# 🚀 GitHub Actions Setup Guide for FiTrack

This guide will help you set up automatic APK builds using GitHub Actions.

---

## 📋 What You'll Get

✅ **Automatic builds** - Every push to `main`/`master` branch triggers a build  
✅ **Downloadable APKs** - Get the APK directly from GitHub Actions artifacts  
✅ **Release builds** - Tag a release with `v1.0.0` and get a production APK  
✅ **Lint checks** - Code quality validation on every PR  
✅ **No local build needed** - Build happens in the cloud on Ubuntu

---

## 🎯 Step-by-Step Setup

### Step 1: Create a GitHub Repository

1. Go to [GitHub.com](https://github.com)
2. Click **"+ "** → **"New repository"**
3. Name it: `fitrack`
4. Description: "Fitness & Nutrition Tracker Android App"
5. **Don't** initialize with README (we already have one)
6. Click **"Create repository"**

### Step 2: Initialize Git Locally

```bash
cd ~/android-projects/fitrack

# Initialize git repository
git init

# Add all files
git add .

# Create initial commit
git commit -m "Initial commit: FiTrack Android App"
```

### Step 3: Add GitHub Remote

```bash
# Add your GitHub repo as remote (replace YOUR_USERNAME with your GitHub username)
git remote add origin https://github.com/YOUR_USERNAME/fitrack.git

# Verify
git remote -v
```

### Step 4: Push to GitHub

```bash
# Rename branch to main (if needed)
git branch -M main

# Push to GitHub
git push -u origin main
```

### Step 5: Watch the Build

1. Go to your repository on GitHub
2. Click the **"Actions"** tab
3. You'll see "Android Build and Release" workflow running
4. Wait 5-10 minutes for the build to complete
5. Click on the running job to see progress

### Step 6: Download Your APK

Once the build completes (green checkmark):

1. Click on the successful build run
2. Scroll down to **"Artifacts"** section
3. Click **"fitrack-debug-apk"** to download
4. Extract the ZIP file → you get `app-debug.apk`

### Step 7: Install on Your Device

```bash
# Connect your Android phone via USB
# Enable "Developer Options" and "USB Debugging" on phone
# Then:

adb install ~/Downloads/app-debug.apk
```

Or transfer the APK to your phone and install manually.

---

## 🏷️ Creating a Release Build

To create a production-ready release:

```bash
# Create a version tag
git tag v1.0.0

# Push the tag
git push origin v1.0.0
```

This will:
- Trigger a release build
- Create a GitHub Release with the APK attached
- Anyone can download the release APK

---

## 📱 Testing the App

### On a Real Device:
1. Enable **Developer Options** on your Android phone
   - Go to Settings → About Phone
   - Tap "Build Number" 7 times
2. Enable **USB Debugging** in Developer Options
3. Connect phone via USB
4. Run: `adb install app-debug.apk`

### On an Emulator (if you have one):
- Start Android emulator
- Run: `adb install app-debug.apk`

---

## 🔧 Customizing the Build

### Change App Version

Edit `app/build.gradle.kts`:

```kotlin
defaultConfig {
    versionCode = 2      // Increment for each release
    versionName = "1.1.0" // User-visible version
}
```

### Add API Keys

If you integrate a real nutrition API:

1. Don't commit API keys to git!
2. Use GitHub Secrets:
   - Go to Repo Settings → Secrets → Actions
   - Add secret: `NUTRITION_API_KEY`
3. Update workflow to use secrets

### Build Flavor (Optional)

Add product flavors in `app/build.gradle.kts` for debug/release variants with different configs.

---

## 📊 Workflow Overview

```
Push Code → GitHub Actions → Build on Ubuntu → Upload APK
                                      ↓
                              Download & Install
```

**Build Time:** ~5-10 minutes  
**Artifact Retention:** 90 days (default)  
**Cost:** Free for public repos, included in free tier for private repos

---

## 🐛 Troubleshooting

### Build Fails

1. Click on the failed job in Actions tab
2. Expand the logs to see the error
3. Common issues:
   - Syntax errors in Kotlin files
   - Missing resources
   - Gradle configuration issues

### APK Not Downloading

- Make sure the build completed successfully (green checkmark)
- Artifacts expire after 90 days
- Download within the retention period

### Want Faster Builds

- Build time is typically 5-10 minutes
- Subsequent builds are faster due to caching
- Consider using GitHub-hosted runners with more power (paid)

---

## 📁 Project Structure Summary

```
fitrack/
├── .github/
│   └── workflows/
│       └── android-build.yml    # CI/CD pipeline
├── app/
│   └── src/main/                # App source code
├── build.gradle.kts             # Root build config
├── app/build.gradle.kts         # App build config
├── gradle.properties            # Gradle settings
└── README.md                    # Project documentation
```

---

## 🎉 Next Steps

1. ✅ Push code to GitHub
2. ✅ Wait for first build
3. ✅ Download and test APK
4. ✅ Start adding features!
5. ✅ Create releases with version tags

---

## 📚 Resources

- [GitHub Actions Docs](https://docs.github.com/en/actions)
- [Android Gradle Plugin](https://developer.android.com/studio/build)
- [FiTrack README](README.md)

---

**Happy Building! 🏗️📱**

Need help? Check the workflow logs or open an issue on GitHub.
