package com.example.control24projectmain

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.text.format.DateFormat
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

private const val START_TIME = "startTime"
private const val END_TIME = "endTime"

fun isDarkModeEnabled(context: Context): Boolean {
    val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return currentNightMode == Configuration.UI_MODE_NIGHT_YES
}

fun themeChange(context: Context, darkThemeState: String) {
    val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
    val currentNightMode = uiModeManager.nightMode

    when (darkThemeState) {
        "OFF" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        "ON" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        "SCHEDULED" -> {
            //Log.i("HDKSJFHFD", isUserTimeWithinRange(context).toString())
            if (isUserTimeWithinRange(context)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
        "SYSTEM" -> {
            when (currentNightMode) {
                UiModeManager.MODE_NIGHT_AUTO -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }

                UiModeManager.MODE_NIGHT_CUSTOM -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }

                UiModeManager.MODE_NIGHT_NO -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }

                UiModeManager.MODE_NIGHT_YES -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
        }
    }
}

private fun isUserTimeWithinRange(context: Context): Boolean {
    // Get the current time
    val currentTime = Calendar.getInstance()

    // Get the start and end times from SharedPreferences
    val startTimeString = UserManager.getScheduledTime(context, START_TIME)
    val endTimeString = UserManager.getScheduledTime(context, END_TIME)

    // Convert the time strings into Calendar objects
    val startTime = stringToCalendar(context, startTimeString)
    val endTime = stringToCalendar(context, endTimeString)

    // If the end time is before the start time, assume it's on the next day
    if (endTime.before(startTime)) {
        endTime.add(Calendar.DAY_OF_MONTH, 1)
    }

    Log.i("HDKSJFHFD", "${currentTime.after(startTime)}")
    Log.i("HDKSJFHFD", "${currentTime.before(endTime)}")
    Log.i("HDKSJFHFD", "$startTime")
    Log.i("HDKSJFHFD", "$endTime")
    Log.i("HDKSJFHFD", "$currentTime")

    // Check if the current time is after the start time and before the end time
    return currentTime.after(startTime) && currentTime.before(endTime)
}

private fun stringToCalendar(context: Context, timeString: String?): Calendar {
    val calendar = Calendar.getInstance()
    val is24HourFormat = DateFormat.is24HourFormat(context)
    val format = if (is24HourFormat) {
        SimpleDateFormat("HH:mm", Locale.getDefault())
    } else {
        SimpleDateFormat("hh:mm a", Locale.getDefault())
    }

    format.timeZone = TimeZone.getDefault()
    val date = timeString?.let { format.parse(it) } ?: throw ParseException("Unable to parse time", 0)
    calendar.time = date

    // Set the day, month, and year to the current day, month, and year
    val currentCalendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_MONTH, currentCalendar.get(Calendar.DAY_OF_MONTH))
    calendar.set(Calendar.MONTH, currentCalendar.get(Calendar.MONTH))
    calendar.set(Calendar.YEAR, currentCalendar.get(Calendar.YEAR))

    return calendar
}
