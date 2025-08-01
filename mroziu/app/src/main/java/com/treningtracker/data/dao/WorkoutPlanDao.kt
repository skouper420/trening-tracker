package com.treningtracker.data.dao

import androidx.room.*
import com.treningtracker.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutPlanDao {
    
    @Query("SELECT * FROM workout_plans WHERE isActive = 1 ORDER BY name ASC")
    fun getAllActiveWorkoutPlans(): Flow<List<WorkoutPlan>>
    
    @Query("SELECT * FROM workout_plans WHERE id = :id")
    suspend fun getWorkoutPlanById(id: Long): WorkoutPlan?
    
    @Transaction
    @Query("SELECT * FROM workout_plans WHERE id = :id")
    suspend fun getWorkoutPlanWithExercises(id: Long): WorkoutPlanWithExercises?
    
    @Insert
    suspend fun insertWorkoutPlan(workoutPlan: WorkoutPlan): Long
    
    @Update
    suspend fun updateWorkoutPlan(workoutPlan: WorkoutPlan)
    
    @Delete
    suspend fun deleteWorkoutPlan(workoutPlan: WorkoutPlan)
    
    @Query("UPDATE workout_plans SET isActive = 0 WHERE id = :id")
    suspend fun deactivateWorkoutPlan(id: Long)
    
    @Insert
    suspend fun insertWorkoutPlanExercise(workoutPlanExercise: WorkoutPlanExercise): Long
    
    @Delete
    suspend fun deleteWorkoutPlanExercise(workoutPlanExercise: WorkoutPlanExercise)
    
    @Query("DELETE FROM workout_plan_exercises WHERE workoutPlanId = :planId")
    suspend fun deleteAllExercisesFromPlan(planId: Long)
}