package com.treningtracker.data.repository

import com.treningtracker.data.dao.WorkoutPlanDao
import com.treningtracker.data.model.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutPlanRepository @Inject constructor(
    private val workoutPlanDao: WorkoutPlanDao
) {
    
    fun getAllActiveWorkoutPlans(): Flow<List<WorkoutPlan>> = 
        workoutPlanDao.getAllActiveWorkoutPlans()
    
    suspend fun getWorkoutPlanById(id: Long): WorkoutPlan? = 
        workoutPlanDao.getWorkoutPlanById(id)
    
    suspend fun getWorkoutPlanWithExercises(id: Long): WorkoutPlanWithExercises? = 
        workoutPlanDao.getWorkoutPlanWithExercises(id)
    
    suspend fun insertWorkoutPlan(workoutPlan: WorkoutPlan): Long = 
        workoutPlanDao.insertWorkoutPlan(workoutPlan)
    
    suspend fun updateWorkoutPlan(workoutPlan: WorkoutPlan) = 
        workoutPlanDao.updateWorkoutPlan(workoutPlan)
    
    suspend fun deleteWorkoutPlan(workoutPlan: WorkoutPlan) = 
        workoutPlanDao.deleteWorkoutPlan(workoutPlan)
    
    suspend fun deactivateWorkoutPlan(id: Long) = 
        workoutPlanDao.deactivateWorkoutPlan(id)
    
    suspend fun insertWorkoutPlanExercise(workoutPlanExercise: WorkoutPlanExercise): Long = 
        workoutPlanDao.insertWorkoutPlanExercise(workoutPlanExercise)
    
    suspend fun deleteWorkoutPlanExercise(workoutPlanExercise: WorkoutPlanExercise) = 
        workoutPlanDao.deleteWorkoutPlanExercise(workoutPlanExercise)
    
    suspend fun deleteAllExercisesFromPlan(planId: Long) = 
        workoutPlanDao.deleteAllExercisesFromPlan(planId)
}