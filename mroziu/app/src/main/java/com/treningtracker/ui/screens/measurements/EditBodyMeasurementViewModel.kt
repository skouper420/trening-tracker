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
class EditBodyMeasurementViewModel @Inject constructor(
    private val repository: BodyMeasurementRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditMeasurementUiState())
    val uiState: StateFlow<EditMeasurementUiState> = _uiState.asStateFlow()

    fun loadMeasurement(measurementId: Long) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                val measurement = repository.getMeasurementById(measurementId)
                if (measurement != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            measurementId = measurementId,
                            date = measurement.date,
                            weight = measurement.weight?.toString() ?: "",
                            waist = measurement.waist?.toString() ?: "",
                            chest = measurement.chest?.toString() ?: "",
                            biceps = measurement.biceps?.toString() ?: "",
                            forearm = measurement.forearm?.toString() ?: "",
                            thigh = measurement.thigh?.toString() ?: "",
                            calf = measurement.calf?.toString() ?: "",
                            notes = measurement.notes
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = "Nie znaleziono pomiaru"
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = "Błąd podczas ładowania: ${e.message}"
                    ) 
                }
            }
        }
    }

    fun updateWeight(weight: String) {
        _uiState.update { it.copy(weight = weight) }
    }

    fun updateWaist(waist: String) {
        _uiState.update { it.copy(waist = waist) }
    }

    fun updateChest(chest: String) {
        _uiState.update { it.copy(chest = chest) }
    }

    fun updateBiceps(biceps: String) {
        _uiState.update { it.copy(biceps = biceps) }
    }

    fun updateForearm(forearm: String) {
        _uiState.update { it.copy(forearm = forearm) }
    }

    fun updateThigh(thigh: String) {
        _uiState.update { it.copy(thigh = thigh) }
    }

    fun updateCalf(calf: String) {
        _uiState.update { it.copy(calf = calf) }
    }

    fun updateNotes(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    fun updateDate(date: Long) {
        _uiState.update { it.copy(date = date) }
    }

    fun updateMeasurement(onSuccess: () -> Unit) {
        val state = _uiState.value
        
        if (state.measurementId == null) {
            _uiState.update { it.copy(error = "Błąd: brak ID pomiaru") }
            return
        }

        // Validate that at least one measurement is provided
        if (state.weight.isBlank() && state.waist.isBlank() && state.chest.isBlank() && 
            state.biceps.isBlank() && state.forearm.isBlank() && state.thigh.isBlank() && state.calf.isBlank()) {
            _uiState.update { it.copy(error = "Wprowadź przynajmniej jeden pomiar") }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isSaving = true, error = null) }
                
                val measurement = BodyMeasurement(
                    id = state.measurementId,
                    date = state.date,
                    weight = state.weight.toFloatOrNull(),
                    waist = state.waist.toFloatOrNull(),
                    chest = state.chest.toFloatOrNull(),
                    biceps = state.biceps.toFloatOrNull(),
                    forearm = state.forearm.toFloatOrNull(),
                    thigh = state.thigh.toFloatOrNull(),
                    calf = state.calf.toFloatOrNull(),
                    notes = state.notes
                )
                
                repository.updateMeasurement(measurement)
                onSuccess()
                
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isSaving = false, 
                        error = "Błąd podczas zapisywania: ${e.message}"
                    ) 
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class EditMeasurementUiState(
    val measurementId: Long? = null,
    val date: Long = System.currentTimeMillis(),
    val weight: String = "",
    val waist: String = "",
    val chest: String = "",
    val biceps: String = "",
    val forearm: String = "",
    val thigh: String = "",
    val calf: String = "",
    val notes: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)