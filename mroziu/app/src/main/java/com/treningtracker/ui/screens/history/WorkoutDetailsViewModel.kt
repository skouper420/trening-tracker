package com.treningtracker.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treningtracker.data.model.WorkoutWithExercises
import com.treningtracker.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutDetailsViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutDetailsUiState())
    val uiState: StateFlow<WorkoutDetailsUiState> = _uiState.asStateFlow()

    private val _workoutWithExercises = MutableStateFlow<WorkoutWithExercises?>(null)
    val workoutWithExercises: StateFlow<WorkoutWithExercises?> = _workoutWithExercises.asStateFlow()

    fun loadWorkout(workoutId: Long) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                val workout = workoutRepository.getWorkoutWithExercises(workoutId)
                if (workout != null) {
                    _workoutWithExercises.value = workout
                    _uiState.update { it.copy(isLoading = false) }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = "Nie znaleziono treningu"
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

    fun deleteWorkout(onSuccess: () -> Unit) {
        val workout = _workoutWithExercises.value?.workout
        if (workout != null) {
            viewModelScope.launch {
                try {
                    workoutRepository.deleteWorkout(workout)
                    onSuccess()
                } catch (e: Exception) {
                    _uiState.update { 
                        it.copy(error = "Błąd podczas usuwania: ${e.message}") 
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class WorkoutDetailsUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)