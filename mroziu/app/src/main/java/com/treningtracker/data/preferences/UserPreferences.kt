package com.treningtracker.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferences @Inject constructor(
    private val context: Context
) {
    companion object {
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        private val DYNAMIC_COLOR_KEY = booleanPreferencesKey("dynamic_color")
        private val AUTO_BACKUP_KEY = booleanPreferencesKey("auto_backup")
        private val BACKUP_FREQUENCY_KEY = intPreferencesKey("backup_frequency")
        private val LAST_BACKUP_KEY = longPreferencesKey("last_backup")
        private val GOOGLE_ACCOUNT_KEY = stringPreferencesKey("google_account")
        private val NOTIFICATIONS_ENABLED_KEY = booleanPreferencesKey("notifications_enabled")
        private val WORKOUT_REMINDERS_KEY = booleanPreferencesKey("workout_reminders")
        private val MEASUREMENT_REMINDERS_KEY = booleanPreferencesKey("measurement_reminders")
    }

    val userPreferencesFlow: Flow<UserPreferencesData> = context.dataStore.data
        .map { preferences ->
            UserPreferencesData(
                darkMode = preferences[DARK_MODE_KEY] ?: false,
                dynamicColor = preferences[DYNAMIC_COLOR_KEY] ?: true,
                autoBackup = preferences[AUTO_BACKUP_KEY] ?: false,
                backupFrequency = preferences[BACKUP_FREQUENCY_KEY] ?: 7, // days
                lastBackup = preferences[LAST_BACKUP_KEY] ?: 0L,
                googleAccount = preferences[GOOGLE_ACCOUNT_KEY] ?: "",
                notificationsEnabled = preferences[NOTIFICATIONS_ENABLED_KEY] ?: true,
                workoutReminders = preferences[WORKOUT_REMINDERS_KEY] ?: false,
                measurementReminders = preferences[MEASUREMENT_REMINDERS_KEY] ?: false
            )
        }

    suspend fun updateDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = enabled
        }
    }

    suspend fun updateDynamicColor(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DYNAMIC_COLOR_KEY] = enabled
        }
    }

    suspend fun updateAutoBackup(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_BACKUP_KEY] = enabled
        }
    }

    suspend fun updateBackupFrequency(days: Int) {
        context.dataStore.edit { preferences ->
            preferences[BACKUP_FREQUENCY_KEY] = days
        }
    }

    suspend fun updateLastBackup(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[LAST_BACKUP_KEY] = timestamp
        }
    }

    suspend fun updateGoogleAccount(account: String) {
        context.dataStore.edit { preferences ->
            preferences[GOOGLE_ACCOUNT_KEY] = account
        }
    }

    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED_KEY] = enabled
        }
    }

    suspend fun updateWorkoutReminders(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[WORKOUT_REMINDERS_KEY] = enabled
        }
    }

    suspend fun updateMeasurementReminders(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[MEASUREMENT_REMINDERS_KEY] = enabled
        }
    }
}

data class UserPreferencesData(
    val darkMode: Boolean = false,
    val dynamicColor: Boolean = true,
    val autoBackup: Boolean = false,
    val backupFrequency: Int = 7, // days
    val lastBackup: Long = 0L,
    val googleAccount: String = "",
    val notificationsEnabled: Boolean = true,
    val workoutReminders: Boolean = false,
    val measurementReminders: Boolean = false
)