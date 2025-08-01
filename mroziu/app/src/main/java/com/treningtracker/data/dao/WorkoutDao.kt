package com.treningtracker.data.dao

import androidx.room.*
import com.treningtracker.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    
    @Query("SELECT * FROM workouts ORDER BY date DESC")
    fun getAllWorkouts(): Flow<List<Workout>>
    
    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getWorkoutById(id: Long): Workout?
    
    @Transaction
    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getWorkoutWithExercises(id: Long): WorkoutWithExercises?
    
    @Transaction
    @Query("SELECT * FROM workouts ORDER BY date DESC")
    fun getAllWorkoutsWithExercises(): Flow<List<WorkoutWithExercises>>
    
    @Query("SELECT * FROM workouts WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getWorkoutsByDateRange(startDate: Long, endDate: Long): Flow<List<Workout>>
    
    @Insert
    suspend fun insertWorkout(workout: Workout): Long
    
    @Update
    suspend fun updateWorkout(workout: Workout)
    
    @Delete
    suspend fun deleteWorkout(workout: Workout)
    
    @Insert
    suspend fun insertWorkoutExercise(workoutExercise: WorkoutExercise): Long
    
    @Insert
    suspend fun insertExerciseSet(exerciseSet: ExerciseSet): Long
    
    @Update
    suspend fun updateExerciseSet(exerciseSet: ExerciseSet)
    
    @Delete
    suspend fun deleteExerciseSet(exerciseSet: ExerciseSet)
    
    @Query("SELECT * FROM exercise_sets WHERE workoutExerciseId = :workoutExerciseId ORDER BY setNumber ASC")
    suspend fun getSetsForWorkoutExercise(workoutExerciseId: Long): List<ExerciseSet>
    
    @Query("SELECT COUNT(*) FROM workouts WHERE date BETWEEN :startOfDay AND :endOfDay")
    suspend fun getWorkoutCountForDate(startOfDay: Long, endOfDay: Long): Int
}