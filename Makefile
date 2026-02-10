.PHONY: help build install run-emulator clean docker-build docker-clean

help:
	@echo "CleanCal - Android Build System"
	@echo ""
	@echo "Available targets:"
	@echo "  build          - Build the Android APK using Docker"
	@echo "  install        - Install APK to connected Android device"
	@echo "  run-emulator   - Run Android emulator in Docker with VNC access"
	@echo "  clean          - Clean build artifacts"
	@echo "  docker-build   - Build Docker images"
	@echo "  docker-clean   - Remove Docker images and volumes"
	@echo ""
	@echo "Requirements:"
	@echo "  - Docker and Docker Compose"
	@echo "  - Internet access for first build (to download dependencies)"
	@echo "  - adb for local device installation"

build:
	@echo "Building Android APK..."
	@./scripts/build.sh

install:
	@echo "Installing APK to device..."
	@./scripts/install.sh

run-emulator:
	@echo "Starting Android emulator..."
	@./scripts/run-emulator.sh

clean:
	@echo "Cleaning build artifacts..."
	@rm -rf app/build
	@rm -rf .gradle
	@rm -rf build

docker-build:
	@echo "Building Docker images..."
	@docker compose build

docker-clean:
	@echo "Removing Docker images and volumes..."
	@docker compose down -v
	@docker rmi cleancal-build cleancal-emulator 2>/dev/null || true
