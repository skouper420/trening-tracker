package com.treningtracker.ui.screens.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treningtracker.data.model.ExerciseSet
import com.treningtracker.data.model.WorkoutWithExercises
import com.treningtracker.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActiveWorkoutViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {
    
    private val _workoutWithExercises = MutableStateFlow<WorkoutWithExercises?>(null)
    val workoutWithExercises: StateFlow<WorkoutWithExercises?> = _workoutWithExercises.asStateFlow()
    
    private val _elapsedTime = MutableStateFlow(0)
    val elapsedTime: StateFlow<Int> = _elapsedTime.asStateFlow()
    
    private var startTime: Long = 0
    private var isTimerRunning = false
    
    fun loadWorkout(workoutId: Long) {
        viewModelScope.launch {
            val workout = workoutRepository.getWorkoutWithExercises(workoutId)
            _workoutWithExercises.value = workout
            startTime = workout?.workout?.startTime ?: System.currentTimeMillis()
        }
    }
    
    fun startTimer() {
        if (!isTimerRunning) {
            isTimerRunning = true
            viewModelScope.launch {
                while (isTimerRunning) {
                    val currentTime = System.currentTimeMillis()
                    val elapsed = ((currentTime - startTime) / 1000).toInt()
                    _elapsedTime.value = elapsed
                    delay(1000)
                }
            }
        }
    }
    
    fun stopTimer() {
        isTimerRunning = false
    }
    
    fun addSet(workoutExerciseId: Long, reps: Int, weight: Float?) {
        viewModelScope.launch {
            try {
                val currentSets = workoutRepository.getSetsForWorkoutExercise(workoutExerciseId)
                val setNumber = currentSets.size + 1
                
                val newSet = ExerciseSet(
                    workoutExerciseId = workoutExerciseId,
                    setNumber = setNumber,
                    reps = reps,
                    weight = weight,
                    isCompleted = false
                )
                
                workoutRepository.insertExerciseSet(newSet)
                refreshWorkout()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun updateSet(set: ExerciseSet) {
        viewModelScope.launch {
            try {
                workoutRepository.updateExerciseSet(set)
                refreshWorkout()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun deleteSet(set: ExerciseSet) {
        viewModelScope.launch {
            try {
                workoutRepository.deleteExerciseSet(set)
                refreshWorkout()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun finishWorkout(onFinished: () -> Unit) {
        viewModelScope.launch {
            try {
                val currentWorkout = _workoutWithExercises.value?.workout
                if (currentWorkout != null) {
                    val finishedWorkout = currentWorkout.copy(
                        endTime = System.currentTimeMillis()
                    )
                    workoutRepository.updateWorkout(finishedWorkout)
                }
                stopTimer()
                onFinished()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    private suspend fun refreshWorkout() {
        val currentWorkout = _workoutWithExercises.value
        if (currentWorkout != null) {
            val refreshedWorkout = workoutRepository.getWorkoutWithExercises(currentWorkout.workout.id)
            _workoutWithExercises.value = refreshedWorkout
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}