# 🚀 GITHUB APK BUILD - STEP BY STEP GUIDE

## 📋 **WHAT YOU'LL NEED:**
- GitHub account (free)
- Your project files
- 10 minutes of time

---

## **STEP 1: CREATE GITHUB ACCOUNT**
1. Go to: https://github.com
2. Click **"Sign up"**
3. Create username, email, password
4. Verify your email

---

## **STEP 2: CREATE NEW REPOSITORY**
1. Click the **green "New"** button (or go to https://github.com/new)
2. Repository name: `trening-tracker`
3. Make it **Public** (so GitHub Actions work for free)
4. ✅ Check **"Add a README file"**
5. Click **"Create repository"**

---

## **STEP 3: UPLOAD YOUR PROJECT FILES**
### Method A: Web Upload (Easiest)
1. In your new repository, click **"uploading an existing file"**
2. Drag and drop ALL your project files/folders
3. Or click **"choose your files"** and select everything
4. Scroll down, add commit message: "Initial project upload"
5. Click **"Commit changes"**

### Method B: GitHub Desktop (Alternative)
1. Download GitHub Desktop: https://desktop.github.com
2. Clone your repository
3. Copy all project files to the cloned folder
4. Commit and push

---

## **STEP 4: ADD GITHUB ACTIONS WORKFLOW**
1. In your repository, click **"Actions"** tab
2. Click **"set up a workflow yourself"**
3. Replace ALL the code with this:

```yaml
name: Build APK

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 11
      uses: actions/setup-java@v3
      with:
        java-version: '11'
        distribution: 'temurin'
        
    - name: Cache Gradle packages
      uses: actions/cache@v3
      with:
        path: |
          ~/.gradle/caches
          ~/.gradle/wrapper
        key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
        restore-keys: |
          ${{ runner.os }}-gradle-
          
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
      
    - name: Build Debug APK
      run: ./gradlew assembleDebug
      
    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: trening-tracker-apk
        path: app/build/outputs/apk/debug/app-debug.apk
```

4. Click **"Commit changes"**

---

## **STEP 5: WAIT FOR BUILD**
1. Go to **"Actions"** tab
2. You'll see a workflow running (yellow circle)
3. Wait 5-10 minutes for it to complete
4. When done, you'll see a green checkmark ✅

---

## **STEP 6: DOWNLOAD YOUR APK**
1. Click on the completed workflow
2. Scroll down to **"Artifacts"**
3. Click **"trening-tracker-apk"**
4. Download the ZIP file
5. Extract it - your APK is inside!

---

## **STEP 7: INSTALL ON PHONE**
1. Transfer the APK to your Android phone
2. Open the APK file
3. Allow "Install from unknown sources"
4. Install and enjoy! 🎉

---

## 🚨 **TROUBLESHOOTING**

### **Build Failed?**
- Check the "Actions" tab for error details
- Most common: missing files or wrong folder structure

### **Can't Find APK?**
- Look in the "Artifacts" section of the completed workflow
- Download might be a ZIP file - extract it

### **Upload Too Big?**
- GitHub has 100MB file limit
- Remove any large files (videos, images) temporarily

---

## 🎯 **TIPS:**
- Keep your repository **Public** for free GitHub Actions
- Each push to main branch will rebuild the APK automatically
- You get 2000 free build minutes per month
- The APK will be a debug version (perfect for testing)

---

*This method works 100% automatically - no Android Studio needed!* 🚀