package com.example.control24projectmain

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import java.util.Calendar

fun isDarkModeEnabled(context: Context): Boolean {
    val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return currentNightMode == Configuration.UI_MODE_NIGHT_YES
}

fun themeChange(context: Context, darkThemeState: String) {
    val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
    val currentNightMode = uiModeManager.nightMode

    when (darkThemeState) {
        OFF -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        ON -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        SCHEDULED -> {
            if (isUserTimeWithinRange(context)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
        SYSTEM -> {
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
    // Get start and end time
    val (startHour, startMinute) = UserManager.getScheduledTime(context, START_TIME)
    val (endHour, endMinute) = UserManager.getScheduledTime(context, END_TIME)

    // Get current time
    val current = Calendar.getInstance()
    val currentHour = current.get(Calendar.HOUR_OF_DAY)
    val currentMinute = current.get(Calendar.MINUTE)

    // Convert times into minutes for comparison
    val startTimeInMinutes = startHour * 60 + startMinute
    val endTimeInMinutes = endHour * 60 + endMinute
    val currentTimeInMinutes = currentHour * 60 + currentMinute

    // Check if current time falls within start and end time
    val isNight = if (startTimeInMinutes < endTimeInMinutes) {
        currentTimeInMinutes in startTimeInMinutes..endTimeInMinutes
    } else {
        currentTimeInMinutes <= endTimeInMinutes || currentTimeInMinutes >= startTimeInMinutes
    }

    return isNight
}