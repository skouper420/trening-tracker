package com.treningtracker.di

import android.content.Context
import com.treningtracker.data.backup.BackupManager
import com.treningtracker.data.preferences.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Provides
    @Singleton
    fun provideUserPreferences(
        @ApplicationContext context: Context
    ): UserPreferences {
        return UserPreferences(context)
    }

    @Provides
    @Singleton
    fun provideBackupManager(
        @ApplicationContext context: Context,
        exerciseRepository: com.treningtracker.data.repository.ExerciseRepository,
        workoutRepository: com.treningtracker.data.repository.WorkoutRepository,
        workoutPlanRepository: com.treningtracker.data.repository.WorkoutPlanRepository,
        bodyMeasurementRepository: com.treningtracker.data.repository.BodyMeasurementRepository
    ): BackupManager {
        return BackupManager(
            context,
            exerciseRepository,
            workoutRepository,
            workoutPlanRepository,
            bodyMeasurementRepository
        )
    }
}