#!/bin/bash

# Run emulator using Docker
set -e

echo "Starting Android Emulator in Docker..."

# Check if KVM is available
if [ ! -e /dev/kvm ]; then
    echo "WARNING: /dev/kvm not found. Emulator will run in software mode (slower)."
    echo "For better performance, ensure KVM is enabled on your system."
fi

# Build the emulator image
docker-compose build emulator

# Run the emulator
echo ""
echo "Starting emulator container..."
echo "This may take several minutes on first run..."
echo ""

docker-compose up emulator

echo ""
echo "Emulator stopped."
