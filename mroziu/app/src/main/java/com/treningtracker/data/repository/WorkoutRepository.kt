package com.treningtracker.data.repository

import com.treningtracker.data.dao.WorkoutDao
import com.treningtracker.data.model.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutRepository @Inject constructor(
    private val workoutDao: WorkoutDao
) {
    
    fun getAllWorkouts(): Flow<List<Workout>> = workoutDao.getAllWorkouts()
    
    suspend fun getWorkoutById(id: Long): Workout? = workoutDao.getWorkoutById(id)
    
    suspend fun getWorkoutWithExercises(id: Long): WorkoutWithExercises? = 
        workoutDao.getWorkoutWithExercises(id)
    
    fun getAllWorkoutsWithExercises(): Flow<List<WorkoutWithExercises>> = 
        workoutDao.getAllWorkoutsWithExercises()
    
    fun getWorkoutsByDateRange(startDate: Long, endDate: Long): Flow<List<Workout>> = 
        workoutDao.getWorkoutsByDateRange(startDate, endDate)
    
    suspend fun insertWorkout(workout: Workout): Long = workoutDao.insertWorkout(workout)
    
    suspend fun updateWorkout(workout: Workout) = workoutDao.updateWorkout(workout)
    
    suspend fun deleteWorkout(workout: Workout) = workoutDao.deleteWorkout(workout)
    
    suspend fun insertWorkoutExercise(workoutExercise: WorkoutExercise): Long = 
        workoutDao.insertWorkoutExercise(workoutExercise)
    
    suspend fun insertExerciseSet(exerciseSet: ExerciseSet): Long = 
        workoutDao.insertExerciseSet(exerciseSet)
    
    suspend fun updateExerciseSet(exerciseSet: ExerciseSet) = 
        workoutDao.updateExerciseSet(exerciseSet)
    
    suspend fun deleteExerciseSet(exerciseSet: ExerciseSet) = 
        workoutDao.deleteExerciseSet(exerciseSet)
    
    suspend fun getSetsForWorkoutExercise(workoutExerciseId: Long): List<ExerciseSet> = 
        workoutDao.getSetsForWorkoutExercise(workoutExerciseId)
    
    suspend fun getWorkoutCountForDate(startOfDay: Long, endOfDay: Long): Int = 
        workoutDao.getWorkoutCountForDate(startOfDay, endOfDay)
}