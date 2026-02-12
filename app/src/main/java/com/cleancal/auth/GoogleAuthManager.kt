package com.cleancal.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.cleancal.R
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
        // Get OAuth client ID from string resources
        clientId = context.getString(R.string.default_web_client_id)
        
        // Check if client ID is configured
        if (clientId == "YOUR_OAUTH_CLIENT_ID_HERE" || clientId.isBlank()) {
            Log.w(TAG, "OAuth client ID not configured. Please update default_web_client_id in strings.xml")
            Log.w(TAG, "See GOOGLE_SETUP.md for instructions on obtaining your OAuth client ID")
        } else {
            Log.d(TAG, "Initializing Google Sign-In with client ID")
        }
        
        // Build sign-in options with OAuth client ID
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(clientId)  // This is critical for account persistence
            .requestScopes(Scope(CalendarScopes.CALENDAR_READONLY))
            .build()
        
        signInClient = GoogleSignIn.getClient(context, signInOptions)
        
        // Log current sign-in status
        val account = GoogleSignIn.getLastSignedInAccount(context)
        if (account != null) {
            Log.d(TAG, "User already signed in: ${account.email}")
            if (GoogleSignIn.hasPermissions(account, Scope(CalendarScopes.CALENDAR_READONLY))) {
                Log.d(TAG, "Calendar permission granted")
            } else {
                Log.w(TAG, "Calendar permission not granted")
            }
        } else {
            Log.d(TAG, "No user currently signed in")
        }
    }
    
    fun getSignInIntent(): Intent {
        Log.d(TAG, "Getting sign-in intent")
        return signInClient.signInIntent
    }
    
    fun getCurrentAccount(): GoogleSignInAccount? {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        if (account == null) {
            Log.d(TAG, "getCurrentAccount: No account found")
        } else {
            Log.d(TAG, "getCurrentAccount: Found account ${account.email}")
        }
        return account
    }
    
    fun isSignedIn(): Boolean {
        val signedIn = getCurrentAccount() != null
        Log.d(TAG, "isSignedIn: $signedIn")
        return signedIn
    }
    
    fun signOut(callback: () -> Unit) {
        Log.d(TAG, "Signing out")
        signInClient.signOut().addOnCompleteListener {
            Log.d(TAG, "Sign out complete")
            callback()
        }
    }
    
    fun getAccountEmail(): String? {
        return getCurrentAccount()?.email
    }
    
    fun hasCalendarPermission(): Boolean {
        val account = getCurrentAccount() ?: return false
        val hasPermission = GoogleSignIn.hasPermissions(
            account,
            Scope(CalendarScopes.CALENDAR_READONLY)
        )
        Log.d(TAG, "hasCalendarPermission: $hasPermission for ${account.email}")
        return hasPermission
    }
    
    fun isClientIdConfigured(): Boolean {
        return clientId != "YOUR_OAUTH_CLIENT_ID_HERE" && clientId.isNotBlank()
    }
    
    companion object {
        private const val TAG = "GoogleAuthManager"
        const val RC_SIGN_IN = 9001
    }
}
