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

class TwoWeekFragment : BaseCalendarFragment() {

    private lateinit var dateRangeText: TextView
    private lateinit var contentLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.view_two_week, container, false)
        
        dateRangeText = view.findViewById(R.id.dateRangeText)
        contentLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        
        // Replace RecyclerView with our LinearLayout for simplicity
        val parent = view as ViewGroup
        val recyclerView = view.findViewById<View>(R.id.daysRecyclerView)
        val recyclerViewParent = recyclerView.parent as ViewGroup
        val index = recyclerViewParent.indexOfChild(recyclerView)
        recyclerViewParent.removeView(recyclerView)
        recyclerViewParent.addView(contentLayout, index)
        
        updateView()
        return view
    }

    override fun updateView() {
        if (!::dateRangeText.isInitialized) return
        
        val startDate = currentDate
        val endDate = currentDate.plusDays(13)
        val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
        
        dateRangeText.text = "${startDate.format(formatter)} - ${endDate.format(formatter)}"
        
        contentLayout.removeAllViews()
        
        // Create columns for each day
        for (i in 0..13) {
            val date = startDate.plusDays(i.toLong())
            val dayColumn = createDayColumn(date)
            contentLayout.addView(dayColumn)
        }
    }

    private fun createDayColumn(date: LocalDate): View {
        val column = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT,
                1f
            )
            setPadding(8, 8, 8, 8)
            setBackgroundColor(Color.WHITE)
        }
        
        // Day header
        val dayOfWeek = TextView(requireContext()).apply {
            text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            textSize = 18f
            setTextColor(Color.DKGRAY)
            gravity = android.view.Gravity.CENTER
        }
        
        val dayNumber = TextView(requireContext()).apply {
            text = date.dayOfMonth.toString()
            textSize = 24f
            setTextColor(Color.BLACK)
            gravity = android.view.Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        
        column.addView(dayOfWeek)
        column.addView(dayNumber)
        
        // Add divider
        val divider = View(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                2
            )
            setBackgroundColor(Color.LTGRAY)
        }
        column.addView(divider)
        
        // Add events for this day
        val dayEvents = events.filter { it.startTime.toLocalDate() == date }
        for (event in dayEvents) {
            val eventView = createEventView(event)
            column.addView(eventView)
        }
        
        return column
    }

    private fun createEventView(event: CalendarEvent): View {
        val eventLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(4, 4, 4, 4)
            }
            setPadding(8, 8, 8, 8)
            setBackgroundColor(event.calendarType.color)
        }
        
        val titleView = TextView(requireContext()).apply {
            text = event.title
            textSize = 16f
            setTextColor(Color.WHITE)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val timeView = TextView(requireContext()).apply {
            text = "${event.startTime.format(timeFormatter)} - ${event.endTime.format(timeFormatter)}"
            textSize = 14f
            setTextColor(Color.WHITE)
        }
        
        eventLayout.addView(titleView)
        eventLayout.addView(timeView)
        
        return eventLayout
    }

    companion object {
        fun newInstance(date: LocalDate): TwoWeekFragment {
            return TwoWeekFragment().apply {
                arguments = createBundle(date)
            }
        }
    }
}
