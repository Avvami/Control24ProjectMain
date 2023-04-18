package com.example.control24projectmain

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

object UserManager {

    private const val SHARED_PREFS_NAME = "USER_INFO"
    private const val LOGIN_KEY = "LOGIN"
    private const val PASSWORD_KEY = "PASSWORD"
    private const val THEME_KEY = "THEME"
    private const val POSITION_KEY = "POSITION"

    // Save user cred inside encrypted shared pref
    fun saveLoginCredentials(context: Context, login: String, password: String) {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

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

    // Save data for other needs
    fun saveSharedPreferencesData(context: Context, isDarkTheme: Boolean, navViewPosition: Int) {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(THEME_KEY, isDarkTheme)
        editor.putInt(POSITION_KEY, navViewPosition)
        editor.apply()
    }

    fun getSharedPreferencesData(context: Context): Pair<Boolean, Int> {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val isLightThemeEnabled = sharedPreferences.getBoolean(THEME_KEY, false)
        val navViewPosition = sharedPreferences.getInt(POSITION_KEY, R.id.list_menu)
        return Pair(isLightThemeEnabled, navViewPosition)
    }

    /*fun clearSelectedFragmentPosition(context: Context) {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(POSITION_KEY)
        editor.apply()
    }*/
}