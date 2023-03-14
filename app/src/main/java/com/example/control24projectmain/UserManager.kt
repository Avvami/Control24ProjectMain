package com.example.control24projectmain

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

object UserManager {

    private const val SHARED_PREFS_NAME = "USER_INFO"
    private const val LOGIN_KEY = "LOGIN"
    private const val PASSWORD_KEY = "PASSWORD"

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
}