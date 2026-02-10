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

```bash
make build
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
```

4. Launch the app from your device

### Option 2: Run in Emulator

1. Start the emulator:

```bash
make run-emulator
```

2. Wait for emulator to boot (5-10 minutes first time)

3. Connect via VNC:

```bash
gvncviewer localhost:0

```

**Important**: VNC clients interpret connection strings differently. If using `gvncviewer`, you MUST use `localhost::5900` (double colon).

## Common Commands

```bash
make help              # Show all available commands
make build             # Build APK
make install           # Install to connected device
make run-emulator      # Start emulator
make clean             # Clean build artifacts
make docker-clean      # Remove Docker resources
```
