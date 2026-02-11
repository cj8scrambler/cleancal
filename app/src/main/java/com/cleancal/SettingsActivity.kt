package com.cleancal

import android.content.Context
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.cleancal.models.ViewType

class SettingsActivity : AppCompatActivity() {

    private lateinit var radioGroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        radioGroup = findViewById(R.id.defaultViewRadioGroup)

        // Load current preference
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedViewType = prefs.getString(PREF_DEFAULT_VIEW, ViewType.TWO_WEEK.name)

        // Set the appropriate radio button
        when (savedViewType) {
            ViewType.TWO_WEEK.name -> findViewById<RadioButton>(R.id.radioTwoWeek).isChecked = true
            ViewType.MONTH.name -> findViewById<RadioButton>(R.id.radioMonth).isChecked = true
            ViewType.THREE_DAY.name -> findViewById<RadioButton>(R.id.radioThreeDay).isChecked = true
            ViewType.ONE_DAY.name -> findViewById<RadioButton>(R.id.radioOneDay).isChecked = true
        }

        // Save preference when changed
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val viewType = when (checkedId) {
                R.id.radioTwoWeek -> ViewType.TWO_WEEK
                R.id.radioMonth -> ViewType.MONTH
                R.id.radioThreeDay -> ViewType.THREE_DAY
                R.id.radioOneDay -> ViewType.ONE_DAY
                else -> ViewType.TWO_WEEK
            }

            prefs.edit().putString(PREF_DEFAULT_VIEW, viewType.name).apply()
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
