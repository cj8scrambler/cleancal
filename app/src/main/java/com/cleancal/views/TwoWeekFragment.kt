package com.cleancal.views

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
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
            orientation = LinearLayout.VERTICAL
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
        
        // Create 2 rows with 7 columns each
        val firstWeekRow = createWeekRow(startDate, 0)
        val secondWeekRow = createWeekRow(startDate, 7)
        
        contentLayout.addView(firstWeekRow)
        contentLayout.addView(secondWeekRow)
    }
    
    private fun createWeekRow(startDate: LocalDate, offset: Int): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            
            // Create 7 day columns for this week
            for (i in 0..6) {
                val date = startDate.plusDays((offset + i).toLong())
                val dayColumn = createDayColumn(date)
                addView(dayColumn)
            }
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
            setPadding(2, 2, 2, 2)
            
            // Create rounded background with subtle color
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#FEFEFE"))
                cornerRadius = 12f
                setStroke(1, Color.parseColor("#E8E8E8"))
            }
        }
        
        // Day header - combined on one line with smaller text
        val headerText = "${date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())} ${date.dayOfMonth}"
        val dayHeader = TextView(requireContext()).apply {
            text = headerText
            textSize = 10f  // Much smaller - reduced from 12f/16f
            setTextColor(Color.parseColor("#757575"))
            gravity = android.view.Gravity.CENTER
            setPadding(2, 2, 2, 2)
        }
        
        column.addView(dayHeader)
        
        // Add subtle divider
        val divider = View(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                1
            )
            setBackgroundColor(Color.parseColor("#EEEEEE"))
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
                setMargins(3, 3, 3, 3)
            }
            setPadding(6, 6, 6, 6)
            
            // Create rounded background with event color
            background = GradientDrawable().apply {
                setColor(event.calendarType.color)
                cornerRadius = 8f
            }
        }
        
        val titleView = TextView(requireContext()).apply {
            text = event.title
            textSize = 14f  // Slightly reduced from 16f
            setTextColor(Color.WHITE)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val timeView = TextView(requireContext()).apply {
            text = "${event.startTime.format(timeFormatter)} - ${event.endTime.format(timeFormatter)}"
            textSize = 12f  // Reduced from 14f
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
