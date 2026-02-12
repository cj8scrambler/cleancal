package com.cleancal.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.cleancal.BuildConfig
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.calendar.CalendarScopes

class GoogleAuthManager(private val context: Context) {
    
    private val signInClient: GoogleSignInClient
    private val clientId: String
    
    init {
        // Get OAuth client ID from BuildConfig (read from local.properties at build time)
        clientId = BuildConfig.GOOGLE_OAUTH_CLIENT_ID
        
        Log.d(TAG, "=== GoogleAuthManager Initialization ===")
        Log.d(TAG, "Context: ${context.packageName}")
        
        // Check if client ID is configured
        if (clientId.isBlank()) {
            Log.e(TAG, "❌ OAuth client ID not configured!")
            Log.e(TAG, "Please add google.oauth.clientId to local.properties")
            Log.e(TAG, "See GOOGLE_SETUP.md for instructions on obtaining your OAuth client ID")
        } else {
            // Log partial client ID for debugging (hide most of it for security)
            val maskedClientId = if (clientId.length > 20) {
                "${clientId.substring(0, 10)}...${clientId.substring(clientId.length - 10)}"
            } else {
                "***"
            }
            Log.d(TAG, "✓ OAuth client ID configured: $maskedClientId")
            Log.d(TAG, "Client ID length: ${clientId.length} characters")
            Log.d(TAG, "Client ID format valid: ${clientId.contains(".apps.googleusercontent.com")}")
        }
        
        // Build sign-in options with OAuth client ID
        Log.d(TAG, "Building GoogleSignInOptions...")
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(clientId)  // This is critical for account persistence
            .requestScopes(Scope(CalendarScopes.CALENDAR_READONLY))
            .build()
        
        Log.d(TAG, "Creating GoogleSignInClient...")
        signInClient = GoogleSignIn.getClient(context, signInOptions)
        Log.d(TAG, "✓ GoogleSignInClient created successfully")
        
        // Log current sign-in status
        Log.d(TAG, "Checking for existing signed-in account...")
        val account = GoogleSignIn.getLastSignedInAccount(context)
        if (account != null) {
            Log.d(TAG, "✓ User already signed in: ${account.email}")
            Log.d(TAG, "  Account ID: ${account.id}")
            Log.d(TAG, "  Display name: ${account.displayName}")
            Log.d(TAG, "  ID token available: ${account.idToken != null}")
            Log.d(TAG, "  Server auth code: ${account.serverAuthCode}")
            
            val calendarScope = Scope(CalendarScopes.CALENDAR_READONLY)
            if (GoogleSignIn.hasPermissions(account, calendarScope)) {
                Log.d(TAG, "  ✓ Calendar permission granted")
            } else {
                Log.w(TAG, "  ❌ Calendar permission NOT granted")
            }
        } else {
            Log.d(TAG, "ℹ No user currently signed in")
        }
        Log.d(TAG, "=== Initialization Complete ===")
    }
    
    fun getSignInIntent(): Intent {
        Log.d(TAG, "=== Getting Sign-In Intent ===")
        Log.d(TAG, "Client ID configured: ${clientId.isNotBlank()}")
        val intent = signInClient.signInIntent
        Log.d(TAG, "Sign-in intent created successfully")
        return intent
    }
    
    fun getCurrentAccount(): GoogleSignInAccount? {
        Log.d(TAG, "=== Getting Current Account ===")
        val account = GoogleSignIn.getLastSignedInAccount(context)
        if (account == null) {
            Log.w(TAG, "❌ No account found")
            Log.d(TAG, "This could mean:")
            Log.d(TAG, "  1. User hasn't signed in yet")
            Log.d(TAG, "  2. OAuth client ID mismatch")
            Log.d(TAG, "  3. SHA-1 fingerprint not registered in Google Cloud Console")
            Log.d(TAG, "  4. Using wrong client ID type (need Android client ID)")
        } else {
            Log.d(TAG, "✓ Account found: ${account.email}")
            Log.d(TAG, "  Account details:")
            Log.d(TAG, "    Email: ${account.email}")
            Log.d(TAG, "    ID: ${account.id}")
            Log.d(TAG, "    Display name: ${account.displayName}")
            Log.d(TAG, "    ID token: ${if (account.idToken != null) "Present (${account.idToken?.take(20)}...)" else "NULL"}")
            Log.d(TAG, "    Server auth code: ${account.serverAuthCode ?: "NULL"}")
            Log.d(TAG, "    Granted scopes: ${account.grantedScopes.joinToString(", ") { it.scopeUri }}")
        }
        return account
    }
    
    fun isSignedIn(): Boolean {
        Log.d(TAG, "=== Checking Sign-In Status ===")
        val account = getCurrentAccount()
        val signedIn = account != null
        Log.d(TAG, "Result: ${if (signedIn) "✓ SIGNED IN" else "❌ NOT SIGNED IN"}")
        return signedIn
    }
    
    fun signOut(callback: () -> Unit) {
        Log.d(TAG, "=== Signing Out ===")
        Log.d(TAG, "Current account: ${getAccountEmail() ?: "None"}")
        signInClient.signOut().addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d(TAG, "✓ Sign out successful")
            } else {
                Log.e(TAG, "❌ Sign out failed: ${it.exception?.message}")
            }
            // Verify sign-out
            val stillSignedIn = getCurrentAccount() != null
            if (stillSignedIn) {
                Log.w(TAG, "⚠ Warning: Account still present after sign-out!")
            } else {
                Log.d(TAG, "✓ Account cleared successfully")
            }
            callback()
        }
    }
    
    fun getAccountEmail(): String? {
        return getCurrentAccount()?.email
    }
    
    fun hasCalendarPermission(): Boolean {
        Log.d(TAG, "=== Checking Calendar Permission ===")
        val account = getCurrentAccount()
        if (account == null) {
            Log.w(TAG, "❌ No account - cannot check permissions")
            return false
        }
        
        val calendarScope = Scope(CalendarScopes.CALENDAR_READONLY)
        val hasPermission = GoogleSignIn.hasPermissions(account, calendarScope)
        
        if (hasPermission) {
            Log.d(TAG, "✓ Calendar permission granted for ${account.email}")
        } else {
            Log.w(TAG, "❌ Calendar permission NOT granted for ${account.email}")
            Log.w(TAG, "User may need to grant permission again")
        }
        
        return hasPermission
    }
    
    fun isClientIdConfigured(): Boolean {
        val configured = clientId.isNotBlank()
        Log.d(TAG, "isClientIdConfigured: $configured")
        return configured
    }
    
    companion object {
        private const val TAG = "GoogleAuthManager"
        const val RC_SIGN_IN = 9001
    }
}
