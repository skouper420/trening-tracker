package com.treningtracker.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treningtracker.data.backup.BackupManager
import com.treningtracker.data.backup.RestoreResult
import com.treningtracker.data.preferences.UserPreferences
import com.treningtracker.data.preferences.UserPreferencesData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val backupManager: BackupManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    val userPreferencesFlow: StateFlow<UserPreferencesData> = userPreferences.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferencesData()
        )

    fun updateDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.updateDarkMode(enabled)
        }
    }

    fun updateDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.updateDynamicColor(enabled)
        }
    }

    fun updateAutoBackup(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.updateAutoBackup(enabled)
        }
    }

    fun updateBackupFrequency(days: Int) {
        viewModelScope.launch {
            userPreferences.updateBackupFrequency(days)
        }
    }

    fun updateNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.updateNotificationsEnabled(enabled)
        }
    }

    fun updateWorkoutReminders(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.updateWorkoutReminders(enabled)
        }
    }

    fun updateMeasurementReminders(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.updateMeasurementReminders(enabled)
        }
    }

    fun createBackup() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isCreatingBackup = true, error = null) }
                val backupFile = backupManager.saveBackupToFile()
                userPreferences.updateLastBackup(System.currentTimeMillis())
                _uiState.update { 
                    it.copy(
                        isCreatingBackup = false,
                        message = "Kopia zapasowa utworzona: ${backupFile.name}"
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isCreatingBackup = false,
                        error = "Błąd podczas tworzenia kopii zapasowej: ${e.message}"
                    ) 
                }
            }
        }
    }

    fun exportData() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isExporting = true, error = null) }
                val jsonData = backupManager.exportToJson()
                _uiState.update { 
                    it.copy(
                        isExporting = false,
                        exportedData = jsonData,
                        message = "Dane wyeksportowane do JSON"
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isExporting = false,
                        error = "Błąd podczas eksportu: ${e.message}"
                    ) 
                }
            }
        }
    }

    fun restoreFromJson(jsonData: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isRestoring = true, error = null) }
                when (val result = backupManager.restoreFromJson(jsonData)) {
                    is RestoreResult.Success -> {
                        _uiState.update { 
                            it.copy(
                                isRestoring = false,
                                message = "Przywrócono ${result.restoredItemsCount} elementów"
                            ) 
                        }
                    }
                    is RestoreResult.Error -> {
                        _uiState.update { 
                            it.copy(
                                isRestoring = false,
                                error = result.message
                            ) 
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isRestoring = false,
                        error = "Błąd podczas przywracania: ${e.message}"
                    ) 
                }
            }
        }
    }

    fun restoreFromFile(file: File) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isRestoring = true, error = null) }
                when (val result = backupManager.restoreFromFile(file)) {
                    is RestoreResult.Success -> {
                        _uiState.update { 
                            it.copy(
                                isRestoring = false,
                                message = "Przywrócono ${result.restoredItemsCount} elementów z pliku ${file.name}"
                            ) 
                        }
                    }
                    is RestoreResult.Error -> {
                        _uiState.update { 
                            it.copy(
                                isRestoring = false,
                                error = result.message
                            ) 
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isRestoring = false,
                        error = "Błąd podczas przywracania: ${e.message}"
                    ) 
                }
            }
        }
    }

    fun getBackupFiles(): List<File> {
        return backupManager.getBackupFiles()
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null, error = null, exportedData = null) }
    }
}

data class SettingsUiState(
    val isCreatingBackup: Boolean = false,
    val isExporting: Boolean = false,
    val isRestoring: Boolean = false,
    val message: String? = null,
    val error: String? = null,
    val exportedData: String? = null
)