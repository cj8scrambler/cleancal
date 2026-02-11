package com.cleancal.views

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.cleancal.R
import com.cleancal.models.CalendarEvent
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class ThreeDayFragment : BaseCalendarFragment() {

    private lateinit var dateRangeText: TextView
    private lateinit var timeColumn: LinearLayout
    private lateinit var daysContainer: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.view_three_day, container, false)
        
        dateRangeText = view.findViewById(R.id.dateRangeText)
        timeColumn = view.findViewById(R.id.timeColumn)
        daysContainer = view.findViewById(R.id.daysContainer)
        
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
                textSize = 14f
                setPadding(8, 8, 8, 8)
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
        if (!::dateRangeText.isInitialized) return
        
        val startDate = currentDate
        val endDate = currentDate.plusDays(2)
        val formatter = DateTimeFormatter.ofPattern("MMM d")
        
        dateRangeText.text = "${startDate.format(formatter)} - ${endDate.format(formatter)}"
        
        daysContainer.removeAllViews()
        
        for (i in 0..2) {
            val date = startDate.plusDays(i.toLong())
            val dayColumn = createDayColumn(date)
            daysContainer.addView(dayColumn)
        }
    }

    private fun createDayColumn(date: LocalDate): View {
        val column = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                400,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.WHITE)
        }
        
        // Day header
        val header = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
            setBackgroundColor(Color.LTGRAY)
        }
        
        val dayOfWeek = TextView(requireContext()).apply {
            text = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
            textSize = 18f
            setTextColor(Color.DKGRAY)
        }
        
        val dayNumber = TextView(requireContext()).apply {
            text = date.dayOfMonth.toString()
            textSize = 28f
            setTextColor(Color.BLACK)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        
        header.addView(dayOfWeek)
        header.addView(dayNumber)
        column.addView(header)
        
        // Events area
        val eventsArea = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        
        val dayEvents = events.filter { it.startTime.toLocalDate() == date }
        for (event in dayEvents) {
            val eventView = createEventView(event)
            eventsArea.addView(eventView)
        }
        
        column.addView(eventsArea)
        
        return column
    }

    private fun createEventView(event: CalendarEvent): View {
        val eventLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 4, 8, 4)
            }
            setPadding(12, 12, 12, 12)
            setBackgroundColor(event.calendarType.color)
        }
        
        val titleView = TextView(requireContext()).apply {
            text = event.title
            textSize = 18f
            setTextColor(Color.WHITE)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val timeView = TextView(requireContext()).apply {
            text = "${event.startTime.format(timeFormatter)} - ${event.endTime.format(timeFormatter)}"
            textSize = 16f
            setTextColor(Color.WHITE)
        }
        
        eventLayout.addView(titleView)
        eventLayout.addView(timeView)
        
        return eventLayout
    }

    companion object {
        fun newInstance(date: LocalDate): ThreeDayFragment {
            return ThreeDayFragment().apply {
                arguments = createBundle(date)
            }
        }
    }
}
