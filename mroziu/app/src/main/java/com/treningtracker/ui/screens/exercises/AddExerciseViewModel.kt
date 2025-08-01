package com.treningtracker.ui.screens.exercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treningtracker.data.model.Exercise
import com.treningtracker.data.repository.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddExerciseViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {
    
    fun addExercise(exercise: Exercise, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                exerciseRepository.insertExercise(exercise)
                onSuccess()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}