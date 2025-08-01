package com.treningtracker.data.repository

import com.treningtracker.data.dao.ExerciseDao
import com.treningtracker.data.model.Exercise
import com.treningtracker.data.model.ExerciseWithHistory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseRepository @Inject constructor(
    private val exerciseDao: ExerciseDao
) {
    
    fun getAllActiveExercises(): Flow<List<Exercise>> = exerciseDao.getAllActiveExercises()
    
    suspend fun getExerciseById(id: Long): Exercise? = exerciseDao.getExerciseById(id)
    
    suspend fun insertExercise(exercise: Exercise): Long = exerciseDao.insertExercise(exercise)
    
    suspend fun updateExercise(exercise: Exercise) = exerciseDao.updateExercise(exercise)
    
    suspend fun deleteExercise(exercise: Exercise) = exerciseDao.deleteExercise(exercise)
    
    suspend fun deactivateExercise(id: Long) = exerciseDao.deactivateExercise(id)
    
    suspend fun getExerciseWithHistory(exerciseId: Long): ExerciseWithHistory? = 
        exerciseDao.getExerciseWithHistory(exerciseId)
    
    fun searchExercises(searchQuery: String): Flow<List<Exercise>> = 
        exerciseDao.searchExercises(searchQuery)
}