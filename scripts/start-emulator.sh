#!/bin/bash

# Start emulator script
set -e

# Clean up any leftover X11 lock files from previous runs
echo "Cleaning up X11 lock files..."
rm -f /tmp/.X0-lock /tmp/.X11-unix/X0

echo "Starting Xvfb..."
Xvfb :0 -screen 0 1920x1080x24 &
sleep 2

echo "Starting fluxbox window manager..."
fluxbox &
sleep 2

echo "Starting VNC server..."
VNC_LOG="/tmp/x11vnc.log"
x11vnc -display :0 -rfbport 5900 -forever -nopw -shared -bg -o "$VNC_LOG"

# Wait a moment and verify VNC server started
sleep 2
if ! pgrep -x x11vnc > /dev/null; then
    echo "ERROR: VNC server failed to start!"
    echo "VNC log:"
    cat "$VNC_LOG" 2>/dev/null || echo "No log file found"
    exit 1
fi
echo "VNC server started successfully on port 5900"

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
echo "VNC server is available on port 5900 (display :0)"
echo ""
echo "Connect using:"
echo "  vncviewer localhost:0          # TigerVNC/TightVNC (display number)"
echo "  gvncviewer localhost::5900     # gvncviewer (double colon for port)"
echo ""
echo "Note: gvncviewer localhost:5900 will try port 11800 - use :: for port!"
echo "===================================================================================="

# Keep container running
tail -f /dev/null
