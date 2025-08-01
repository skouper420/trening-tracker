package com.treningtracker.ui.screens.workoutplans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treningtracker.data.model.WorkoutPlan
import com.treningtracker.data.model.WorkoutPlanWithExercises
import com.treningtracker.data.repository.WorkoutPlanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutPlansViewModel @Inject constructor(
    private val workoutPlanRepository: WorkoutPlanRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutPlansUiState())
    val uiState: StateFlow<WorkoutPlansUiState> = _uiState.asStateFlow()

    val workoutPlans = workoutPlanRepository.getAllActiveWorkoutPlans()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteWorkoutPlan(workoutPlan: WorkoutPlan) {
        viewModelScope.launch {
            try {
                workoutPlanRepository.deleteWorkoutPlan(workoutPlan)
                _uiState.update { it.copy(message = "Plan treningowy został usunięty") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Błąd podczas usuwania planu: ${e.message}") }
            }
        }
    }

    fun deactivateWorkoutPlan(planId: Long) {
        viewModelScope.launch {
            try {
                workoutPlanRepository.deactivateWorkoutPlan(planId)
                _uiState.update { it.copy(message = "Plan treningowy został dezaktywowany") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Błąd podczas dezaktywacji planu: ${e.message}") }
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null, error = null) }
    }
}

data class WorkoutPlansUiState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val error: String? = null
)