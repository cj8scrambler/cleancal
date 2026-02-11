package com.cleancal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.cleancal.adapters.CalendarPagerAdapter
import com.cleancal.data.ExampleDataGenerator
import com.cleancal.models.CalendarEvent
import com.cleancal.models.ViewType
import com.google.android.material.button.MaterialButton
import java.time.LocalDate

class MainActivity : AppCompatActivity() {
    
    private lateinit var viewPager: ViewPager2
    private lateinit var btnTwoWeek: MaterialButton
    private lateinit var btnMonth: MaterialButton
    private lateinit var btnThreeDay: MaterialButton
    private lateinit var btnOneDay: MaterialButton
    private lateinit var btnSettings: MaterialButton
    
    private lateinit var adapter: CalendarPagerAdapter
    private var currentViewType = ViewType.TWO_WEEK
    private lateinit var events: List<CalendarEvent>
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable fullscreen and keep screen on
        setupFullscreen()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        setContentView(R.layout.activity_main)
        
        // Load default view preference
        loadDefaultView()
        
        // Generate example events
        generateExampleEvents()
        
        // Initialize views
        initializeViews()
        
        // Setup adapter
        setupViewPager()
        
        // Setup button listeners
        setupListeners()
        
        // Update button states
        updateButtonStates()
    }
    
    private fun setupFullscreen() {
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        )
    }
    
    private fun loadDefaultView() {
        val prefs = getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE)
        val savedViewType = prefs.getString(SettingsActivity.PREF_DEFAULT_VIEW, ViewType.TWO_WEEK.name)
        currentViewType = ViewType.valueOf(savedViewType ?: ViewType.TWO_WEEK.name)
    }
    
    private fun generateExampleEvents() {
        val startDate = LocalDate.now().minusMonths(2)
        val endDate = LocalDate.now().plusMonths(2)
        events = ExampleDataGenerator.generateEvents(startDate, endDate)
    }
    
    private fun initializeViews() {
        viewPager = findViewById(R.id.calendarViewPager)
        btnTwoWeek = findViewById(R.id.btnTwoWeekView)
        btnMonth = findViewById(R.id.btnMonthView)
        btnThreeDay = findViewById(R.id.btnThreeDayView)
        btnOneDay = findViewById(R.id.btnOneDayView)
        btnSettings = findViewById(R.id.btnSettings)
    }
    
    private fun setupViewPager() {
        adapter = CalendarPagerAdapter(this, currentViewType, events)
        viewPager.adapter = adapter
        viewPager.setCurrentItem(adapter.getStartPosition(), false)
    }
    
    private fun setupListeners() {
        btnTwoWeek.setOnClickListener { switchViewType(ViewType.TWO_WEEK) }
        btnMonth.setOnClickListener { switchViewType(ViewType.MONTH) }
        btnThreeDay.setOnClickListener { switchViewType(ViewType.THREE_DAY) }
        btnOneDay.setOnClickListener { switchViewType(ViewType.ONE_DAY) }
        btnSettings.setOnClickListener { openSettings() }
    }
    
    private fun switchViewType(viewType: ViewType) {
        if (currentViewType != viewType) {
            currentViewType = viewType
            adapter.updateViewType(viewType)
            viewPager.setCurrentItem(adapter.getStartPosition(), false)
            updateButtonStates()
        }
    }
    
    private fun updateButtonStates() {
        // Reset all buttons
        btnTwoWeek.setBackgroundColor(getColor(android.R.color.transparent))
        btnMonth.setBackgroundColor(getColor(android.R.color.transparent))
        btnThreeDay.setBackgroundColor(getColor(android.R.color.transparent))
        btnOneDay.setBackgroundColor(getColor(android.R.color.transparent))
        
        // Highlight selected button
        val selectedColor = getColor(R.color.selected_view)
        when (currentViewType) {
            ViewType.TWO_WEEK -> btnTwoWeek.setBackgroundColor(selectedColor)
            ViewType.MONTH -> btnMonth.setBackgroundColor(selectedColor)
            ViewType.THREE_DAY -> btnThreeDay.setBackgroundColor(selectedColor)
            ViewType.ONE_DAY -> btnOneDay.setBackgroundColor(selectedColor)
        }
    }
    
    private fun openSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }
    
    override fun onResume() {
        super.onResume()
        // Reload preference in case it was changed
        val prefs = getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE)
        val savedViewType = prefs.getString(SettingsActivity.PREF_DEFAULT_VIEW, currentViewType.name)
        val newViewType = ViewType.valueOf(savedViewType ?: currentViewType.name)
        if (newViewType != currentViewType) {
            switchViewType(newViewType)
        }
        
        // Re-apply fullscreen
        setupFullscreen()
    }
}
