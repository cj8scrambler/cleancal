#!/bin/bash

# Run emulator using Docker
set -e

echo "Starting Android Emulator in Docker..."

# Check if KVM is available
KVM_AVAILABLE=false
if [ -e /dev/kvm ]; then
    echo "KVM detected - emulator will use hardware acceleration."
    KVM_AVAILABLE=true
else
    echo "WARNING: /dev/kvm not found. Emulator will run in software mode (slower)."
    echo "For better performance, ensure KVM is enabled on your system."
fi

# Build the emulator image
docker compose build emulator

# Run the emulator
echo ""
echo "Starting emulator container..."
echo "This may take several minutes on first run..."
echo ""

# Create a temporary override file to add KVM device if available
if [ "$KVM_AVAILABLE" = true ]; then
    # Create a unique temporary file with .yml suffix
    TEMP_COMPOSE=$(mktemp --suffix=.yml)
    
    # Set up cleanup trap to ensure file is removed on exit
    trap "rm -f '$TEMP_COMPOSE'" EXIT INT TERM
    
    cat > "$TEMP_COMPOSE" << 'EOF'
services:
  emulator:
    devices:
      - /dev/kvm:/dev/kvm
EOF
    docker compose -f docker-compose.yml -f "$TEMP_COMPOSE" up emulator
else
    docker compose up emulator
fi

echo ""
echo "Emulator stopped."
