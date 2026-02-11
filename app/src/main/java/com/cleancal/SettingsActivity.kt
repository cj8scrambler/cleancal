package com.cleancal

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.cleancal.models.ViewType

class SettingsActivity : AppCompatActivity() {

    private lateinit var spinner: Spinner
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

        spinner = findViewById(R.id.defaultViewSpinner)

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

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        const val PREFS_NAME = "CleanCalPrefs"
        const val PREF_DEFAULT_VIEW = "default_view"
    }
}
