#!/bin/bash

# Check VNC connection script
set -e

echo "Checking emulator container and VNC connectivity..."
echo ""

# Check if container is running
CONTAINER_ID=$(docker ps --filter "ancestor=cleancal-emulator:latest" --format "{{.ID}}" | head -1)

if [ -z "$CONTAINER_ID" ]; then
    echo "❌ ERROR: Emulator container is not running"
    echo ""
    echo "Start the emulator first:"
    echo "  ./scripts/run-emulator.sh"
    exit 1
fi

echo "✓ Container is running: $CONTAINER_ID"

# Check port mapping
PORT_MAP=$(docker port "$CONTAINER_ID" 5900 2>/dev/null || echo "")
if [ -z "$PORT_MAP" ]; then
    echo "❌ ERROR: Port 5900 is not mapped"
    exit 1
fi
echo "✓ Port 5900 is mapped to: $PORT_MAP"

# Check if VNC process is running in container
VNC_RUNNING=$(docker exec "$CONTAINER_ID" pgrep -x x11vnc 2>/dev/null || echo "")
if [ -z "$VNC_RUNNING" ]; then
    echo "❌ ERROR: VNC server is not running inside container"
    echo ""
    echo "Check container logs:"
    echo "  docker logs $CONTAINER_ID"
    exit 1
fi
echo "✓ VNC server is running (PID: $VNC_RUNNING)"

# Try to check if port is listening
echo ""
echo "Attempting to test VNC connection..."
if command -v nc &> /dev/null; then
    if nc -zv localhost 5900 2>&1 | grep -q succeeded; then
        echo "✓ Port 5900 is accessible"
    else
        echo "⚠ Warning: Could not connect to port 5900"
    fi
else
    echo "ℹ Install 'netcat' to test port connectivity"
fi

echo ""
echo "================================"
echo "VNC Connection Information:"
echo "================================"
echo "Host: localhost"
echo "Port: 5900"
echo "Display: :0"
echo ""
echo "Connect with:"
echo "  vncviewer localhost:5900"
echo "  or"
echo "  gvncviewer localhost:5900"
echo ""
