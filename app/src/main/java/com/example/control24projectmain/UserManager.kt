package com.example.control24projectmain

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

object UserManager {

    private const val SHARED_PREFS_NAME = "USER_INFO"
    private const val LOGIN_KEY = "LOGIN"
    private const val PASSWORD_KEY = "PASSWORD"
    private const val THEME_KEY = "THEME"
    private const val OBJECTS_LIST_VIEW_KEY = "OBJECTS_LIST"
    private const val LIST_STATE = "LIST_STATE"

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
    fun saveThemeState(context: Context, isDarkTheme: Boolean) {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(THEME_KEY, isDarkTheme)
        editor.apply()
    }

    // Get theme state
    fun getThemeState(context: Context): Boolean {
        val sharedPreferences =
            context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(THEME_KEY, false)
    }

    // Save objects list view state
    fun saveObjectsListView(context: Context, isDetailedList: Boolean) {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(OBJECTS_LIST_VIEW_KEY, isDetailedList)
        editor.apply()
    }

    // Get objects list view state
    fun getObjectsListView(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(OBJECTS_LIST_VIEW_KEY, false)
    }

    // Save expanded list item
    fun saveExpandedListItem(context: Context, listStateArray: String) {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(LIST_STATE, listStateArray)
        editor.apply()
    }

    // Save expanded list item
    fun getExpandedListItem(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(LIST_STATE, null)
    }

    // Clear expanded list data
    fun clearExpandedListItem(context: Context) {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(LIST_STATE)
        editor.apply()
    }
}