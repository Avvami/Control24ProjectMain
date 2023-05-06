package com.example.control24projectmain

import android.app.Application
import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.yandex.mapkit.MapKitFactory

class AppThemeSet : Application() {
    override fun onCreate() {
        super.onCreate()

        // Set the api key to access yandex maps
        MapKitFactory.setApiKey("39866773-71e2-4a90-94a4-7beb016d6717")

        // Code to be executed before styles are loaded
        // Load and set saved app theme
        val uiModeManager = this.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        val currentNightMode = uiModeManager.nightMode

        //val darkThemeState = UserManager.getThemeState(this@AppThemeSet).first
        //var isDarkTheme = UserManager.getThemeState(this@AppThemeSet).second

        when (UserManager.getThemeState(this@AppThemeSet)) {
            "OFF" -> /*AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)*/
                themeChange(this@AppThemeSet, "OFF")
            "ON" -> /*AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)*/
                themeChange(this@AppThemeSet, "ON")
            "SCHEDULED" -> /*AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)*/
                themeChange(this@AppThemeSet, "SCHEDULED")
            "SYSTEM" -> /*{
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
            }*/themeChange(this@AppThemeSet, "SYSTEM")
        }
        /*if (themePreferences) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }*/
    }
}