package com.example.control24projectmain

import android.content.Context
import android.text.format.DateFormat
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.yandex.mapkit.mapview.MapView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

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
            MAP_SELECTED, "YANDEX")
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
        val isSystem24Hour = DateFormat.is24HourFormat(context)

        val timeString = if (isSystem24Hour) {
            String.format("%02d:%02d", hour, minute)
        } else {
            val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
            format.timeZone = TimeZone.getDefault()
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
            }
            format.format(calendar.time)
        }
        sharedPreferences.edit {
            putString(key, timeString)
        }
    }

    // Get scheduled time
    fun getScheduledTime(context: Context, key: String): String? {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val isSystem24Hour = DateFormat.is24HourFormat(context)
        val defaultHour = if (key == "startTime") 22 else 7
        val defaultMinute = 0
        val defaultTimeString = if (isSystem24Hour) {
            String.format("%02d:%02d", defaultHour, defaultMinute)
        } else {
            val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
            format.timeZone = TimeZone.getDefault()
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, defaultHour)
                set(Calendar.MINUTE, defaultMinute)
            }
            format.format(calendar.time)
        }
        return sharedPreferences.getString(key, defaultTimeString)
    }

    // Sava Yandex map's camera position
    fun saveYandexCameraPosition(context: Context, mapView: MapView) {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val cameraPosition = mapView.map.cameraPosition
        sharedPreferences.edit {
            putString("yandex_lat", cameraPosition.target.latitude.toString())
            putString("yandex_lon", cameraPosition.target.longitude.toString())
            putString("yandex_zoom", cameraPosition.zoom.toString())
        }
    }

    // Get Yandex map's camera position
    fun getYandexCameraPosition(context: Context, key: String, defaultValue: String): String? {
        return context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE).getString(key, defaultValue)
    }

    // Sava OSM map's camera position
    fun saveOsmCameraPosition(context: Context, mapView: com.mapbox.maps.MapView) {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val cameraPosition = mapView.getMapboxMap().cameraState
        sharedPreferences.edit {
            putString("osm_lat", cameraPosition.center.latitude().toString())
            putString("osm_lon", cameraPosition.center.longitude().toString())
            putString("osm_zoom", cameraPosition.zoom.toString())
        }
    }

    // Get OSM map's camera position
    fun getOsmCameraPosition(context: Context, key: String, defaultValue: String): String? {
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
            MAP_TYPE, "SCHEME")
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
}