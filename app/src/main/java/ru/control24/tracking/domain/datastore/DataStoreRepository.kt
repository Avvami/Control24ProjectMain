package ru.control24.tracking.domain.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class DataStoreRepository(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("control24_pref")
        val LOGIN = stringPreferencesKey("login")
        val PASSWORD = stringPreferencesKey("password")
    }

    val readUserPreference: Flow<User> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            User(
                login = preferences[LOGIN],
                password = preferences[PASSWORD]
            )
        }

    suspend fun saveUser(login: String, password: String) {
        context.dataStore.edit { preferences ->
            preferences[LOGIN] = login
            preferences[PASSWORD] = password
        }
    }
}

data class User(
    val login: String? = null,
    val password: String? = null
)