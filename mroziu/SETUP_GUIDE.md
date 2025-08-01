# 🏋️ Trening Tracker - Setup Guide

## 📋 Prerequisites

1. **Android Studio** (latest version recommended)
2. **Android SDK** (API level 24 or higher)
3. **Android device** or **emulator** for testing

## 🚀 How to Run the App

### Option 1: Android Studio (Recommended)
1. **Open Android Studio**
2. **Import Project** → Select the root folder containing this code
3. **Create local.properties file**:
   - Copy `local.properties.template` to `local.properties`
   - Update the `sdk.dir` path to your Android SDK location
   - Example: `sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk`
4. **Wait for Gradle sync** to complete
5. **Connect Android device** (enable USB debugging) OR **start emulator**
6. **Click Run** (green play button) or press `Ctrl+R`

### Option 2: Command Line
```bash
# Navigate to project directory
cd TreningTracker

# Build the project
./gradlew build

# Install on connected device
./gradlew installDebug

# Or build APK
./gradlew assembleDebug
```

## 📱 Testing the App

### ✅ What You Can Test Right Now:

1. **Home Screen Navigation**
   - Navigate through all menu options
   - Polish localization working

2. **Exercise Management**
   - Add new exercises (with/without weights)
   - Search exercises
   - View exercise list

3. **Full Workout Session**
   - Select exercises for workout
   - Start active workout with timer
   - Add sets with reps and weights
   - Use quick-add buttons (+1, +5, +10)
   - Edit and delete sets
   - Complete workout

### 🎯 Core Features to Test:

#### **Start a Complete Workout:**
1. Home → "Rozpocznij Trening"
2. Select exercises (create some first if needed)
3. Name your workout
4. Start workout session
5. Add sets with reps/weights
6. Use quick-add buttons
7. Mark sets as completed
8. Finish workout

#### **Exercise Management:**
1. Home → "Ćwiczenia"
2. Add new exercise (try both with/without weights)
3. Search for exercises
4. View exercise details

## 🔧 Troubleshooting

### Common Issues:

1. **Gradle Sync Failed**
   - Check internet connection
   - Update Android Studio
   - Invalidate caches: File → Invalidate Caches and Restart

2. **Build Errors**
   - Ensure Android SDK is properly installed
   - Check if all dependencies are available
   - Clean and rebuild: Build → Clean Project

3. **App Crashes**
   - Check Logcat for error messages
   - Ensure device has sufficient storage
   - Try on different device/emulator

## 📊 Database

The app uses **Room Database** which will be automatically created on first run. All data is stored locally on the device.

## 🎨 UI Features

- **Material 3 Design** with dynamic theming
- **Dark/Light mode** support (follows system setting)
- **Polish localization** throughout
- **Responsive design** for different screen sizes

## 🔄 Next Development Steps

The app structure is ready for extending with:
- Body measurement tracking
- Workout plan management
- Progress charts
- Google Drive backup
- Enhanced UI animations

## 📝 Notes

- The app is designed for **portrait orientation**
- Minimum Android version: **API 24 (Android 7.0)**
- Target Android version: **API 34 (Android 14)**
- All placeholder screens show "Do implementacji" (To be implemented)

## 🐛 Known Limitations

- Some screens are placeholders (marked in navigation)
- No data export/import yet
- No Google Drive backup implemented
- Charts not implemented yet

The **core workout tracking functionality is fully operational**! 🎉