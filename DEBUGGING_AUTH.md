# Debugging Google Authentication

This guide explains how to use the comprehensive logging added to diagnose Google authentication issues.

## Quick Start

After building and installing the app, view logs with:

```bash
# All authentication logs
adb logcat | grep -E "GoogleAuthManager|SettingsActivity"

# Only errors
adb logcat | grep -E "GoogleAuthManager|SettingsActivity" | grep "❌"

# From Docker container
docker exec -it cleancal-emulator-1 adb logcat | grep -E "GoogleAuthManager|SettingsActivity"
```

## Log Markers

The logs use visual markers for quick identification:
- ✓ Success operations
- ❌ Errors and failures  
- ⚠ Warnings
- ℹ Informational messages

## What Gets Logged

### On App Start (GoogleAuthManager initialization)

```
=== GoogleAuthManager Initialization ===
Context: com.cleancal
✓ OAuth client ID configured: 123456789...oogleusercontent.com
Client ID length: 72 characters
Client ID format valid: true
Checking for existing signed-in account...
✓ User already signed in: user@example.com
  Account ID: 123456789
  Display name: John Doe
  ID token available: true
  Server auth code: null
  ✓ Calendar permission granted
=== Initialization Complete ===
```

### On Sign-In Attempt

```
=== Connect Button Clicked ===
User not signed in - initiating sign in
=== Getting Sign-In Intent ===
Client ID configured: true
Sign-in intent created successfully
Launching sign-in intent...
```

### On Sign-In Result

#### Success Case:
```
=== Sign-In Result Received ===
Result code: -1
Expected RESULT_OK: -1
Match: true
Processing successful sign-in...
✓ Sign-in successful!
  Email: user@example.com
  Display name: John Doe
  ID: 123456789
  ID token: Present
  Server auth code: NULL
Verifying account persistence...
✓ Account successfully persisted: user@example.com
```

#### Failure Case (OAuth config issue):
```
=== Sign-In Result Received ===
Result code: -1
Expected RESULT_OK: -1
Match: true
Processing successful sign-in...
❌ Sign-in failed with ApiException
  Status code: 10
  Status message: DEVELOPER_ERROR
  Message: 10: 
Error 10: DEVELOPER_ERROR - Check OAuth client ID and SHA-1 configuration
```

#### Account Not Persisting:
```
✓ Sign-in successful!
  Email: user@example.com
  ...
Verifying account persistence...
❌ CRITICAL: Account NOT persisted!
This usually means:
  1. OAuth client ID doesn't match Google Cloud Console
  2. SHA-1 fingerprint not registered
  3. Wrong client ID type (need Android client)
```

## Common Error Patterns

### Error 10: DEVELOPER_ERROR

**Log shows:**
```
❌ Sign-in failed with ApiException
  Status code: 10
Error 10: DEVELOPER_ERROR - Check OAuth client ID and SHA-1 configuration
```

**Causes:**
1. OAuth client ID in `local.properties` doesn't match Google Cloud Console
2. SHA-1 fingerprint not registered in Google Cloud Console
3. Using Web client ID instead of Android client ID

**Fix:**
1. Get your SHA-1:
   ```bash
   docker compose run --rm build keytool -list -v -keystore /workspace/debug.keystore -alias androiddebugkey -storepass android -keypass android | grep SHA1
   ```

2. Go to Google Cloud Console → APIs & Credentials → OAuth 2.0 Client IDs
3. Find your **Android** client (not Web)
4. Verify SHA-1 is listed
5. Copy the client ID (format: `xxx.apps.googleusercontent.com`)
6. Update `local.properties`:
   ```properties
   google.oauth.clientId=YOUR_ANDROID_CLIENT_ID_HERE.apps.googleusercontent.com
   ```

7. Rebuild: `make build`

### Account Not Persisting (Sign-In Succeeds but Shows "Not Connected")

**Log shows:**
```
✓ Sign-in successful!
❌ CRITICAL: Account NOT persisted!
```

**Cause:** OAuth client ID mismatch

**Fix:** Same as Error 10 above

### Client ID Not Configured

**Log shows:**
```
❌ OAuth client ID not configured!
```

**Fix:**
1. Create `local.properties` from template:
   ```bash
   cp local.properties.example local.properties
   ```

2. Add your client ID:
   ```properties
   google.oauth.clientId=YOUR_CLIENT_ID_HERE
   ```

3. Rebuild: `make build`

### Client ID Format Invalid

**Log shows:**
```
Client ID format valid: false
```

**Cause:** Client ID doesn't end with `.apps.googleusercontent.com`

**Fix:** Make sure you're using the Android OAuth client ID from Google Cloud Console, not the Web client ID

## Verification Checklist

After configuring authentication, the logs should show:

On app start:
- ✓ OAuth client ID configured: [masked]
- ✓ Client ID length: 60-80 characters
- ✓ Client ID format valid: true

After successful sign-in:
- ✓ Sign-in successful!
- ✓ Account successfully persisted: [email]
- ✓ Calendar permission granted

If any of these show ❌, follow the troubleshooting steps above.

## Getting Help

If you're still having issues after following this guide:

1. Capture full logs:
   ```bash
   adb logcat > auth-debug.log
   ```

2. Try signing in

3. Stop the log capture (Ctrl+C)

4. Search the log for:
   - `GoogleAuthManager Initialization`
   - `Sign-In Result Received`
   - Any lines with ❌

5. Share the relevant log sections (remove any sensitive information)

## Additional Resources

- [GOOGLE_SETUP.md](GOOGLE_SETUP.md) - Complete setup guide
- [QUICKSTART_AUTH.md](QUICKSTART_AUTH.md) - 5-minute quick start
- [AUTHENTICATION_FIX.md](AUTHENTICATION_FIX.md) - Technical details
- [OAUTH_CONFIG_CHANGE.md](OAUTH_CONFIG_CHANGE.md) - Configuration guide
