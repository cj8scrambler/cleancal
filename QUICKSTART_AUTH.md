# Quick Start: Google Calendar Authentication

This guide helps you quickly set up Google Calendar authentication for CleanCal.

## TL;DR

1. Create OAuth credentials in Google Cloud Console
2. Copy your OAuth client ID
3. Update `app/src/main/res/values/strings.xml` with your client ID
4. Rebuild and test

## Step-by-Step (5 Minutes)

### 1. Google Cloud Console Setup (3 minutes)

```bash
# Open in browser:
https://console.cloud.google.com/
```

1. Create new project: "CleanCal"
2. Enable Google Calendar API
3. Configure OAuth consent screen (External type)
4. Create OAuth 2.0 credentials:
   - Type: **Android**
   - Package: `com.cleancal`
   - SHA-1: Get from keystore (see below)

### 2. Get SHA-1 Fingerprint (1 minute)

```bash
# Generate keystore if needed
docker compose run --rm build keytool -genkey -v \
  -keystore /workspace/debug.keystore \
  -storepass android -keypass android \
  -alias androiddebugkey \
  -dname "CN=Android Debug,O=Android,C=US" \
  -keyalg RSA -keysize 2048 -validity 10000

# Get SHA-1 fingerprint
docker compose run --rm build keytool -list -v \
  -keystore /workspace/debug.keystore \
  -alias androiddebugkey \
  -storepass android -keypass android | grep SHA1
```

Copy the SHA-1 value and paste it into the OAuth client form.

### 3. Configure App (1 minute)

```bash
# Edit strings.xml
vi app/src/main/res/values/strings.xml

# Find this line:
<string name="default_web_client_id" translatable="false">YOUR_OAUTH_CLIENT_ID_HERE</string>

# Replace with your OAuth client ID from Google Cloud Console:
<string name="default_web_client_id" translatable="false">123456789012-abc...xyz.apps.googleusercontent.com</string>
```

### 4. Build and Test

```bash
# Build app
make build

# Check for your device
adb devices

# Verify OAuth configuration
adb logcat | grep GoogleAuthManager
```

## Verification

### ✅ Successful Setup:

When you open the app and go to Settings, you should see:
- "Connect Google Account" button is **enabled**
- No error messages about configuration

In logs:
```
D/GoogleAuthManager: Initializing Google Sign-In with client ID
D/GoogleAuthManager: No user currently signed in
```

### ❌ Configuration Issue:

If you see:
- "OAuth client ID not configured" message
- "Connect Google Account" button is **disabled**

In logs:
```
W/GoogleAuthManager: OAuth client ID not configured
```

**Solution:** You didn't update `strings.xml` with your actual OAuth client ID.

## Testing Authentication

1. Open CleanCal
2. Tap Settings (gear icon)
3. Tap "Connect Google Account"
4. Select your Google account
5. Grant calendar permissions
6. Return to Settings
7. Should see: "Connected: your@email.com"

## Common Issues

### "Sign-in failed: 12500"

**Cause:** SHA-1 fingerprint mismatch

**Fix:**
```bash
# Get your actual SHA-1
docker compose run --rm build keytool -list -v \
  -keystore /workspace/debug.keystore \
  -alias androiddebugkey \
  -storepass android -keypass android | grep SHA1

# Add it to your OAuth client in Google Cloud Console
# Wait 5-10 minutes for changes to propagate
```

### Button Stays Disabled

**Cause:** Client ID still says "YOUR_OAUTH_CLIENT_ID_HERE"

**Fix:** Actually update the value in strings.xml and rebuild

### Account Not Persisting

**Cause:** Using wrong type of OAuth client

**Fix:** Ensure you created an **Android** OAuth client (not Web)

## View Detailed Logs

```bash
# All auth-related logs
adb logcat | grep -E "GoogleAuth|Settings|SignIn"

# Just errors
adb logcat | grep -E "E/"

# Clear and watch fresh logs
adb logcat -c && adb logcat | grep GoogleAuth
```

## What Changed?

Previously, the app was missing `.requestIdToken(clientId)` in the sign-in configuration. This caused accounts to not persist even though sign-in completed successfully.

The fix requires you to:
1. Set up OAuth credentials (one-time)
2. Configure your client ID in the app (one-time)
3. Then authentication works properly

## Need More Help?

- **Full Setup Guide:** See `GOOGLE_SETUP.md`
- **Technical Details:** See `AUTHENTICATION_FIX.md`
- **Troubleshooting:** See GOOGLE_SETUP.md "Troubleshooting" section

## File Locations

```
app/src/main/res/values/strings.xml     # Update this with your OAuth client ID
app/src/main/java/com/cleancal/auth/    # Authentication code
GOOGLE_SETUP.md                          # Detailed setup instructions
AUTHENTICATION_FIX.md                    # Technical explanation of the fix
```

## Security Notes

- OAuth client ID is not secret (it's in the APK)
- Each developer should use their own OAuth credentials
- Don't commit keystores to git
- Use different OAuth clients for dev/prod

## Success Checklist

- [ ] Created Google Cloud project
- [ ] Enabled Google Calendar API
- [ ] Created OAuth consent screen
- [ ] Created Android OAuth client with correct package and SHA-1
- [ ] Copied OAuth client ID
- [ ] Updated strings.xml with actual client ID (not placeholder)
- [ ] Rebuilt app: `make build`
- [ ] Connect button is enabled (not grayed out)
- [ ] Can tap "Connect Google Account"
- [ ] Sign-in completes successfully
- [ ] Returns to Settings showing "Connected: email@example.com"
- [ ] Calendar data loads from Google Calendar

If all checkboxes are checked, your setup is complete! 🎉
