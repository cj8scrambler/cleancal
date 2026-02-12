package com.cleancal

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.cleancal.auth.GoogleAuthManager
import com.cleancal.models.ViewType
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton

class SettingsActivity : AppCompatActivity() {

    private lateinit var spinner: Spinner
    private lateinit var btnConnectGoogle: MaterialButton
    private lateinit var txtAccountStatus: TextView
    private lateinit var authManager: GoogleAuthManager
    
    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d(TAG, "=== Sign-In Result Received ===")
        Log.d(TAG, "Result code: ${result.resultCode}")
        Log.d(TAG, "Expected RESULT_OK: ${Activity.RESULT_OK}")
        Log.d(TAG, "Match: ${result.resultCode == Activity.RESULT_OK}")
        
        if (result.resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "Processing successful sign-in...")
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d(TAG, "✓ Sign-in successful!")
                Log.d(TAG, "  Email: ${account?.email}")
                Log.d(TAG, "  Display name: ${account?.displayName}")
                Log.d(TAG, "  ID: ${account?.id}")
                Log.d(TAG, "  ID token: ${if (account?.idToken != null) "Present" else "NULL"}")
                Log.d(TAG, "  Server auth code: ${account?.serverAuthCode ?: "NULL"}")
                
                // Check if account is now persisted
                Log.d(TAG, "Verifying account persistence...")
                val persistedAccount = GoogleSignIn.getLastSignedInAccount(this)
                if (persistedAccount != null) {
                    Log.d(TAG, "✓ Account successfully persisted: ${persistedAccount.email}")
                } else {
                    Log.e(TAG, "❌ CRITICAL: Account NOT persisted!")
                    Log.e(TAG, "This usually means:")
                    Log.e(TAG, "  1. OAuth client ID doesn't match Google Cloud Console")
                    Log.e(TAG, "  2. SHA-1 fingerprint not registered")
                    Log.e(TAG, "  3. Wrong client ID type (need Android client)")
                }
                
                updateAccountStatus()
            } catch (e: ApiException) {
                Log.e(TAG, "❌ Sign-in failed with ApiException")
                Log.e(TAG, "  Status code: ${e.statusCode}")
                Log.e(TAG, "  Status message: ${e.statusMessage}")
                Log.e(TAG, "  Message: ${e.message}")
                Log.e(TAG, "  Localized message: ${e.localizedMessage}")
                
                // Provide helpful diagnostic information
                when (e.statusCode) {
                    10 -> Log.e(TAG, "Error 10: DEVELOPER_ERROR - Check OAuth client ID and SHA-1 configuration")
                    12500 -> Log.e(TAG, "Error 12500: SIGN_IN_REQUIRED - User needs to sign in")
                    12501 -> Log.e(TAG, "Error 12501: SIGN_IN_CANCELLED - User cancelled the sign-in")
                    else -> Log.e(TAG, "Unknown error code: ${e.statusCode}")
                }
                
                txtAccountStatus.text = "Sign-in failed: Error ${e.statusCode} - ${e.message}"
            }
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            Log.w(TAG, "⚠ Sign-in cancelled by user")
            txtAccountStatus.text = "Sign-in cancelled"
        } else {
            Log.w(TAG, "⚠ Unexpected result code: ${result.resultCode}")
            txtAccountStatus.text = "Sign-in failed with code: ${result.resultCode}"
        }
        Log.d(TAG, "=== Sign-In Result Processing Complete ===")
    }
    
    private val viewTypeOptions = listOf(
        "Two week view",
        "Month view",
        "Three day view",
        "One day view"
    )
    private val viewTypes = listOf(
        ViewType.TWO_WEEK,
        ViewType.MONTH,
        ViewType.THREE_DAY,
        ViewType.ONE_DAY
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize auth manager
        authManager = GoogleAuthManager(this)
        
        // Initialize views
        spinner = findViewById(R.id.defaultViewSpinner)
        btnConnectGoogle = findViewById(R.id.btnConnectGoogle)
        txtAccountStatus = findViewById(R.id.txtAccountStatus)

        // Setup default view spinner
        setupViewSpinner()
        
        // Setup Google sign-in
        setupGoogleSignIn()
    }
    
    private fun setupViewSpinner() {
        // Create adapter for spinner
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            viewTypeOptions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Load current preference
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedViewType = prefs.getString(PREF_DEFAULT_VIEW, ViewType.TWO_WEEK.name)

        // Set the spinner to the saved view type
        val savedIndex = viewTypes.indexOfFirst { it.name == savedViewType }
        if (savedIndex >= 0) {
            spinner.setSelection(savedIndex)
        }

        // Save preference when changed
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedViewType = viewTypes[position]
                prefs.edit().putString(PREF_DEFAULT_VIEW, selectedViewType.name).apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }
    
    private fun setupGoogleSignIn() {
        Log.d(TAG, "=== Setting Up Google Sign-In ===")
        
        // Check if OAuth client ID is configured
        if (!authManager.isClientIdConfigured()) {
            Log.e(TAG, "❌ OAuth client ID not configured")
            txtAccountStatus.text = "OAuth client ID not configured. See GOOGLE_SETUP.md for setup instructions."
            btnConnectGoogle.isEnabled = false
            return
        }
        
        Log.d(TAG, "✓ OAuth client ID is configured")
        
        // Enable the button
        btnConnectGoogle.isEnabled = true
        Log.d(TAG, "✓ Connect button enabled")
        
        // Update UI based on current sign-in state
        updateAccountStatus()
        
        // Set click listener
        btnConnectGoogle.setOnClickListener {
            Log.d(TAG, "=== Connect Button Clicked ===")
            if (authManager.isSignedIn()) {
                Log.d(TAG, "User is signed in - initiating sign out")
                // Sign out
                authManager.signOut {
                    Log.d(TAG, "Sign out callback executed")
                    updateAccountStatus()
                }
            } else {
                Log.d(TAG, "User not signed in - initiating sign in")
                // Sign in
                val signInIntent = authManager.getSignInIntent()
                Log.d(TAG, "Launching sign-in intent...")
                signInLauncher.launch(signInIntent)
            }
        }
        
        Log.d(TAG, "=== Google Sign-In Setup Complete ===")
    }
    
    private fun updateAccountStatus() {
        Log.d(TAG, "=== Updating Account Status UI ===")
        val isSignedIn = authManager.isSignedIn()
        Log.d(TAG, "Signed in: $isSignedIn")
        
        if (isSignedIn) {
            val email = authManager.getAccountEmail()
            Log.d(TAG, "Updating UI for signed-in user: $email")
            txtAccountStatus.text = "Connected: $email"
            btnConnectGoogle.text = getString(R.string.settings_disconnect_google)
            
            // Check calendar permission
            val hasPermission = authManager.hasCalendarPermission()
            if (!hasPermission) {
                Log.w(TAG, "⚠ Calendar permission not granted!")
                txtAccountStatus.text = "Connected: $email (Calendar access not granted)"
            }
        } else {
            Log.d(TAG, "Updating UI for disconnected state")
            txtAccountStatus.text = getString(R.string.settings_google_status_disconnected)
            btnConnectGoogle.text = getString(R.string.settings_connect_google)
        }
        
        Log.d(TAG, "=== Account Status UI Updated ===")
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
    
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "=== SettingsActivity Resumed ===")
        Log.d(TAG, "Updating account status after resume...")
        // Update account status when activity resumes
        updateAccountStatus()
    }

    companion object {
        private const val TAG = "SettingsActivity"
        const val PREFS_NAME = "CleanCalPrefs"
        const val PREF_DEFAULT_VIEW = "default_view"
    }
}
