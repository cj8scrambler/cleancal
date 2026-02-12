# CleanCal

Android app with a simple clean view for electronic wall calendars.

## Overview

CleanCal is a Kotlin-based Android application designed for electronic wall calendars. The app provides a clean, minimalistic interface optimized for always-on display scenarios.

## Features

- **Multiple Calendar Views**: Two-week, month, three-day, and single-day views
- **Google Calendar Integration**: Connect your Google account to display real calendar events (read-only)
- **Swipe Navigation**: Easy navigation between time periods
- **Color-Coded Events**: Automatic categorization of events (work, personal, birthdays, reminders, holidays)
- **Full-Screen Mode**: Optimized for wall-mounted tablets
- **Always-On Display**: Keep screen awake for continuous display

## Google Calendar Setup

CleanCal supports Google Calendar integration, allowing you to display real calendar events instead of example data.

### Quick Start (5 minutes)

1. **Create OAuth Credentials**: Follow [QUICKSTART_AUTH.md](QUICKSTART_AUTH.md) for a fast setup
2. **Update Configuration**: Add your OAuth client ID to `app/src/main/res/values/strings.xml`
3. **Build and Test**: `make build` and connect your Google account

### Documentation

- **[QUICKSTART_AUTH.md](QUICKSTART_AUTH.md)**: 5-minute quick setup guide
- **[GOOGLE_SETUP.md](GOOGLE_SETUP.md)**: Complete step-by-step instructions
- **[AUTHENTICATION_FIX.md](AUTHENTICATION_FIX.md)**: Technical details and troubleshooting

### Important Notes

- **OAuth client ID required**: You must configure your own OAuth 2.0 client ID from Google Cloud Console
- **One-time setup**: Configuration is needed once per development environment
- **Read-only access**: App only requests calendar read permissions

See the documentation above for detailed setup instructions.

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

