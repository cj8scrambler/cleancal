# Google Authentication Persistence Fix

## Problem Summary

**Issue:** Google Sign-In flow completed successfully, but the app showed "Not Connected" when returning to Settings. Calendar data was not loaded from Google Calendar.

**Root Cause:** The `GoogleSignInOptions` was missing `.requestIdToken(clientId)`, which is required for Google Sign-In to properly persist the signed-in account. Without this, `GoogleSignIn.getLastSignedInAccount()` returns `null` even after successful authentication.

## What Was Fixed

### 1. Moved OAuth Client ID to local.properties

**File:** `local.properties` (gitignored)
```properties
# Google OAuth 2.0 Client ID for Calendar API
google.oauth.clientId=YOUR_OAUTH_CLIENT_ID_HERE
```

**Previously:** OAuth client ID was stored in `strings.xml` (checked into git)
**Now:** OAuth client ID is stored in `local.properties` (gitignored, private)

### 2. Updated Build Configuration

**File:** `app/build.gradle.kts`

**Key changes:**
- Reads OAuth client ID from `local.properties` at build time
- Injects it as `BuildConfig.GOOGLE_OAUTH_CLIENT_ID`
- Enables BuildConfig feature

**Code:**
```kotlin
// Load local.properties file
val localProperties = java.util.Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

// Get OAuth client ID from local.properties
val googleOAuthClientId = localProperties.getProperty("google.oauth.clientId", "")

android {
    defaultConfig {
        // Inject OAuth client ID as BuildConfig field
        buildConfigField("String", "GOOGLE_OAUTH_CLIENT_ID", "\"$googleOAuthClientId\"")
    }
    
    buildFeatures {
        buildConfig = true  // Enable BuildConfig generation
    }
}
```

### 3. Updated GoogleAuthManager

**File:** `app/src/main/java/com/cleancal/auth/GoogleAuthManager.kt`

**Key changes:**
- Changed from reading string resources to reading `BuildConfig`
- **Critical fix:** Still uses `.requestIdToken(clientId)` for account persistence
- Updated validation to check for blank client ID

**Before:**
```kotlin
clientId = context.getString(R.string.default_web_client_id)
if (clientId == "YOUR_OAUTH_CLIENT_ID_HERE" || clientId.isBlank()) {
    // Not configured
}
```

**After:**
```kotlin
clientId = BuildConfig.GOOGLE_OAUTH_CLIENT_ID
if (clientId.isBlank()) {
    // Not configured
}
```

The core fix remains the same:
```kotlin
val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
    .requestEmail()
    .requestIdToken(clientId)  // This is critical for account persistence
    .requestScopes(Scope(CalendarScopes.CALENDAR_READONLY))
    .build()
```

### 4. Created Template File

**File:** `local.properties.example`

A template file that users can copy to create their own `local.properties`:
```properties
google.oauth.clientId=YOUR_OAUTH_CLIENT_ID_HERE
```

### 5. Updated Documentation

**Files:** `GOOGLE_SETUP.md`, `QUICKSTART_AUTH.md`

**Changes:**
- Updated step 5 to use `local.properties` instead of `strings.xml`
- Clear instructions on copying template and adding OAuth client ID
- Emphasized that `local.properties` is gitignored
- Updated troubleshooting sections

## How to Configure

### For Developers

1. **Complete Google Cloud Setup** (Steps 1-4 in GOOGLE_SETUP.md):
   - Create Google Cloud project
   - Enable Google Calendar API
   - Configure OAuth consent screen
   - Create OAuth 2.0 credentials (Android type)

2. **Copy Your OAuth Client ID**:
   - From Google Cloud Console > APIs & Services > Credentials
   - It looks like: `123456789012-abcdefghijklmnopqrstuvwxyz123456.apps.googleusercontent.com`

3. **Configure local.properties**:
   ```bash
   # Copy the template
   cp local.properties.example local.properties
   
   # Edit the file
   vi local.properties
   
   # Add your OAuth client ID
   google.oauth.clientId=YOUR_ACTUAL_CLIENT_ID_HERE
   ```

4. **Rebuild the App**:
   ```bash
   make build
   ```

5. **Test**:
   - Install app on device
   - Open Settings
   - Tap "Connect Google Account"
   - Complete sign-in
   - Return to Settings - should show "Connected: your@email.com"

## Diagnostic Logging

The fix includes comprehensive logging to help diagnose issues:

### View Logs:
```bash
# All authentication logs
adb logcat | grep -E "GoogleAuthManager|SettingsActivity"

# Just sign-in events
adb logcat | grep "Sign-in"
```

### Log Messages:

**Successful configuration:**
```
D/GoogleAuthManager: Initializing Google Sign-In with client ID
D/GoogleAuthManager: No user currently signed in
```

**Missing configuration:**
```
W/GoogleAuthManager: OAuth client ID not configured. Please update default_web_client_id in strings.xml
W/GoogleAuthManager: See GOOGLE_SETUP.md for instructions on obtaining your OAuth client ID
```

**Successful sign-in:**
```
D/SettingsActivity: Sign-in result received: resultCode=-1
D/SettingsActivity: Sign-in successful: user@example.com
D/GoogleAuthManager: getCurrentAccount: Found account user@example.com
D/GoogleAuthManager: isSignedIn: true
```

**Sign-in failure:**
```
E/SettingsActivity: Sign-in failed: 12500 - Sign in failed
```

## Common Status Codes

- `12500`: Sign in failed - usually SHA-1 or OAuth config issue
- `12501`: Sign in cancelled by user
- `10`: Developer error - usually OAuth client ID mismatch

## Testing Without OAuth Credentials

If you want to test the app without setting up OAuth:
- The app will show: "OAuth client ID not configured"
- Connect button will be disabled
- App will use example calendar data
- No actual Google authentication possible

This is by design to prevent confusion and provide clear guidance.

## Why This Fix Works

### The Technical Details

**Without `.requestIdToken(clientId)`:**
1. User completes Google Sign-In flow
2. Account is added to Android's AccountManager
3. But Google Sign-In SDK doesn't associate account with the app
4. `GoogleSignIn.getLastSignedInAccount()` returns `null`
5. App thinks user is not signed in

**With `.requestIdToken(clientId)`:**
1. User completes Google Sign-In flow
2. Account is added to Android's AccountManager
3. Google Sign-In SDK requests ID token for the specific OAuth client
4. This creates a proper association between account and app
5. Account information is persisted in app's shared preferences
6. `GoogleSignIn.getLastSignedInAccount()` returns the account
7. App correctly recognizes signed-in state

### The ID Token

The ID token is a JWT (JSON Web Token) that:
- Proves the user's identity
- Is signed by Google
- Contains user information (email, name, etc.)
- Is specific to your OAuth client ID
- Enables offline access validation

## Security Considerations

### OAuth Client ID
- Not a secret (embedded in APK)
- Each app/environment should have its own
- Tied to package name and SHA-1 fingerprint
- Limits who can use the credentials

### Best Practices
- Don't commit keystores to version control
- Use different OAuth clients for dev/prod
- Rotate credentials periodically
- Monitor usage in Google Cloud Console

## Troubleshooting Guide

### Issue: Button Disabled, "OAuth client ID not configured"

**Solution:** You need to configure your OAuth client ID:
1. Get your client ID from Google Cloud Console
2. Update `strings.xml` with actual client ID
3. Rebuild app

### Issue: Sign-In Completes but Shows "Not Connected"

**Possible Causes:**
1. OAuth client ID not updated in strings.xml
2. SHA-1 fingerprint mismatch
3. Wrong type of OAuth client (should be Android, not Web)

**Solution:**
1. Check logcat: `adb logcat | grep GoogleAuthManager`
2. Verify client ID in strings.xml matches Google Cloud Console
3. Verify SHA-1 matches: `docker compose run --rm build keytool -list -v -keystore /workspace/debug.keystore -alias androiddebugkey -storepass android -keypass android`
4. Wait 5-10 minutes for Google Cloud changes to propagate

### Issue: Calendar Data Not Loading

**This could be:**
1. Authentication not working (see above)
2. Calendar API not enabled
3. No events in calendar for date range (±2 months)

**Check Logs:**
```bash
adb logcat | grep -E "GoogleAuth|Calendar|MainActivity"
```

## Additional Resources

- **Setup Guide:** `GOOGLE_SETUP.md`
- **Implementation Details:** `GOOGLE_AUTH_IMPLEMENTATION.md`
- **Google Sign-In Docs:** https://developers.google.com/identity/sign-in/android
- **OAuth 2.0 for Mobile:** https://developers.google.com/identity/protocols/oauth2/native-app

## Summary

The fix ensures that Google Sign-In properly persists authenticated accounts by:
1. Requiring OAuth client ID configuration
2. Requesting ID token during sign-in
3. Providing clear error messages when misconfigured
4. Adding diagnostic logging throughout
5. Offering comprehensive troubleshooting documentation

Users must configure their own OAuth credentials, but the process is now clearly documented with step-by-step instructions and troubleshooting guidance.
