package com.cleancal.views

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.cleancal.R
import com.cleancal.models.CalendarEvent
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class OneDayFragment : BaseCalendarFragment() {

    private lateinit var dateText: TextView
    private lateinit var timeColumn: LinearLayout
    private lateinit var eventsContainer: FrameLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.view_one_day, container, false)
        
        dateText = view.findViewById(R.id.dateText)
        timeColumn = view.findViewById(R.id.timeColumn)
        eventsContainer = view.findViewById(R.id.eventsContainer)
        
        setupTimeColumn()
        updateView()
        return view
    }

    private fun setupTimeColumn() {
        timeColumn.removeAllViews()
        
        // Add hour labels
        for (hour in 0..23) {
            val timeLabel = TextView(requireContext()).apply {
                text = String.format("%02d:00", hour)
                textSize = 18f
                setPadding(16, 16, 16, 16)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    80
                )
                setTextColor(Color.DKGRAY)
            }
            timeColumn.addView(timeLabel)
        }
    }

    override fun updateView() {
        if (!::dateText.isInitialized) return
        
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")
        dateText.text = currentDate.format(formatter)
        
        // Clear and repopulate events
        eventsContainer.removeAllViews()
        
        val eventsLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        
        val dayEvents = events.filter { it.startTime.toLocalDate() == currentDate }
        for (event in dayEvents) {
            val eventView = createEventView(event)
            eventsLayout.addView(eventView)
        }
        
        eventsContainer.addView(eventsLayout)
    }

    private fun createEventView(event: CalendarEvent): View {
        val eventLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16, 8, 16, 8)
            }
            setPadding(20, 20, 20, 20)
            setBackgroundColor(event.calendarType.color)
        }
        
        val titleView = TextView(requireContext()).apply {
            text = event.title
            textSize = 24f
            setTextColor(Color.WHITE)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val timeView = TextView(requireContext()).apply {
            text = "${event.startTime.format(timeFormatter)} - ${event.endTime.format(timeFormatter)}"
            textSize = 20f
            setTextColor(Color.WHITE)
        }
        
        eventLayout.addView(titleView)
        eventLayout.addView(timeView)
        
        return eventLayout
    }

    companion object {
        fun newInstance(date: LocalDate): OneDayFragment {
            return OneDayFragment().apply {
                arguments = createBundle(date)
            }
        }
    }
}
