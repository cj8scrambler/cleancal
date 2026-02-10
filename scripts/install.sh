#!/bin/bash

# Install APK locally using adb
set -e

APK_PATH="app/build/outputs/apk/debug/app-debug.apk"

if [ ! -f "$APK_PATH" ]; then
    echo "APK not found at $APK_PATH"
    echo "Please build the app first using: ./scripts/build.sh"
    exit 1
fi

echo "Installing APK to connected device..."

# Check if adb is available
if ! command -v adb &> /dev/null; then
    echo "adb not found. Please install Android SDK platform-tools."
    exit 1
fi

# Check if device is connected
if ! adb devices | grep -q "device$"; then
    echo "No device connected. Please connect a device or start an emulator."
    exit 1
fi

# Install the APK
adb install -r "$APK_PATH"

echo ""
echo "Installation complete!"
echo "Launch the app with: adb shell am start -n com.cleancal/.MainActivity"
