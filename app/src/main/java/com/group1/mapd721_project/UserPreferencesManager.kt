package com.yulingwu.mapd721_project

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferencesManager(private val context: Context) {

    companion object {
        val EMAIL_KEY = stringPreferencesKey("email")
        val PASSWORD_KEY = stringPreferencesKey("password")
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

    // Logout - Clean email and password
    suspend fun clearUser() {
        context.dataStore.edit { it.clear() }
    }
}