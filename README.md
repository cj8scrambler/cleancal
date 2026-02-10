# CleanCal

Android app with a simple clean view for electronic wall calendars.

## Overview

CleanCal is a Kotlin-based Android application designed for electronic wall calendars. The app provides a clean, minimalistic interface optimized for always-on display scenarios.

## Prerequisites

- Docker and Docker Compose (for containerized builds and emulation)
- For local installation: Android SDK with `adb` in PATH
- **Internet access required for first build** to download:
  - Gradle distribution
  - Android Gradle Plugin
  - Android SDK components
  - Project dependencies (AndroidX, Kotlin, etc.)

After the first successful build, subsequent builds can use cached dependencies.

## Project Structure

```
cleancal/
├── app/                          # Android application module
│   ├── src/main/
│   │   ├── java/com/cleancal/   # Kotlin source files
│   │   ├── res/                 # Android resources
│   │   └── AndroidManifest.xml  # App manifest
│   └── build.gradle.kts         # App-level Gradle build file
├── scripts/                      # Build and deployment scripts
│   ├── build.sh                 # Build APK using Docker
│   ├── install.sh               # Install APK to local device
│   ├── run-emulator.sh          # Run emulator in Docker
│   └── start-emulator.sh        # Emulator startup script (internal)
├── Dockerfile.build             # Docker image for building
├── Dockerfile.emulator          # Docker image for emulator
├── docker-compose.yml           # Docker Compose orchestration
├── build.gradle.kts             # Root build file
└── settings.gradle.kts          # Gradle settings

```

## Building the App

### Quick Start with Make

The easiest way to build and run the app:

```bash
# Build the APK
make build

# Install to connected device
make install

# Run emulator
make run-emulator

# Show all available commands
make help
```

### Using Docker (Recommended)

Build the APK using the provided Docker environment:

```bash
./scripts/build.sh
```

This will:
1. Build the Docker image with all necessary Android SDK components
2. Compile the Kotlin code
3. Generate the debug APK at `app/build/outputs/apk/debug/app-debug.apk`

### Manual Build (Without Docker)

If you have the Android SDK installed locally:

```bash
./gradlew assembleDebug
```

## Installing the App

### Install to Connected Device

Connect an Android device via USB with USB debugging enabled, then:

```bash
./scripts/install.sh
```

Or manually:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Running the Emulator

### Using Docker

Run the Android emulator in a Docker container:

```bash
./scripts/run-emulator.sh
```

The emulator will:
1. Start in a virtual X11 environment
2. Build the APK if not already built
3. Install and launch the app automatically
4. Expose a VNC server on port 5900 for UI access

### Accessing the Emulator UI

Connect to the emulator using a VNC client:

```bash
vncviewer localhost:5900
```

Or use any VNC client application and connect to `localhost:5900`.

**Note**: The first run may take several minutes as the emulator initializes the Android system.

### Hardware Acceleration

For better performance, ensure KVM is enabled on your Linux host:

```bash
# Check if KVM is available
ls /dev/kvm

# If not available, enable virtualization in BIOS and load KVM module
sudo modprobe kvm
sudo modprobe kvm_intel  # or kvm_amd for AMD processors
```

## Development

### Project Configuration

- **Language**: Kotlin
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Build Tools**: Gradle 8.2 with Gradle Wrapper
- **Android Gradle Plugin**: 8.2.0
- **Kotlin Version**: 1.9.20

### Key Dependencies

- AndroidX Core KTX
- AppCompat
- Material Design Components
- ConstraintLayout

### Clean Build

To clean and rebuild:

```bash
# Using Docker
docker compose run --rm build ./gradlew clean assembleDebug

# Locally
./gradlew clean assembleDebug
```

## Docker Containers

### Build Container

The build container (`Dockerfile.build`) includes:
- Ubuntu 22.04 base
- OpenJDK 17
- Android SDK Command Line Tools
- Platform Tools
- Android Platform 34
- Build Tools 34.0.0

### Emulator Container

The emulator container (`Dockerfile.emulator`) includes:
- All build container components
- Android Emulator
- X11 Virtual Frame Buffer (Xvfb)
- VNC Server for remote UI access
- System image for x86_64 emulation

### Troubleshooting

### Build Issues

- **Network restrictions**: If building in a restricted network environment, you may need to pre-download Android SDK components and Gradle dependencies
- Ensure Docker is running: `docker ps`
- Clear Gradle cache: `./gradlew clean`
- Rebuild Docker images: `docker compose build --no-cache`
- **Plugin not found errors**: The Android Gradle Plugin and dependencies need to be downloaded from Maven repositories. Ensure your environment has access to https://google.com and https://maven.google.com

### Emulator Issues

- **Emulator won't start**: Check KVM availability with `ls /dev/kvm`
- **Black screen**: Wait a few minutes for the emulator to fully boot
- **Can't connect via VNC**: Ensure port 5900 is not in use: `netstat -an | grep 5900`

### Installation Issues

- **Device not detected**: Check USB debugging is enabled on device
- **Permission denied**: Add udev rules for Android devices or run with sudo

## License

See LICENSE file for details.
