package com.treningtracker.ui.screens.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treningtracker.data.model.Exercise
import com.treningtracker.data.model.Workout
import com.treningtracker.data.model.WorkoutExercise
import com.treningtracker.data.repository.ExerciseRepository
import com.treningtracker.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartWorkoutViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val workoutRepository: WorkoutRepository
) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _selectedExercises = MutableStateFlow<List<Exercise>>(emptyList())
    val selectedExercises: StateFlow<List<Exercise>> = _selectedExercises.asStateFlow()
    
    val exercises: StateFlow<List<Exercise>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                exerciseRepository.getAllActiveExercises()
            } else {
                exerciseRepository.searchExercises(query)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun addExercise(exercise: Exercise) {
        val currentList = _selectedExercises.value.toMutableList()
        if (!currentList.contains(exercise)) {
            currentList.add(exercise)
            _selectedExercises.value = currentList
        }
    }
    
    fun removeExercise(exercise: Exercise) {
        val currentList = _selectedExercises.value.toMutableList()
        currentList.remove(exercise)
        _selectedExercises.value = currentList
    }
    
    fun startWorkout(workoutName: String, onWorkoutCreated: (Long) -> Unit) {
        viewModelScope.launch {
            try {
                val currentTime = System.currentTimeMillis()
                val workout = Workout(
                    name = workoutName,
                    date = currentTime,
                    startTime = currentTime
                )
                
                val workoutId = workoutRepository.insertWorkout(workout)
                
                // Add selected exercises to workout
                _selectedExercises.value.forEachIndexed { index, exercise ->
                    val workoutExercise = WorkoutExercise(
                        workoutId = workoutId,
                        exerciseId = exercise.id,
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