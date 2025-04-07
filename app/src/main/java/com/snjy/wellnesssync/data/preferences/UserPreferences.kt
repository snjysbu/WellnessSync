package com.snjy.wellnesssync.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val USER_ID_KEY = stringPreferencesKey("user_id")
        val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
        val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        val LANGUAGE_KEY = stringPreferencesKey("language")
    }

    // User Authentication
    val isLoggedIn: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN_KEY] ?: false
    }

    val userId: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_ID_KEY]
    }

    val authToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[AUTH_TOKEN_KEY]
    }

    suspend fun saveUserSession(userId: String, authToken: String) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[AUTH_TOKEN_KEY] = authToken
            preferences[IS_LOGGED_IN_KEY] = true
        }
    }

    suspend fun clearUserSession() {
        dataStore.edit { preferences ->
            preferences.remove(USER_ID_KEY)
            preferences.remove(AUTH_TOKEN_KEY)
            preferences[IS_LOGGED_IN_KEY] = false
        }
    }

    // App Preferences
    val darkMode: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[DARK_MODE_KEY] ?: false
    }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = enabled
        }
    }

    val language: Flow<String> = dataStore.data.map { preferences ->
        preferences[LANGUAGE_KEY] ?: "en" // Default to English
    }

    suspend fun setLanguage(languageCode: String) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = languageCode
        }
    }
}