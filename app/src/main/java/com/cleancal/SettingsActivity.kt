package com.cleancal

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                task.getResult(ApiException::class.java)
                updateAccountStatus()
            } catch (e: ApiException) {
                // Sign-in failed
                txtAccountStatus.text = "Sign-in failed: ${e.statusCode}"
            }
        }
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
        // Enable the button
        btnConnectGoogle.isEnabled = true
        
        // Update UI based on current sign-in state
        updateAccountStatus()
        
        // Set click listener
        btnConnectGoogle.setOnClickListener {
            if (authManager.isSignedIn()) {
                // Sign out
                authManager.signOut {
                    updateAccountStatus()
                }
            } else {
                // Sign in
                val signInIntent = authManager.getSignInIntent()
                signInLauncher.launch(signInIntent)
            }
        }
    }
    
    private fun updateAccountStatus() {
        if (authManager.isSignedIn()) {
            val email = authManager.getAccountEmail()
            txtAccountStatus.text = "Connected: $email"
            btnConnectGoogle.text = getString(R.string.settings_disconnect_google)
        } else {
            txtAccountStatus.text = getString(R.string.settings_google_status_disconnected)
            btnConnectGoogle.text = getString(R.string.settings_connect_google)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        const val PREFS_NAME = "CleanCalPrefs"
        const val PREF_DEFAULT_VIEW = "default_view"
    }
}
