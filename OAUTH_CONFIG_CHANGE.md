# OAuth Client ID Configuration Change

## Summary

Successfully moved OAuth client ID configuration from `strings.xml` (checked into git) to `local.properties` (gitignored) to keep credentials private.

## What Changed

### Files Modified

1. **app/build.gradle.kts**
   - Added code to read `google.oauth.clientId` from `local.properties`
   - Injects value as `BuildConfig.GOOGLE_OAUTH_CLIENT_ID` at build time
   - Enabled `buildConfig` feature
   - Defaults to empty string if not configured

2. **app/src/main/java/com/cleancal/auth/GoogleAuthManager.kt**
   - Changed from `context.getString(R.string.default_web_client_id)` 
   - To `BuildConfig.GOOGLE_OAUTH_CLIENT_ID`
   - Updated validation to check for blank (simpler)
   - Updated log messages to mention `local.properties`

3. **app/src/main/res/values/strings.xml**
   - Removed `default_web_client_id` string resource
   - No longer needed

4. **local.properties.example** (NEW)
   - Template file with instructions
   - Users copy to `local.properties` and add their OAuth client ID

5. **Documentation Updates**
   - GOOGLE_SETUP.md: Step 5 now uses local.properties
   - QUICKSTART_AUTH.md: Updated quick start guide
   - AUTHENTICATION_FIX.md: Documented the new approach

## How to Use

### Setup (One-time)

```bash
# 1. Copy template
cp local.properties.example local.properties

# 2. Edit and add your OAuth client ID
vi local.properties

# Add this line with your actual client ID:
google.oauth.clientId=123456789012-abcdefghijklmnopqrstuvwxyz123456.apps.googleusercontent.com

# 3. Build
make build
```

### What Gets Gitignored

```
local.properties      ✓ Already in .gitignore (line 9)
debug.keystore        ✓ Already in .gitignore (line 31)
*.keystore            ✓ Already in .gitignore (line 31)
```

## Benefits

✅ **Security**: OAuth credentials never committed to version control
✅ **Privacy**: Each developer uses their own credentials  
✅ **Standard**: Uses Android's standard `local.properties` pattern
✅ **Flexibility**: Easy to switch between different credentials
✅ **Build-time**: Injected at compile time as BuildConfig constant
✅ **Validation**: Build still succeeds without config (empty string default)

## Technical Details

### Build-time Injection

```kotlin
// In build.gradle.kts
val localProperties = java.util.Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

val googleOAuthClientId = localProperties.getProperty("google.oauth.clientId", "")

android {
    defaultConfig {
        buildConfigField("String", "GOOGLE_OAUTH_CLIENT_ID", "\"$googleOAuthClientId\"")
    }
}
```

### Runtime Access

```kotlin
// In GoogleAuthManager.kt
clientId = BuildConfig.GOOGLE_OAUTH_CLIENT_ID

if (clientId.isBlank()) {
    // Not configured - show error message
}
```

## Migration from Previous Version

If you had OAuth client ID in `strings.xml`:

```bash
# 1. Find your old client ID
grep default_web_client_id app/src/main/res/values/strings.xml

# 2. Create local.properties
cp local.properties.example local.properties

# 3. Add your client ID
echo "google.oauth.clientId=YOUR_CLIENT_ID" >> local.properties

# 4. Rebuild
make build
```

## Verification

### Check if Configured

```bash
# Should show empty if not configured
grep "google.oauth.clientId" local.properties

# Check logs when app starts
adb logcat | grep GoogleAuthManager
# Should see: "OAuth client ID not configured" or "Initializing Google Sign-In"
```

### Build Verification

```bash
# Build should succeed with or without local.properties
make build

# Check BuildConfig was generated
find app/build -name "BuildConfig.java" -o -name "BuildConfig.kt" | head -1 | xargs grep GOOGLE_OAUTH_CLIENT_ID
```

## Security Notes

1. **OAuth Client ID is not secret**
   - It's embedded in the APK anyway
   - Can be extracted by decompiling
   - Still good practice to not commit to git

2. **Each environment should have its own credentials**
   - Development: Personal OAuth client
   - Staging: Staging OAuth client  
   - Production: Production OAuth client

3. **local.properties is gitignored by default**
   - Standard Android practice
   - Already in .gitignore on line 9
   - Safe to add credentials

## Files in .gitignore

```
local.properties          ← OAuth client ID goes here
debug.keystore            ← Debug signing key (generated)
*.keystore                ← Any keystore files
google-services.json      ← Google Services config (if using Firebase)
```

## Common Issues

### "OAuth client ID not configured" Error

**Cause:** `local.properties` doesn't exist or doesn't have `google.oauth.clientId`

**Fix:**
```bash
cp local.properties.example local.properties
vi local.properties
# Add: google.oauth.clientId=YOUR_ACTUAL_CLIENT_ID
make build
```

### Build fails with "property not found"

**Cause:** Should not happen - we default to empty string

**Check:**
```bash
# Verify build.gradle.kts has default:
grep 'getProperty.*google.oauth.clientId.*""' app/build.gradle.kts
```

### Changes not taking effect

**Cause:** Need to rebuild after changing local.properties

**Fix:**
```bash
make build  # Rebuilds and injects new value
```

## Commit Information

**Commit:** 96649c1
**Branch:** copilot/implement-google-authentication
**Date:** 2026-02-12

## Related Documentation

- **GOOGLE_SETUP.md** - Complete setup guide
- **QUICKSTART_AUTH.md** - 5-minute quick start
- **AUTHENTICATION_FIX.md** - Technical deep dive
- **local.properties.example** - Configuration template
