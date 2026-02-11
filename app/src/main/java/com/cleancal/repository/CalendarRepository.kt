package com.cleancal.repository

import android.content.Context
import com.cleancal.auth.GoogleAuthManager
import com.cleancal.models.CalendarEvent
import com.cleancal.models.CalendarType
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneId

class CalendarRepository(private val context: Context) {
    
    private val authManager = GoogleAuthManager(context)
    
    suspend fun fetchCalendarEvents(startDate: LocalDateTime, endDate: LocalDateTime): List<CalendarEvent> {
        return withContext(Dispatchers.IO) {
            try {
                val account = authManager.getCurrentAccount() ?: return@withContext emptyList()
                
                // Create credential
                val credential = GoogleAccountCredential.usingOAuth2(
                    context,
                    listOf(CalendarScopes.CALENDAR_READONLY)
                )
                credential.selectedAccount = account.account
                
                // Build Calendar service
                val service = Calendar.Builder(
                    AndroidHttp.newCompatibleTransport(),
                    GsonFactory.getDefaultInstance(),
                    credential
                )
                    .setApplicationName("CleanCal")
                    .build()
                
                // Convert LocalDateTime to DateTime
                val timeMin = DateTime(
                    startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                )
                val timeMax = DateTime(
                    endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                )
                
                // Fetch events from primary calendar
                val events = service.events().list("primary")
                    .setTimeMin(timeMin)
                    .setTimeMax(timeMax)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute()
                
                // Convert Google Calendar events to CalendarEvent objects
                events.items?.mapNotNull { event ->
                    try {
                        val start = event.start?.dateTime ?: event.start?.date ?: return@mapNotNull null
                        val end = event.end?.dateTime ?: event.end?.date ?: return@mapNotNull null
                        
                        val startTime = LocalDateTime.ofInstant(
                            java.time.Instant.ofEpochMilli(start.value),
                            ZoneId.systemDefault()
                        )
                        val endTime = LocalDateTime.ofInstant(
                            java.time.Instant.ofEpochMilli(end.value),
                            ZoneId.systemDefault()
                        )
                        
                        // Determine calendar type based on event properties
                        val calendarType = determineCalendarType(event.summary ?: "")
                        
                        CalendarEvent(
                            id = event.id ?: "",
                            title = event.summary ?: "Untitled",
                            startTime = startTime,
                            endTime = endTime,
                            calendarType = calendarType,
                            description = event.description ?: ""
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }
    
    private fun determineCalendarType(title: String): CalendarType {
        val lowerTitle = title.lowercase()
        return when {
            lowerTitle.contains("birthday") -> CalendarType.BIRTHDAY
            lowerTitle.contains("holiday") -> CalendarType.HOLIDAY
            lowerTitle.contains("reminder") || lowerTitle.contains("task") -> CalendarType.REMINDER
            lowerTitle.contains("meeting") || lowerTitle.contains("work") ||
                lowerTitle.contains("call") || lowerTitle.contains("standup") -> CalendarType.WORK
            else -> CalendarType.PERSONAL
        }
    }
}
