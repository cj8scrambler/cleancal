package com.cleancal.models

import java.time.LocalDateTime

data class CalendarEvent(
    val id: String,
    val title: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val calendarType: CalendarType,
    val description: String = ""
)

enum class CalendarType(val color: Int, val displayName: String) {
    WORK(0xFF2196F3.toInt(), "Work"),           // Blue
    PERSONAL(0xFF4CAF50.toInt(), "Personal"),   // Green
    BIRTHDAY(0xFFFF5722.toInt(), "Birthday"),   // Deep Orange
    REMINDER(0xFFFF9800.toInt(), "Reminder"),   // Orange
    HOLIDAY(0xFF9C27B0.toInt(), "Holiday")      // Purple
}

enum class ViewType {
    TWO_WEEK,
    MONTH,
    THREE_DAY,
    ONE_DAY
}
