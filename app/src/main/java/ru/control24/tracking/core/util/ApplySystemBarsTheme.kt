package ru.control24.tracking.core.util

import android.content.Context
import android.view.View
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat

fun ApplySystemBarsTheme(view: View, context: Context, applyLightStatusBars: Boolean) {
    if (!view.isInEditMode) {
        (context as? ComponentActivity)?.let { activity ->
            WindowCompat.getInsetsController(activity.window, view).apply {
                isAppearanceLightStatusBars = !applyLightStatusBars
            }
        }
    }
}