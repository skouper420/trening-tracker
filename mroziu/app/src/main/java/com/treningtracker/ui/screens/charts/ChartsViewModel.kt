package com.treningtracker.ui.screens.charts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treningtracker.data.model.BodyMeasurement
import com.treningtracker.data.model.Exercise
import com.treningtracker.data.model.ExerciseSet
import com.treningtracker.data.repository.BodyMeasurementRepository
import com.treningtracker.data.repository.ExerciseRepository
import com.treningtracker.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChartsViewModel @Inject constructor(
    private val bodyMeasurementRepository: BodyMeasurementRepository,
    private val exerciseRepository: ExerciseRepository,
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChartsUiState())
    val uiState: StateFlow<ChartsUiState> = _uiState.asStateFlow()

    val bodyMeasurements = bodyMeasurementRepository.getAllMeasurements()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val exercises = exerciseRepository.getAllExercises()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Get weight measurements for chart
    suspend fun getWeightMeasurements(): List<BodyMeasurement> {
        return bodyMeasurementRepository.getWeightMeasurements()
    }

    // Get specific body measurement type
    suspend fun getMeasurementsByType(type: MeasurementType): List<BodyMeasurement> {
        return when (type) {
            MeasurementType.WEIGHT -> bodyMeasurementRepository.getWeightMeasurements()
            MeasurementType.WAIST -> bodyMeasurementRepository.getWaistMeasurements()
            MeasurementType.CHEST -> bodyMeasurementRepository.getChestMeasurements()
            MeasurementType.BICEPS -> bodyMeasurementRepository.getBicepsMeasurements()
            MeasurementType.FOREARM -> bodyMeasurementRepository.getForearmMeasurements()
            MeasurementType.THIGH -> bodyMeasurementRepository.getThighMeasurements()
            MeasurementType.CALF -> bodyMeasurementRepository.getCalfMeasurements()
        }
    }

    // Get exercise progression data
    suspend fun getExerciseProgression(exerciseId: Long): List<ExerciseProgressionData> {
        return try {
            val workouts = workoutRepository.getAllWorkoutsWithExercises().first()
            val progressionData = mutableListOf<ExerciseProgressionData>()

            workouts.forEach { workout ->
                workout.workoutExercises
                    .filter { it.exercise.id == exerciseId }
                    .forEach { workoutExercise ->
                        workoutExercise.sets.forEach { set ->
                            progressionData.add(
                                ExerciseProgressionData(
                                    date = workout.workout.date,
                                    weight = set.weight,
                                    reps = set.reps,
                                    volume = (set.weight ?: 0f) * set.reps
                                )
                            )
                        }
                    }
            }

            progressionData.sortedBy { it.date }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = "Błąd podczas ładowania danych: ${e.message}") }
            emptyList()
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class ChartsUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class MeasurementType(val displayName: String, val unit: String) {
    WEIGHT("Waga", "kg"),
    WAIST("Talia", "cm"),
    CHEST("Klatka", "cm"),
    BICEPS("Biceps", "cm"),
    FOREARM("Przedramię", "cm"),
    THIGH("Udo", "cm"),
    CALF("Łydka", "cm")
}

data class ExerciseProgressionData(
    val date: Long,
    val weight: Float?,
    val reps: Int,
    val volume: Float
)