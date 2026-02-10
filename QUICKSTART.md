# CleanCal - Quick Start Guide

Get up and running with the CleanCal Android app in minutes!

## Prerequisites

- **Docker** and **Docker Compose** installed
- **Internet connection** (for first build)
- Optional: **adb** for device installation
- Optional: **VNC client** for emulator UI access

## Installation

### 1. Clone the Repository

```bash
git clone https://github.com/cj8scrambler/cleancal.git
cd cleancal
```

### 2. Build the App

Use any of these methods:

**Option A: Using Make (Recommended)**
```bash
make build
```

**Option B: Using Scripts**
```bash
./scripts/build.sh
```

**Option C: Using Docker Compose Directly**
```bash
docker compose run --rm build ./gradlew assembleDebug
```

**Expected Output:**
- Gradle downloads dependencies (first time only)
- Kotlin code compiles
- APK generated at `app/build/outputs/apk/debug/app-debug.apk`
- Build takes ~5-15 minutes first time, ~1 minute subsequently

## Usage

### Option 1: Install on Physical Device

1. Enable USB debugging on your Android device
2. Connect device via USB
3. Install the app:

```bash
make install
# or
./scripts/install.sh
```

4. Launch the app from your device

### Option 2: Run in Emulator

1. Start the emulator:

```bash
make run-emulator
# or
./scripts/run-emulator.sh
```

2. Wait for emulator to boot (5-10 minutes first time)

3. Connect via VNC:

```bash
vncviewer localhost:5900
# or use any VNC client: Remmina, TigerVNC, etc.
```

4. You should see the CleanCal app running with "Welcome to CleanCal" text

**Note**: The `run-emulator.sh` script (run on your host) automatically manages the emulator container, including running the internal `start-emulator.sh` script. You only need to use `run-emulator.sh`.

## Common Commands

```bash
make help              # Show all available commands
make build             # Build APK
make install           # Install to connected device
make run-emulator      # Start emulator
make clean             # Clean build artifacts
make docker-clean      # Remove Docker resources
```

## What You Get

After successful build:
- ✅ Android APK at `app/build/outputs/apk/debug/app-debug.apk`
- ✅ Installable on any Android 7.0+ device
- ✅ Runnable in Docker-based emulator
- ✅ Simple UI showing app name

## Troubleshooting

### Build Fails with "Plugin not found"

**Problem:** Can't download Android Gradle Plugin

**Solution:** Ensure internet access to:
- https://google.com
- https://maven.google.com
- https://services.gradle.org

### Emulator Won't Start

**Problem:** Emulator fails to start or runs very slowly

**Solution:**
- The emulator works without KVM but will be slower
- For better performance, enable KVM:
```bash
# Check if KVM is available
ls /dev/kvm

# If missing, enable in BIOS and load module
sudo modprobe kvm
```
- The run-emulator.sh script automatically detects and uses KVM when available

### Can't Connect via VNC

**Problem:** Port 5900 in use or emulator not ready

**Solution:**
- Wait longer (emulator boot takes time)
- Check if port is free: `netstat -an | grep 5900`
- Check Docker logs: `docker logs <container-id>`

### Device Not Found

**Problem:** adb can't see device

**Solution:**
- Enable USB debugging on device
- Check connection: `adb devices`
- Try different USB cable/port

## Next Steps

1. **Modify the UI**: Edit `app/src/main/res/layout/activity_main.xml`
2. **Change app name**: Edit `app/src/main/res/values/strings.xml`
3. **Add features**: Edit `app/src/main/java/com/cleancal/MainActivity.kt`
4. **Rebuild**: Run `make build` after changes

## Documentation

- **README.md** - Comprehensive documentation
- **TESTING.md** - Detailed testing procedures
- **IMPLEMENTATION_SUMMARY.md** - Technical overview

## Support

For issues or questions:
1. Check the troubleshooting section above
2. Review TESTING.md for detailed diagnostics
3. Check Docker logs for errors
4. Ensure all prerequisites are met

## Success Checklist

- [ ] Docker and Docker Compose installed
- [ ] Repository cloned
- [ ] `make build` completes successfully
- [ ] APK file exists at `app/build/outputs/apk/debug/app-debug.apk`
- [ ] App installs on device OR emulator runs
- [ ] App shows "Welcome to CleanCal" text

**Time Investment:** 
- First time: 15-30 minutes
- Subsequent builds: 1-5 minutes

Enjoy building with CleanCal! 🚀
