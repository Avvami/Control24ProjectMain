package com.example.control24projectmain

import android.app.Application
import android.text.format.DateFormat
import android.util.Log
import com.jakewharton.threetenabp.AndroidThreeTen
import com.yandex.mapkit.MapKitFactory
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

class AppLevelClass : Application() {
    override fun onCreate() {
        super.onCreate()

        // Set the api key to access yandex maps
        MapKitFactory.setApiKey("39866773-71e2-4a90-94a4-7beb016d6717")

        // Code to be executed before styles are loaded
        // Load and set saved app theme

        when (UserManager.getThemeState(this@AppLevelClass)) {
            "OFF" -> themeChange(this@AppLevelClass, "OFF")
            "ON" -> themeChange(this@AppLevelClass, "ON")
            "SCHEDULED" -> themeChange(this@AppLevelClass, "SCHEDULED")
            "SYSTEM" -> themeChange(this@AppLevelClass, "SYSTEM")
        }

        // Initialize ThreeTenABP
        AndroidThreeTen.init(this@AppLevelClass)
    }

    fun convertTime(timeGMT: String): String {
        val formatter24h = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
        val formatter12h = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm:ss a")
        val zonedTimeGMT = ZonedDateTime.parse(timeGMT, formatter24h.withZone(ZoneId.of("GMT")))

        val isSystem24Hour = DateFormat.is24HourFormat(this@AppLevelClass)

        // Get the system default time zone
        val defaultZoneId = ZoneId.systemDefault()

        val zonedTimeLocal = zonedTimeGMT.withZoneSameInstant(defaultZoneId)

        // Format the output
        return if (isSystem24Hour) {
            formatter24h.format(zonedTimeLocal)
        } else {
            formatter12h.format(zonedTimeLocal)
        }
    }
}