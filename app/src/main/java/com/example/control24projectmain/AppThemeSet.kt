package com.example.control24projectmain

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.yandex.mapkit.MapKitFactory

class AppThemeSet : Application() {
    override fun onCreate() {
        super.onCreate()

        // Set the api key to access yandex maps
        MapKitFactory.setApiKey("39866773-71e2-4a90-94a4-7beb016d6717")

        // Code to be executed before styles are loaded
        // Load and set saved app theme
        val themePreferences = UserManager.getThemeState(this@AppThemeSet)
        if (themePreferences) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}