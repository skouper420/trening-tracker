package com.treningtracker.ui.screens.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treningtracker.data.model.Workout
import com.treningtracker.data.model.WorkoutExercise
import com.treningtracker.data.model.WorkoutPlanWithExercises
import com.treningtracker.data.repository.WorkoutPlanRepository
import com.treningtracker.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutFromPlanViewModel @Inject constructor(
    private val workoutPlanRepository: WorkoutPlanRepository,
    private val workoutRepository: WorkoutRepository
) : ViewModel() {
    
    private val _workoutPlan = MutableStateFlow<WorkoutPlanWithExercises?>(null)
    val workoutPlan: StateFlow<WorkoutPlanWithExercises?> = _workoutPlan.asStateFlow()
    
    fun loadWorkoutPlan(planId: Long) {
        viewModelScope.launch {
            try {
                val plan = workoutPlanRepository.getWorkoutPlanWithExercises(planId)
                _workoutPlan.value = plan
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun startWorkoutFromPlan(workoutName: String, onWorkoutCreated: (Long) -> Unit) {
        viewModelScope.launch {
            try {
                val plan = _workoutPlan.value ?: return@launch
                
                val currentTime = System.currentTimeMillis()
                val workout = Workout(
                    name = workoutName,
                    date = currentTime,
                    startTime = currentTime
                )
                
                val workoutId = workoutRepository.insertWorkout(workout)
                
                // Add exercises from plan to workout
                plan.planExercises.forEachIndexed { index, planExercise ->
                    val workoutExercise = WorkoutExercise(
                        workoutId = workoutId,
                        exerciseId = planExercise.exercise.id,
                        orderIndex = index
                    )
                    workoutRepository.insertWorkoutExercise(workoutExercise)
                }
                
                onWorkoutCreated(workoutId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}