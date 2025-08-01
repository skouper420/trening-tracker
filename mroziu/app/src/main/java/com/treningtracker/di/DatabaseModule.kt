package com.treningtracker.di

import android.content.Context
import androidx.room.Room
import com.treningtracker.data.dao.*
import com.treningtracker.data.database.WorkoutDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideWorkoutDatabase(@ApplicationContext context: Context): WorkoutDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            WorkoutDatabase::class.java,
            "workout_database"
        ).build()
    }
    
    @Provides
    fun provideExerciseDao(database: WorkoutDatabase): ExerciseDao = database.exerciseDao()
    
    @Provides
    fun provideWorkoutDao(database: WorkoutDatabase): WorkoutDao = database.workoutDao()
    
    @Provides
    fun provideWorkoutPlanDao(database: WorkoutDatabase): WorkoutPlanDao = database.workoutPlanDao()
    
    @Provides
    fun provideBodyMeasurementDao(database: WorkoutDatabase): BodyMeasurementDao = database.bodyMeasurementDao()
}