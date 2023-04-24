package com.example.control24projectmain

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class AppThemeSet : Application() {
    override fun onCreate() {
        super.onCreate()

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