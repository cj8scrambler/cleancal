package com.cleancal.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cleancal.models.CalendarEvent
import java.time.LocalDate

abstract class BaseCalendarFragment : Fragment() {
    protected var currentDate: LocalDate = LocalDate.now()
    protected var events: List<CalendarEvent> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        currentDate = arguments?.getString(ARG_DATE)?.let { LocalDate.parse(it) } ?: LocalDate.now()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    fun setEvents(events: List<CalendarEvent>) {
        this.events = events
    }

    abstract fun updateView()

    companion object {
        const val ARG_DATE = "date"

        fun createBundle(date: LocalDate): Bundle {
            return Bundle().apply {
                putString(ARG_DATE, date.toString())
            }
        }
    }
}
