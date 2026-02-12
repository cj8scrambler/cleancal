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

**Important:** Since the Docker container is ephemeral (removed after each run with `--rm`), we need to store the keystore in the mounted `/workspace` directory so it persists between container runs.

**Using the Docker build container** (recommended for this project):

**Step 1:** Generate the debug keystore in the workspace directory:
```bash
docker compose run --rm build keytool -genkey -v -keystore /workspace/debug.keystore -storepass android -keypass android -alias androiddebugkey -dname "CN=Android Debug,O=Android,C=US" -keyalg RSA -keysize 2048 -validity 10000
```

This creates a `debug.keystore` file in your project root (which is mounted as `/workspace` in the container).

**Step 2:** Get the SHA-1 fingerprint from the keystore:
```bash
docker compose run --rm build keytool -list -v -keystore /workspace/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

Look for the "SHA1" line in the output and copy the fingerprint.

**Important Notes:**
- The keystore file `debug.keystore` will be created in your project root directory
- You should add `debug.keystore` to your `.gitignore` file to avoid committing it to version control
- This keystore is only for development/testing OAuth setup
- For production builds, use a proper release keystore stored securely

**Using local keytool** (if you have Android SDK installed locally):

For debug builds:
```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

For release builds, use your release keystore:
```bash
keytool -list -v -keystore /path/to/your/release.keystore -alias your_alias
```

5. Click "CREATE"
6. Your OAuth client will be created and you'll see the client details

### 5. Configure the OAuth Client ID in the App

After creating your OAuth 2.0 credentials, you need to configure the client ID in the CleanCal app.

**Important:** The OAuth client ID from step 4 must be added to the app's configuration for Google Sign-In to work properly.

#### Option 1: Update strings.xml (Recommended for developers)

1. Open `app/src/main/res/values/strings.xml` in your project
2. Find the line with `default_web_client_id`
3. Replace `YOUR_OAUTH_CLIENT_ID_HERE` with your actual OAuth 2.0 client ID
4. Example:
   ```xml
   <string name="default_web_client_id" translatable="false">123456789012-abcdefghijklmnopqrstuvwxyz123456.apps.googleusercontent.com</string>
   ```

#### Option 2: Using google-services.json (For Firebase users)

If you're also using Firebase, you can add `google-services.json` to your `app/` directory, and the app will automatically use the client ID from that file.

**After configuration:**
- Rebuild the app: `make build`
- The app will now properly persist your Google account after sign-in

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

### "OAuth client ID not configured" Error

**Problem:** When opening Settings, you see "OAuth client ID not configured" and the Connect button is disabled.

**Root Cause:** The app needs your OAuth 2.0 client ID to properly authenticate and persist your Google account.

**Solution:**
1. Complete steps 1-4 above to create OAuth credentials in Google Cloud Console
2. Copy your OAuth 2.0 client ID (it looks like: `123456789012-abc...xyz.apps.googleusercontent.com`)
3. Open `app/src/main/res/values/strings.xml`
4. Replace `YOUR_OAUTH_CLIENT_ID_HERE` with your actual client ID:
   ```xml
   <string name="default_web_client_id" translatable="false">YOUR_ACTUAL_CLIENT_ID_HERE</string>
   ```
5. Rebuild the app: `make build`
6. Reinstall on your device

### "Sign-in failed" or Account Not Persisting

**Problem:** Sign-in completes but the app shows "Not Connected" when you return to Settings.

**Possible causes:**
1. OAuth client ID not configured correctly in `strings.xml`
2. SHA-1 fingerprint mismatch between the keystore used to build the app and the one registered in Google Cloud Console
3. OAuth client not properly configured in Google Cloud Console

**Solutions:**

1. **Verify OAuth Client ID Configuration:**
   - Open `app/src/main/res/values/strings.xml`
   - Ensure `default_web_client_id` is set to your actual OAuth client ID
   - The client ID should be from the "Android" OAuth client type (not Web)

2. **Check Logcat for Detailed Errors:**
   ```bash
   # View logs from the app
   adb logcat | grep -E "GoogleAuthManager|SettingsActivity"
   ```
   Look for messages like:
   - "OAuth client ID not configured"
   - "Sign-in failed: [error code]"
   - "No user currently signed in"

3. **Verify SHA-1 Fingerprint:**
   - Get the SHA-1 from your current keystore:
     ```bash
     docker compose run --rm build keytool -list -v -keystore /workspace/debug.keystore -alias androiddebugkey -storepass android -keypass android
     ```
   - Compare with the SHA-1 registered in Google Cloud Console (APIs & Services > Credentials)
   - If they don't match, either:
     - Add the correct SHA-1 to your OAuth client in Google Cloud Console, OR
     - Use the keystore that matches the registered SHA-1

4. **Wait for Changes to Propagate:**
   - After updating OAuth credentials in Google Cloud Console, wait 5-10 minutes
   - Clear app data: `adb shell pm clear com.cleancal`
   - Try signing in again

### "Keystore file does not exist" Error

**Problem:** When running the keytool command, you get an error that the keystore file doesn't exist.

**Root Cause:** The Docker container is ephemeral (`--rm` flag removes it after each run). Any files created outside the mounted volumes (like `/root/.android/`) are deleted when the container stops.

**Solution:** Store the keystore in the mounted workspace directory so it persists:

1. **Generate the keystore in the workspace**:
   ```bash
   docker compose run --rm build keytool -genkey -v -keystore /workspace/debug.keystore -storepass android -keypass android -alias androiddebugkey -dname "CN=Android Debug,O=Android,C=US" -keyalg RSA -keysize 2048 -validity 10000
   ```

2. **Get the SHA-1 fingerprint**:
   ```bash
   docker compose run --rm build keytool -list -v -keystore /workspace/debug.keystore -alias androiddebugkey -storepass android -keypass android
   ```

The keystore file will be created in your project root directory and will persist between container runs.

### "Sign-in failed" Error

**Possible causes:**
1. OAuth client ID not configured in strings.xml
2. OAuth credentials not configured correctly
3. SHA-1 fingerprint mismatch
4. Google Calendar API not enabled

**Solutions:**
1. Ensure you've configured the OAuth client ID in `strings.xml` (see step 5 above)
2. Verify that the Google Calendar API is enabled in your Google Cloud project
3. Double-check that the package name in the OAuth client matches `com.cleancal`
4. Ensure the SHA-1 fingerprint matches the keystore you're using to build the app
5. Check logcat for detailed error messages: `adb logcat | grep GoogleAuthManager`
6. Wait a few minutes for changes to propagate (Google Cloud changes can take 5-10 minutes)

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
# Get SHA-1 fingerprint for debug keystore (stored in workspace to persist)
docker compose run --rm build keytool -list -v -keystore /workspace/debug.keystore -alias androiddebugkey -storepass android -keypass android

# Run any other Android SDK command
docker compose run --rm build <command>
```

**Important Container Notes:**
- The Docker container is ephemeral (`--rm` removes it after each run)
- Only files in `/workspace` (your project directory) persist between runs
- The `/root/.android/` directory is NOT mounted, so keystores there are deleted after each run
- Always store keystores in `/workspace/` (your project root) to persist them

### Debug Builds
- Store the debug keystore at `/workspace/debug.keystore` (in your project root)
- Add `debug.keystore` to `.gitignore` to avoid committing it
- Make sure to add the debug SHA-1 fingerprint to your OAuth client
- Use the commands above to generate and read the keystore

### Release Builds
- Use your release keystore's SHA-1 fingerprint
- Store release keystores securely outside the repository
- Never commit keystores or credentials to version control

### Multiple Developers
- Each developer can generate their own keystore using the commands above
- Each developer needs to add their keystore's SHA-1 fingerprint to the OAuth client
- Alternatively, share a common keystore file (stored securely, not in git) for consistent fingerprints

### Testing
- Use test accounts during development
- Consider creating a separate Google Cloud project for testing

## Additional Resources

- [Google Calendar API Documentation](https://developers.google.com/calendar)
- [Google Sign-In for Android](https://developers.google.com/identity/sign-in/android)
- [OAuth 2.0 for Mobile & Desktop Apps](https://developers.google.com/identity/protocols/oauth2/native-app)
