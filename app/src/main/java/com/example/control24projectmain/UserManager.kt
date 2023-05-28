package com.example.control24projectmain

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.yandex.mapkit.mapview.MapView

object UserManager {

    private const val SHARED_PREFS_NAME = "USER_INFO"
    private const val LOGIN_KEY = "LOGIN"
    private const val PASSWORD_KEY = "PASSWORD"
    private const val DARK_THEME = "THEME_STATE"
    private const val OBJECTS_LIST_VIEW_KEY = "OBJECTS_LIST"
    private const val LIST_STATE = "LIST_STATE"
    private const val MAP_SELECTED = "MAP"
    private const val DISPLAYED_ITEMS = "DISPLAY"
    private const val TRAFFIC_JAM = "TRAFFIC_JAM_STATE"
    private const val MAP_TYPE = "MAP_TYPE"
    private const val ZOOM_CONTROLS = "ZOOM_CONTROLS_STATE"
    private const val TRAFFIC_CONTROLS = "TRAFFIC_CONTROLS_STATE"
    private const val RESPONSE = "SAVED_RESPONSE"


    // Save user cred inside encrypted shared pref
    fun saveLoginCredentials(context: Context, login: String, password: String) {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        // Encrypt credentials
        val sharedPreferences = EncryptedSharedPreferences.create(
            SHARED_PREFS_NAME,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val editor = sharedPreferences.edit()
        editor.putString(LOGIN_KEY, login)
        editor.putString(PASSWORD_KEY, password)
        editor.apply()
    }

    // Decrypt cred for using
    fun getLoginCredentials(context: Context): Pair<String, String>? {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        // Decrypt credentials
        val sharedPreferences = EncryptedSharedPreferences.create(
            SHARED_PREFS_NAME,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val login = sharedPreferences.getString(LOGIN_KEY, null)
        val password = sharedPreferences.getString(PASSWORD_KEY, null)

        return if (login != null && password != null) {
            Pair(login, password)
        } else {
            null
        }
    }

    // Clear saved login cred
    fun clearLoginCredentials(context: Context) {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        val sharedPreferences = EncryptedSharedPreferences.create(
            SHARED_PREFS_NAME,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val editor = sharedPreferences.edit()
        editor.remove(LOGIN_KEY)
        editor.remove(PASSWORD_KEY)
        editor.apply()
    }

    // Save theme state
    fun saveThemeState(context: Context, darkThemeState: String) {
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit {
            putString(DARK_THEME, darkThemeState)
        }
    }

    // Get theme state
    fun getThemeState(context: Context): String? {
        return context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE).getString(
            DARK_THEME, "OFF")
    }

    // Save objects list view state
    fun saveObjectsListView(context: Context, isDetailedList: Boolean) {
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit {
            putBoolean(OBJECTS_LIST_VIEW_KEY, isDetailedList)
        }
    }

    // Get objects list view state
    fun getObjectsListView(context: Context): Boolean {
        return context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE).getBoolean(
            OBJECTS_LIST_VIEW_KEY, false)
    }

    // Save expanded list item
    fun saveExpandedListItem(context: Context, listStateArray: String) {
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit {
            putString(LIST_STATE, listStateArray)
        }
    }

    // Get expanded list item
    fun getExpandedListItem(context: Context): String? {
        return context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE).getString(
            LIST_STATE, null)
    }

    // Clear expanded list data
    fun clearExpandedListItem(context: Context) {
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit {
            remove(LIST_STATE)
        }
    }

    // Save selected map
    fun saveSelectedMap(context: Context, selectedMap: String) {
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit {
            putString(MAP_SELECTED, selectedMap)
        }
    }

    // Get expanded list item
    fun getSelectedMap(context: Context): String? {
        return context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE).getString(
            MAP_SELECTED, yandexMap)
    }

    // Save displayed items
    fun saveDisplayedItems(context: Context, itemsDisplayedArray: String) {
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit {
            putString(DISPLAYED_ITEMS, itemsDisplayedArray)
        }
    }

    // Get displayed items
    fun getDisplayedItems(context: Context): String? {
        return context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE).getString(
            DISPLAYED_ITEMS, null)
    }

    // Clear displayed items
    fun clearDisplayedItems(context: Context) {
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit {
            remove(DISPLAYED_ITEMS)
        }
    }

    // Save selected time
    fun saveScheduledTime(context: Context, key: String, hour: Int, minute: Int) {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit {
            putInt("${key}Hour", hour)
            putInt("${key}Minute", minute)
        }
    }

    // Get scheduled time
    fun getScheduledTime(context: Context, key: String): Pair<Int, Int> {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val defaultHour = if (key == START_TIME) 20 else 8
        val hour = sharedPreferences.getInt("${key}Hour", defaultHour)
        val minute = sharedPreferences.getInt("${key}Minute", 0)
        return Pair(hour, minute)
    }

    fun saveMapsCameraPosition(context: Context, latitude: Double, longitude: Double, zoom: Double) {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit {
            putString("latitude", latitude.toString())
            putString("longitude", longitude.toString())
            putString("zoom", zoom.toString())
        }
    }

    fun getMapsCameraPosition(context: Context, key: String, defaultValue: String): String? {
        return context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE).getString(key, defaultValue)
    }

    // Save traffic jam state
    fun saveTrafficJamState(context: Context, trafficJamState: Boolean) {
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit {
            putBoolean(TRAFFIC_JAM, trafficJamState)
        }
    }

    // Get traffic jam state
    fun getTrafficJamState(context: Context): Boolean {
        return context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE).getBoolean(
            TRAFFIC_JAM, false)
    }

    // Save driver name/phone
    fun saveDriverInfo(context: Context, key: String, name: String?, number: String?) {
        val combinedInfo = "$name,$number"
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit {
            putString(key, combinedInfo)
        }
    }

    // Get driver name\phone
    fun getDriverInfo(context: Context, key: String): Pair<String?, String?> {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val combinedInfo = sharedPreferences.getString(key, "null")
        val nameNumber = combinedInfo?.split(",")
        val name = nameNumber?.getOrNull(0)
        val number = nameNumber?.getOrNull(1)
        return Pair(name, number)
    }

    // Save map type
    fun saveMapType(context: Context, mapType: String) {
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit {
            putString(MAP_TYPE, mapType)
        }
    }

    // Get map type
    fun getMapType(context: Context): String? {
        return context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE).getString(
            MAP_TYPE, SCHEME)
    }

    // Save map type
    fun saveZoomControlsState(context: Context, zoomEnabled: Boolean) {
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit {
            putBoolean(ZOOM_CONTROLS, zoomEnabled)
        }
    }

    // Get map type
    fun getZoomControlsState(context: Context): Boolean {
        return context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE).getBoolean(
            ZOOM_CONTROLS, true)
    }

    // Save map type
    fun saveTrafficControlsState(context: Context, trafficControlsEnabled: Boolean) {
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit {
            putBoolean(TRAFFIC_CONTROLS, trafficControlsEnabled)
        }
    }

    // Get map type
    fun getTrafficControlsState(context: Context): Boolean {
        return context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE).getBoolean(
            TRAFFIC_CONTROLS, true)
    }

    // Save response
    fun saveResponse(context: Context, response: String) {
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit {
            putString(RESPONSE, response)
        }
    }

    // Get saved response
    fun getResponse(context: Context): String? {
        return context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE).getString(
            RESPONSE, "No data")
    }

    // Clear saved response
    fun clearResponse(context: Context) {
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit {
            remove(RESPONSE)
        }
    }
}