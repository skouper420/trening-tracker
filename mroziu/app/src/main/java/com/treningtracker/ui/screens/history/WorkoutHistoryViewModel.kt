package com.treningtracker.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treningtracker.data.model.Workout
import com.treningtracker.data.model.WorkoutWithExercises
import com.treningtracker.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class WorkoutHistoryViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutHistoryUiState())
    val uiState: StateFlow<WorkoutHistoryUiState> = _uiState.asStateFlow()

    val workouts = workoutRepository.getAllWorkoutsWithExercises()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Group workouts by date for calendar view
    val workoutsByDate = workouts.map { workoutList ->
        workoutList.groupBy { workout ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = workout.workout.date
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.timeInMillis
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyMap()
    )

    fun deleteWorkout(workout: Workout) {
        viewModelScope.launch {
            try {
                workoutRepository.deleteWorkout(workout)
                _uiState.update { it.copy(message = "Trening został usunięty") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Błąd podczas usuwania treningu: ${e.message}") }
            }
        }
    }

    fun getWorkoutsForDate(date: Long): List<WorkoutWithExercises> {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfDay = calendar.timeInMillis

        return workouts.value.filter { workout ->
            workout.workout.date in startOfDay..endOfDay
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null, error = null) }
    }
}

data class WorkoutHistoryUiState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val error: String? = null
)