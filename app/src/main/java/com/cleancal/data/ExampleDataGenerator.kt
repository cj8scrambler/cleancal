package com.cleancal.data

import com.cleancal.models.CalendarEvent
import com.cleancal.models.CalendarType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

object ExampleDataGenerator {
    
    fun generateEvents(startDate: LocalDate, endDate: LocalDate): List<CalendarEvent> {
        val events = mutableListOf<CalendarEvent>()
        var currentDate = startDate
        var eventId = 1
        
        while (!currentDate.isAfter(endDate)) {
            // Add various events throughout the period
            when (currentDate.dayOfWeek.value) {
                1 -> { // Monday
                    events.add(createEvent(eventId++, "Team Meeting", currentDate, 9, 0, 10, 0, CalendarType.WORK))
                    events.add(createEvent(eventId++, "Project Review", currentDate, 14, 0, 15, 30, CalendarType.WORK))
                }
                2 -> { // Tuesday
                    events.add(createEvent(eventId++, "Client Call", currentDate, 10, 0, 11, 0, CalendarType.WORK))
                    events.add(createEvent(eventId++, "Gym", currentDate, 18, 0, 19, 0, CalendarType.PERSONAL))
                }
                3 -> { // Wednesday
                    events.add(createEvent(eventId++, "Department Sync", currentDate, 11, 0, 12, 0, CalendarType.WORK))
                    events.add(createEvent(eventId++, "Lunch with Sarah", currentDate, 12, 30, 13, 30, CalendarType.PERSONAL))
                }
                4 -> { // Thursday
                    events.add(createEvent(eventId++, "Sprint Planning", currentDate, 9, 0, 10, 30, CalendarType.WORK))
                    events.add(createEvent(eventId++, "Code Review", currentDate, 15, 0, 16, 0, CalendarType.WORK))
                }
                5 -> { // Friday
                    events.add(createEvent(eventId++, "Weekly Standup", currentDate, 9, 30, 10, 0, CalendarType.WORK))
                    events.add(createEvent(eventId++, "Happy Hour", currentDate, 17, 0, 19, 0, CalendarType.PERSONAL))
                }
                6 -> { // Saturday
                    events.add(createEvent(eventId++, "Grocery Shopping", currentDate, 10, 0, 11, 30, CalendarType.PERSONAL))
                    events.add(createEvent(eventId++, "Family Dinner", currentDate, 18, 0, 20, 0, CalendarType.PERSONAL))
                }
                7 -> { // Sunday
                    events.add(createEvent(eventId++, "Meal Prep", currentDate, 10, 0, 12, 0, CalendarType.PERSONAL))
                }
            }
            
            // Add some birthdays randomly
            if (currentDate.dayOfMonth == 5) {
                events.add(createEvent(eventId++, "Mom's Birthday", currentDate, 0, 0, 23, 59, CalendarType.BIRTHDAY))
            } else if (currentDate.dayOfMonth == 15) {
                events.add(createEvent(eventId++, "John's Birthday", currentDate, 0, 0, 23, 59, CalendarType.BIRTHDAY))
            }
            
            // Add reminders on specific days
            if (currentDate.dayOfWeek.value == 1) {
                events.add(createEvent(eventId++, "Pay Bills", currentDate, 8, 0, 8, 30, CalendarType.REMINDER))
            }
            
            // Add holidays
            if (currentDate.month.value == 12 && currentDate.dayOfMonth == 25) {
                events.add(createEvent(eventId++, "Christmas Day", currentDate, 0, 0, 23, 59, CalendarType.HOLIDAY))
            } else if (currentDate.month.value == 1 && currentDate.dayOfMonth == 1) {
                events.add(createEvent(eventId++, "New Year's Day", currentDate, 0, 0, 23, 59, CalendarType.HOLIDAY))
            } else if (currentDate.month.value == 7 && currentDate.dayOfMonth == 4) {
                events.add(createEvent(eventId++, "Independence Day", currentDate, 0, 0, 23, 59, CalendarType.HOLIDAY))
            }
            
            currentDate = currentDate.plusDays(1)
        }
        
        return events
    }
    
    private fun createEvent(
        id: Int,
        title: String,
        date: LocalDate,
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int,
        type: CalendarType
    ): CalendarEvent {
        return CalendarEvent(
            id = id.toString(),
            title = title,
            startTime = LocalDateTime.of(date, LocalTime.of(startHour, startMinute)),
            endTime = LocalDateTime.of(date, LocalTime.of(endHour, endMinute)),
            calendarType = type
        )
    }
}
