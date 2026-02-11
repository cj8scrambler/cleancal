package com.cleancal.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cleancal.models.CalendarEvent
import com.cleancal.models.ViewType
import com.cleancal.views.BaseCalendarFragment
import com.cleancal.views.MonthFragment
import com.cleancal.views.OneDayFragment
import com.cleancal.views.ThreeDayFragment
import com.cleancal.views.TwoWeekFragment
import java.time.LocalDate
import java.time.YearMonth

class CalendarPagerAdapter(
    activity: FragmentActivity,
    private var viewType: ViewType,
    private var events: List<CalendarEvent>
) : FragmentStateAdapter(activity) {

    private val fragments = mutableMapOf<Int, BaseCalendarFragment>()
    private val baseDate = LocalDate.now()

    override fun getItemCount(): Int = Int.MAX_VALUE / 2  // Allow infinite scrolling

    override fun createFragment(position: Int): Fragment {
        val date = getDateForPosition(position)
        val fragment = when (viewType) {
            ViewType.TWO_WEEK -> TwoWeekFragment.newInstance(date)
            ViewType.MONTH -> MonthFragment.newInstance(date)
            ViewType.THREE_DAY -> ThreeDayFragment.newInstance(date)
            ViewType.ONE_DAY -> OneDayFragment.newInstance(date)
        }
        
        fragment.setEvents(events)
        fragments[position] = fragment
        return fragment
    }

    fun getStartPosition(): Int = itemCount / 2

    private fun getDateForPosition(position: Int): LocalDate {
        val offset = position - getStartPosition()
        
        return when (viewType) {
            ViewType.TWO_WEEK -> baseDate.plusDays(offset * 14L)
            ViewType.MONTH -> {
                val monthOffset = offset.toLong()
                YearMonth.from(baseDate).plusMonths(monthOffset).atDay(1)
            }
            ViewType.THREE_DAY -> baseDate.plusDays(offset * 3L)
            ViewType.ONE_DAY -> baseDate.plusDays(offset.toLong())
        }
    }

    fun updateViewType(newViewType: ViewType) {
        viewType = newViewType
        notifyDataSetChanged()
    }
    
    fun updateEvents(newEvents: List<CalendarEvent>) {
        events = newEvents
        notifyEventsChanged(newEvents)
    }

    fun notifyEventsChanged(newEvents: List<CalendarEvent>) {
        fragments.values.forEach { it.setEvents(newEvents) }
    }
}
