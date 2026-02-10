#!/bin/bash

# Start emulator script
set -e

echo "Starting Xvfb..."
Xvfb :0 -screen 0 1920x1080x24 &
sleep 2

echo "Starting fluxbox window manager..."
fluxbox &
sleep 2

echo "Starting VNC server..."
x11vnc -display :0 -forever -nopw -quiet -bg

echo "Starting Android Emulator..."
cd /workspace

# Build the APK if it doesn't exist
if [ ! -f app/build/outputs/apk/debug/app-debug.apk ]; then
    echo "Building APK..."
    ./gradlew assembleDebug
fi

# Start emulator in headless mode
emulator -avd test_avd -no-audio -no-boot-anim -gpu swiftshader_indirect -no-snapshot -wipe-data &

# Wait for emulator to boot
echo "Waiting for emulator to boot..."
adb wait-for-device
adb shell 'while [[ -z $(getprop sys.boot_completed) ]]; do sleep 1; done'

echo "Emulator is ready!"
echo "Installing APK..."
adb install -r app/build/outputs/apk/debug/app-debug.apk || true

echo "Launching app..."
adb shell am start -n com.cleancal/.MainActivity || true

echo ""
echo "===================================================================================="
echo "Android Emulator is running!"
echo "VNC server is available on port 5900"
echo "Connect using: vncviewer localhost:5900"
echo "Or from host: vncviewer <container-ip>:5900"
echo "===================================================================================="

# Keep container running
tail -f /dev/null
