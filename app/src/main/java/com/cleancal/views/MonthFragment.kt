package com.cleancal.views

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.cleancal.R
import com.cleancal.models.CalendarEvent
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

class MonthFragment : BaseCalendarFragment() {

    private lateinit var monthYearText: TextView
    private lateinit var daysOfWeekHeader: LinearLayout
    private lateinit var monthGrid: GridLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.view_month, container, false)
        
        monthYearText = view.findViewById(R.id.monthYearText)
        daysOfWeekHeader = view.findViewById(R.id.daysOfWeekHeader)
        
        // Replace RecyclerView with GridLayout
        monthGrid = GridLayout(requireContext()).apply {
            columnCount = 7
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        
        val recyclerView = view.findViewById<View>(R.id.monthGridRecyclerView)
        val parent = recyclerView.parent as ViewGroup
        val index = parent.indexOfChild(recyclerView)
        parent.removeView(recyclerView)
        parent.addView(monthGrid, index)
        
        setupDaysOfWeekHeader()
        updateView()
        return view
    }

    private fun setupDaysOfWeekHeader() {
        val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        
        for (day in daysOfWeek) {
            val dayView = TextView(requireContext()).apply {
                text = day
                textSize = 20f
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    1f
                )
                setTextColor(Color.DKGRAY)
                setTypeface(null, android.graphics.Typeface.BOLD)
            }
            daysOfWeekHeader.addView(dayView)
        }
    }

    override fun updateView() {
        if (!::monthYearText.isInitialized) return
        
        val yearMonth = YearMonth.from(currentDate)
        monthYearText.text = "${yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${yearMonth.year}"
        
        monthGrid.removeAllViews()
        
        val firstDayOfMonth = yearMonth.atDay(1)
        val lastDayOfMonth = yearMonth.atEndOfMonth()
        
        // Add empty cells for days before the first day of the month
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
        for (i in 0 until firstDayOfWeek) {
            monthGrid.addView(createEmptyCell())
        }
        
        // Add cells for each day of the month
        for (day in 1..lastDayOfMonth.dayOfMonth) {
            val date = yearMonth.atDay(day)
            val dayCell = createDayCell(date)
            monthGrid.addView(dayCell)
        }
    }

    private fun createEmptyCell(): View {
        return View(requireContext()).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = 150
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            }
        }
    }

    private fun createDayCell(date: LocalDate): View {
        val cell = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = 150
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(2, 2, 2, 2)
            }
            setPadding(8, 8, 8, 8)
            setBackgroundColor(Color.WHITE)
        }
        
        val dayNumber = TextView(requireContext()).apply {
            text = date.dayOfMonth.toString()
            textSize = 20f
            setTextColor(Color.BLACK)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        cell.addView(dayNumber)
        
        // Show event indicators (colored dots)
        val dayEvents = events.filter { it.startTime.toLocalDate() == date }
        if (dayEvents.isNotEmpty()) {
            val eventsIndicator = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
            }
            
            dayEvents.take(5).forEach { event ->
                val dot = View(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(12, 12).apply {
                        setMargins(2, 2, 2, 2)
                    }
                    setBackgroundColor(event.calendarType.color)
                }
                eventsIndicator.addView(dot)
            }
            
            cell.addView(eventsIndicator)
        }
        
        return cell
    }

    companion object {
        fun newInstance(date: LocalDate): MonthFragment {
            return MonthFragment().apply {
                arguments = createBundle(date)
            }
        }
    }
}
