package com.treningtracker.data.dao

import androidx.room.*
import com.treningtracker.data.model.Exercise
import com.treningtracker.data.model.ExerciseWithHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    
    @Query("SELECT * FROM exercises WHERE isActive = 1 ORDER BY name ASC")
    fun getAllActiveExercises(): Flow<List<Exercise>>
    
    @Query("SELECT * FROM exercises WHERE id = :id")
    suspend fun getExerciseById(id: Long): Exercise?
    
    @Insert
    suspend fun insertExercise(exercise: Exercise): Long
    
    @Update
    suspend fun updateExercise(exercise: Exercise)
    
    @Delete
    suspend fun deleteExercise(exercise: Exercise)
    
    @Query("UPDATE exercises SET isActive = 0 WHERE id = :id")
    suspend fun deactivateExercise(id: Long)
    
    @Transaction
    @Query("SELECT * FROM exercises WHERE id = :exerciseId")
    suspend fun getExerciseWithHistory(exerciseId: Long): ExerciseWithHistory?
    
    @Query("SELECT * FROM exercises WHERE name LIKE '%' || :searchQuery || '%' AND isActive = 1")
    fun searchExercises(searchQuery: String): Flow<List<Exercise>>
}