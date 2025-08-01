package com.treningtracker.ui.screens.workoutplans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treningtracker.data.model.Exercise
import com.treningtracker.data.model.WorkoutPlan
import com.treningtracker.data.model.WorkoutPlanExercise
import com.treningtracker.data.repository.ExerciseRepository
import com.treningtracker.data.repository.WorkoutPlanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditWorkoutPlanViewModel @Inject constructor(
    private val workoutPlanRepository: WorkoutPlanRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditWorkoutPlanUiState())
    val uiState: StateFlow<EditWorkoutPlanUiState> = _uiState.asStateFlow()

    val availableExercises = exerciseRepository.getAllExercises()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun loadWorkoutPlan(planId: Long) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                val planWithExercises = workoutPlanRepository.getWorkoutPlanWithExercises(planId)
                if (planWithExercises != null) {
                    val selectedExercises = planWithExercises.planExercises
                        .sortedBy { it.workoutPlanExercise.orderIndex }
                        .map { it.exercise }
                    
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            planId = planId,
                            name = planWithExercises.workoutPlan.name,
                            description = planWithExercises.workoutPlan.description,
                            selectedExercises = selectedExercises
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = "Nie znaleziono planu treningowego"
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

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun toggleExerciseSelection(exercise: Exercise) {
        _uiState.update { currentState ->
            val currentExercises = currentState.selectedExercises.toMutableList()
            if (currentExercises.any { it.id == exercise.id }) {
                currentExercises.removeAll { it.id == exercise.id }
            } else {
                currentExercises.add(exercise)
            }
            currentState.copy(selectedExercises = currentExercises)
        }
    }

    fun moveExerciseUp(exercise: Exercise) {
        _uiState.update { currentState ->
            val exercises = currentState.selectedExercises.toMutableList()
            val index = exercises.indexOfFirst { it.id == exercise.id }
            if (index > 0) {
                exercises.removeAt(index)
                exercises.add(index - 1, exercise)
            }
            currentState.copy(selectedExercises = exercises)
        }
    }

    fun moveExerciseDown(exercise: Exercise) {
        _uiState.update { currentState ->
            val exercises = currentState.selectedExercises.toMutableList()
            val index = exercises.indexOfFirst { it.id == exercise.id }
            if (index < exercises.size - 1) {
                exercises.removeAt(index)
                exercises.add(index + 1, exercise)
            }
            currentState.copy(selectedExercises = exercises)
        }
    }

    fun removeExercise(exercise: Exercise) {
        _uiState.update { currentState ->
            val exercises = currentState.selectedExercises.toMutableList()
            exercises.removeAll { it.id == exercise.id }
            currentState.copy(selectedExercises = exercises)
        }
    }

    fun updateWorkoutPlan(onSuccess: () -> Unit) {
        val state = _uiState.value
        
        if (state.planId == null) {
            _uiState.update { it.copy(error = "Błąd: brak ID planu") }
            return
        }

        if (state.name.isBlank()) {
            _uiState.update { it.copy(error = "Nazwa planu jest wymagana") }
            return
        }

        if (state.selectedExercises.isEmpty()) {
            _uiState.update { it.copy(error = "Wybierz przynajmniej jedno ćwiczenie") }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isSaving = true, error = null) }
                
                // Update workout plan
                val workoutPlan = WorkoutPlan(
                    id = state.planId,
                    name = state.name,
                    description = state.description
                )
                
                workoutPlanRepository.updateWorkoutPlan(workoutPlan)
                
                // Remove all existing exercises from plan
                workoutPlanRepository.deleteAllExercisesFromPlan(state.planId)
                
                // Add updated exercises to plan
                state.selectedExercises.forEachIndexed { index, exercise ->
                    val planExercise = WorkoutPlanExercise(
                        workoutPlanId = state.planId,
                        exerciseId = exercise.id,
                        orderIndex = index
                    )
                    workoutPlanRepository.insertWorkoutPlanExercise(planExercise)
                }
                
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

data class EditWorkoutPlanUiState(
    val planId: Long? = null,
    val name: String = "",
    val description: String = "",
    val selectedExercises: List<Exercise> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)