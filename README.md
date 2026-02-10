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
│   ├── build.sh                 # Build APK using Docker (host)
│   ├── install.sh               # Install APK to local device (host)
│   ├── run-emulator.sh          # Run emulator in Docker (host)
│   ├── check-vnc.sh             # Check VNC connectivity (host)
│   └── start-emulator.sh        # Emulator initialization (container)
├── Dockerfile.build             # Docker image for building
├── Dockerfile.emulator          # Docker image for emulator
├── docker-compose.yml           # Docker Compose orchestration
├── build.gradle.kts             # Root build file
└── settings.gradle.kts          # Gradle settings

```

## Scripts Overview

The project includes two types of scripts for different purposes:

### Host Scripts (Run on your machine)

- **`scripts/build.sh`** - Builds the Android APK using Docker
- **`scripts/install.sh`** - Installs the APK to a connected Android device
- **`scripts/run-emulator.sh`** - Starts the Android emulator in a Docker container
  - This is the main script you run from your host machine
  - Detects KVM availability and configures the container accordingly
  - Builds the Docker image and starts the emulator container

### Container Scripts (Run inside Docker)

- **`scripts/start-emulator.sh`** - Internal initialization script
  - Automatically executed inside the emulator container
  - Sets up Xvfb (virtual display)
  - Starts the VNC server
  - Launches the Android emulator
  - Installs and runs the app
  - You don't need to run this manually

## Building the App

### Quick Start with Make

See [QUICKSTART.md](QUICKSTART.md)

