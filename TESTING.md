# Testing Guide for CleanCal

This document provides testing procedures to verify the Android build framework.

## Prerequisites for Testing

1. **System Requirements**:
   - Linux x86_64 platform
   - Docker and Docker Compose installed
   - Internet connectivity (for initial build)
   - At least 10GB free disk space

2. **For Device Testing**:
   - Android device with USB debugging enabled
   - adb installed and configured

3. **For Emulator Testing**:
   - KVM support enabled (for hardware acceleration)
   - VNC client installed

## Test Procedures

### 1. Verify Project Structure

```bash
# Check all required files exist
ls -la build.gradle.kts settings.gradle.kts gradlew app/build.gradle.kts
ls -la app/src/main/AndroidManifest.xml app/src/main/java/com/cleancal/MainActivity.kt

# Verify XML files are valid
find app -name "*.xml" -exec echo "Checking: {}" \; -exec python3 -c "import xml.etree.ElementTree as ET; ET.parse('{}')" \;
```

### 2. Build Docker Image

```bash
# Build the Android build environment
docker compose build build

# Verify image was created
docker images | grep cleancal-build
```

Expected: `cleancal-build` image appears in the list

### 3. Build Android APK

```bash
# Build the APK using Docker
./scripts/build.sh

# Or using make
make build

# Or directly
docker compose run --rm build ./gradlew assembleDebug
```

Expected output:
- Gradle downloads dependencies
- Kotlin code compiles successfully
- APK is generated at `app/build/outputs/apk/debug/app-debug.apk`

### 4. Verify APK

```bash
# Check APK exists and has reasonable size
ls -lh app/build/outputs/apk/debug/app-debug.apk

# Verify APK contents (requires aapt or unzip)
unzip -l app/build/outputs/apk/debug/app-debug.apk | grep MainActivity
```

Expected:
- APK file exists
- Size is typically 2-5 MB for a basic app
- Contains compiled classes and resources

### 5. Install to Physical Device

```bash
# Connect Android device via USB
# Enable USB debugging on device

# Install APK
./scripts/install.sh

# Or directly
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Expected:
- APK installs successfully
- App appears in device's app drawer as "CleanCal"

### 6. Launch App on Device

```bash
# Launch the app
adb shell am start -n com.cleancal/.MainActivity

# Or manually open from device
```

Expected:
- App launches without crashes
- Shows "Welcome to CleanCal" text centered on screen
- White background, black text

### 7. Build Emulator Docker Image

```bash
# Build emulator environment
docker compose build emulator

# Verify image was created
docker images | grep cleancal-emulator
```

Expected: `cleancal-emulator` image appears in the list

### 8. Run Emulator

```bash
# Start emulator (runs in foreground)
./scripts/run-emulator.sh

# Or using make
make run-emulator

# Or directly
docker compose up emulator
```

Expected:
- Emulator container starts
- VNC server starts on port 5900
- Android system boots (may take 5-10 minutes first time)
- APK is automatically installed
- App launches automatically

### 9. Connect to Emulator UI

```bash
# In a new terminal, connect via VNC
vncviewer localhost:5900

# Or use any VNC client:
# - Remmina (Linux)
# - TigerVNC (Cross-platform)
# - RealVNC (Cross-platform)
```

Expected:
- VNC connection succeeds
- Android home screen is visible
- CleanCal app is running with "Welcome to CleanCal" text

### 10. Clean Build Test

```bash
# Clean all build artifacts
make clean

# Or manually
rm -rf app/build .gradle build

# Rebuild
make build
```

Expected:
- Clean completes successfully
- Rebuild works without errors

## Common Issues and Solutions

### Build Fails: "Plugin not found"

**Cause**: Network issues or restricted access to Maven repositories

**Solution**:
- Ensure internet connectivity
- Check firewall/proxy settings
- Verify access to https://google.com and https://maven.google.com

### Build Fails: SSL/Certificate Errors

**Cause**: Docker image doesn't have updated CA certificates

**Solution**:
- Rebuild Docker image with `--no-cache` flag
- Check system time is correct

### Emulator Won't Start

**Cause**: KVM not available

**Solution**:
- Enable virtualization in BIOS
- Load KVM kernel module: `sudo modprobe kvm`
- Emulator will run in software mode (slower) without KVM

### VNC Connection Fails

**Cause**: Port 5900 already in use or emulator not fully started

**Solution**:
- Check port: `netstat -an | grep 5900`
- Wait longer for emulator to start
- Check Docker logs: `docker logs <container-id>`

### APK Install Fails: "Device not found"

**Cause**: Device not connected or USB debugging disabled

**Solution**:
- Enable USB debugging on device
- Check connection: `adb devices`
- Install udev rules for Android devices

## Performance Benchmarks

Expected build times (approximate):

- **First build**: 5-15 minutes (downloading dependencies)
- **Incremental build**: 30-60 seconds
- **Clean build**: 2-5 minutes

Expected emulator boot times:

- **First boot**: 5-10 minutes
- **Subsequent boots**: 2-3 minutes

## Success Criteria

The framework is working correctly if:

1. ✅ Docker images build without errors
2. ✅ APK builds successfully
3. ✅ APK can be installed on a device
4. ✅ App launches and displays UI correctly
5. ✅ Emulator starts and runs the app
6. ✅ VNC connection shows the app UI

## Validation Checklist

- [ ] Project structure is correct
- [ ] All XML files are valid
- [ ] Kotlin source compiles
- [ ] Docker build image creates successfully
- [ ] APK builds in Docker
- [ ] APK installs on physical device
- [ ] App runs on physical device
- [ ] Emulator Docker image creates successfully
- [ ] Emulator boots and runs app
- [ ] VNC connection works
- [ ] Clean and rebuild works

## Automated Testing Script

```bash
#!/bin/bash
# Quick validation script

echo "=== CleanCal Framework Validation ==="

echo "1. Checking project structure..."
test -f build.gradle.kts && test -f gradlew && echo "✓ Project files present" || exit 1

echo "2. Validating XML files..."
python3 -c "
import xml.etree.ElementTree as ET
import glob
files = glob.glob('app/**/*.xml', recursive=True)
for f in files:
    ET.parse(f)
" && echo "✓ All XML files valid" || exit 1

echo "3. Building Docker image..."
docker compose build build > /dev/null 2>&1 && echo "✓ Docker image built" || exit 1

echo "4. Building APK..."
docker compose run --rm build ./gradlew assembleDebug > /tmp/build.log 2>&1
if [ -f app/build/outputs/apk/debug/app-debug.apk ]; then
    echo "✓ APK built successfully"
else
    echo "✗ APK build failed"
    tail -50 /tmp/build.log
    exit 1
fi

echo ""
echo "=== All checks passed! ==="
echo "APK location: app/build/outputs/apk/debug/app-debug.apk"
```

Save this as `test.sh`, make it executable with `chmod +x test.sh`, and run it to validate the framework.
