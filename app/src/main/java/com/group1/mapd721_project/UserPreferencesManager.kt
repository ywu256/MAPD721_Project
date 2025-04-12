package com.group1.mapd721_project

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow

val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferencesManager(private val context: Context) {

    companion object {
        val EMAIL_KEY = stringPreferencesKey("email")
        val PASSWORD_KEY = stringPreferencesKey("password")
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode_enabled")
        val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications_enabled")
    }

    // Store email and password
    suspend fun saveUser(email: String, password: String) {
        context.dataStore.edit { prefs ->
            prefs[EMAIL_KEY] = email
            prefs[PASSWORD_KEY] = password
        }
    }

    // Get the stored email and password
    suspend fun getUser(): Pair<String?, String?> {
        val prefs = context.dataStore.data.first()
        val email = prefs[EMAIL_KEY]
        val password = prefs[PASSWORD_KEY]
        return Pair(email, password)
    }

    // Forgot Password - Update password
    suspend fun updatePassword(newPassword: String) {
        context.dataStore.edit { prefs ->
            prefs[PASSWORD_KEY] = newPassword
        }
    }

    // Storing DarkMode preference
    suspend fun setDarkModeEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = enabled
        }
    }
    // storing notification preference
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[NOTIFICATIONS_KEY] = enabled
        }
    }

    val darkModeFlow: Flow<Boolean> = context.dataStore.data.map {
        preferences ->
        preferences[DARK_MODE_KEY] ?: false
    }

    // Logout - Clean email, password and preferences
    suspend fun clearUser() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}