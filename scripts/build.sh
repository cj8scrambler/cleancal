#!/bin/bash

# Build APK using Docker
set -e

echo "Building Android APK using Docker..."

# Build the Docker image
docker compose build build

# Run the build
docker compose run --rm build

echo ""
echo "Build complete!"
echo "APK location: app/build/outputs/apk/debug/app-debug.apk"
