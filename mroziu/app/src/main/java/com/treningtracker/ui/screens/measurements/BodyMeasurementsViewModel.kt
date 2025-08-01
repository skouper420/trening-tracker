package com.treningtracker.ui.screens.measurements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treningtracker.data.model.BodyMeasurement
import com.treningtracker.data.repository.BodyMeasurementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BodyMeasurementsViewModel @Inject constructor(
    private val repository: BodyMeasurementRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BodyMeasurementsUiState())
    val uiState: StateFlow<BodyMeasurementsUiState> = _uiState.asStateFlow()

    val measurements = repository.getAllMeasurements()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteMeasurement(measurement: BodyMeasurement) {
        viewModelScope.launch {
            try {
                repository.deleteMeasurement(measurement)
                _uiState.update { it.copy(message = "Pomiar został usunięty") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Błąd podczas usuwania pomiaru: ${e.message}") }
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null, error = null) }
    }
}

data class BodyMeasurementsUiState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val error: String? = null
)