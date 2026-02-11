# Google Calendar Integration Setup

This guide explains how to set up Google OAuth credentials to enable Google Calendar integration in CleanCal.

## Overview

CleanCal now supports Google Calendar integration, allowing users to:
- Authenticate with their Google account
- Access their Google Calendar data (read-only)
- Display real calendar events instead of example data

## Prerequisites

- A Google account
- Access to the [Google Cloud Console](https://console.cloud.google.com/)
- The CleanCal app installed on your Android device

## Setup Instructions

### 1. Create a Google Cloud Project

1. Go to the [Google Cloud Console](https://console.cloud.google.com/)
2. Click on "Select a project" at the top of the page
3. Click "NEW PROJECT"
4. Enter a project name (e.g., "CleanCal")
5. Click "CREATE"

### 2. Enable the Google Calendar API

1. In your Google Cloud project, go to "APIs & Services" > "Library"
2. Search for "Google Calendar API"
3. Click on "Google Calendar API"
4. Click "ENABLE"

### 3. Configure OAuth Consent Screen

1. Go to "APIs & Services" > "OAuth consent screen"
2. Select "External" user type and click "CREATE"
3. Fill in the required fields:
   - App name: `CleanCal`
   - User support email: Your email address
   - Developer contact information: Your email address
4. Click "SAVE AND CONTINUE"
5. On the "Scopes" page, click "ADD OR REMOVE SCOPES"
6. Search for and add:
   - `Google Calendar API` > `../auth/calendar.readonly`
7. Click "UPDATE" and then "SAVE AND CONTINUE"
8. On the "Test users" page (optional), add any test users if needed
9. Click "SAVE AND CONTINUE"
10. Review the summary and click "BACK TO DASHBOARD"

### 4. Create OAuth 2.0 Credentials

1. Go to "APIs & Services" > "Credentials"
2. Click "CREATE CREDENTIALS" > "OAuth client ID"
3. Select "Android" as the application type
4. Fill in the required fields:
   - Name: `CleanCal Android Client`
   - Package name: `com.cleancal`
   - SHA-1 certificate fingerprint: See below for how to obtain this

#### Getting Your SHA-1 Certificate Fingerprint

**Important:** The debug keystore is automatically created the first time you build a debug APK. If you haven't built the app yet, build it first:

```bash
# Build the app (this creates the debug keystore)
make build
# OR
./scripts/build.sh
```

**Using the Docker build container** (recommended for this project):

After building, run this command from the project root to get the SHA-1 fingerprint:
```bash
docker compose run --rm build keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

**Alternative: Generate keystore without full build**

If you just want to generate the keystore without doing a full build:
```bash
docker compose run --rm build keytool -genkey -v -keystore ~/.android/debug.keystore -storepass android -keypass android -alias androiddebugkey -dname "CN=Android Debug,O=Android,C=US" -keyalg RSA -keysize 2048 -validity 10000
```

Then get the fingerprint:
```bash
docker compose run --rm build keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

**Using local keytool** (if you have Android SDK installed locally):

For debug builds (after building the app or generating the keystore):
```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

For release builds, use your release keystore:
```bash
keytool -list -v -keystore /path/to/your/release.keystore -alias your_alias
```

Look for the "SHA1" line in the output and copy the fingerprint.

5. Click "CREATE"
6. Your OAuth client will be created and you'll see the client details

### 5. No Additional Configuration Needed

The CleanCal app is already configured to use the Google Sign-In SDK and will automatically use your OAuth credentials once they are set up in the Google Cloud Console.

**Important:** The OAuth client ID is associated with your app's package name (`com.cleancal`) and SHA-1 certificate fingerprint. The app will automatically discover and use these credentials.

## Using Google Calendar in CleanCal

### First-Time Authentication

1. Open the CleanCal app on your Android device
2. Tap the "Settings" button (gear icon)
3. In the "Google Account" section, tap "Connect Google Account"
4. You'll be redirected to Google's sign-in page
5. Select your Google account
6. Review and approve the permissions (read-only calendar access)
7. You'll be redirected back to CleanCal

### Viewing Your Calendar

Once authenticated:
- The app will automatically load your Google Calendar events
- Events from your primary calendar will be displayed in all views
- The app will categorize events based on keywords in their titles:
  - Work: meetings, calls, standup
  - Personal: general events
  - Birthday: events with "birthday" in the title
  - Reminder: tasks or reminders
  - Holiday: holiday events

### Disconnecting Your Account

1. Open the CleanCal app
2. Go to Settings
3. Tap "Disconnect Google Account"
4. The app will return to showing example events

## Troubleshooting

### "Keystore file does not exist" Error

**Problem:** When running the keytool command, you get an error that the keystore file doesn't exist.

**Solution:** The debug keystore is automatically created when you first build a debug APK. You have two options:

1. **Build the app first** (recommended):
   ```bash
   make build
   ```
   Then run the keytool command to get the SHA-1 fingerprint.

2. **Generate the keystore manually**:
   ```bash
   docker compose run --rm build keytool -genkey -v -keystore ~/.android/debug.keystore -storepass android -keypass android -alias androiddebugkey -dname "CN=Android Debug,O=Android,C=US" -keyalg RSA -keysize 2048 -validity 10000
   ```
   Then get the fingerprint with the original keytool command.

### "Sign-in failed" Error

**Possible causes:**
1. OAuth credentials not configured correctly
2. SHA-1 fingerprint mismatch
3. Google Calendar API not enabled

**Solutions:**
1. Verify that the Google Calendar API is enabled in your Google Cloud project
2. Double-check that the package name in the OAuth client matches `com.cleancal`
3. Ensure the SHA-1 fingerprint matches the keystore you're using to build the app
4. Wait a few minutes for changes to propagate (Google Cloud changes can take 5-10 minutes)

### No Events Displayed

**Possible causes:**
1. Your Google Calendar has no events in the date range (2 months before to 2 months after today)
2. Permission not granted for calendar access
3. Network connectivity issues

**Solutions:**
1. Check that you have events in your Google Calendar
2. Try disconnecting and reconnecting your account
3. Verify that you granted calendar permissions during sign-in
4. Check your internet connection

### Events Not Updating

The app loads calendar events when:
- You first open the app (if signed in)
- You return to the app from settings

To force a refresh:
1. Go to Settings
2. Go back to the main calendar view

## Security and Privacy

- CleanCal only requests read-only access to your Google Calendar
- Calendar data is fetched on-demand and not stored permanently on the device
- Your credentials are managed securely by the Google Sign-In SDK
- You can revoke CleanCal's access at any time through your [Google Account settings](https://myaccount.google.com/permissions)

## Permissions Required

- `android.permission.INTERNET`: Required to communicate with Google's servers
- `android.permission.GET_ACCOUNTS`: Required to access the list of accounts on the device

## API Scopes

CleanCal requests the following OAuth scope:
- `https://www.googleapis.com/auth/calendar.readonly`: Read-only access to Google Calendar

## Development Notes

For developers building and testing CleanCal:

### Using Docker Build Container

Since this project uses Docker for builds, you can run any Android SDK tool (including keytool) within the build container:

```bash
# Get SHA-1 fingerprint for debug keystore
docker compose run --rm build keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android

# Run any other Android SDK command
docker compose run --rm build <command>
```

### Debug Builds
- The debug build uses the debug keystore at `~/.android/debug.keystore` inside the Docker container
- Make sure to add the debug SHA-1 fingerprint to your OAuth client
- Use the Docker command above to get the fingerprint

### Release Builds
- Use your release keystore's SHA-1 fingerprint
- Never commit keystores or credentials to version control

### Multiple Developers
- Each developer needs to add their debug keystore's SHA-1 fingerprint to the OAuth client
- When using Docker, all developers will share the same debug keystore (inside the container)
- This simplifies team development as everyone uses the same fingerprint

### Testing
- Use test accounts during development
- Consider creating a separate Google Cloud project for testing

## Additional Resources

- [Google Calendar API Documentation](https://developers.google.com/calendar)
- [Google Sign-In for Android](https://developers.google.com/identity/sign-in/android)
- [OAuth 2.0 for Mobile & Desktop Apps](https://developers.google.com/identity/protocols/oauth2/native-app)
