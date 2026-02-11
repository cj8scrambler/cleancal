package com.cleancal.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.calendar.CalendarScopes

class GoogleAuthManager(private val context: Context) {
    
    private val signInClient: GoogleSignInClient
    
    init {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(CalendarScopes.CALENDAR_READONLY))
            .build()
        
        signInClient = GoogleSignIn.getClient(context, signInOptions)
    }
    
    fun getSignInIntent(): Intent {
        return signInClient.signInIntent
    }
    
    fun getCurrentAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }
    
    fun isSignedIn(): Boolean {
        return getCurrentAccount() != null
    }
    
    fun signOut(callback: () -> Unit) {
        signInClient.signOut().addOnCompleteListener {
            callback()
        }
    }
    
    fun getAccountEmail(): String? {
        return getCurrentAccount()?.email
    }
    
    fun hasCalendarPermission(): Boolean {
        val account = getCurrentAccount() ?: return false
        return GoogleSignIn.hasPermissions(
            account,
            Scope(CalendarScopes.CALENDAR_READONLY)
        )
    }
    
    companion object {
        const val RC_SIGN_IN = 9001
    }
}
