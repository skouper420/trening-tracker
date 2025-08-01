package com.treningtracker.data.backup

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.treningtracker.data.model.*
import com.treningtracker.data.repository.*
import kotlinx.coroutines.flow.first
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupManager @Inject constructor(
    private val context: Context,
    private val exerciseRepository: ExerciseRepository,
    private val workoutRepository: WorkoutRepository,
    private val workoutPlanRepository: WorkoutPlanRepository,
    private val bodyMeasurementRepository: BodyMeasurementRepository
) {
    private val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    suspend fun createBackup(): BackupData {
        return BackupData(
            exercises = exerciseRepository.getAllExercises().first(),
            workouts = workoutRepository.getAllWorkoutsWithExercises().first(),
            workoutPlans = workoutPlanRepository.getAllActiveWorkoutPlans().first().map { plan ->
                workoutPlanRepository.getWorkoutPlanWithExercises(plan.id)
            }.filterNotNull(),
            bodyMeasurements = bodyMeasurementRepository.getAllMeasurements().first(),
            exportDate = System.currentTimeMillis(),
            appVersion = "1.0"
        )
    }

    suspend fun exportToJson(): String {
        val backupData = createBackup()
        return gson.toJson(backupData)
    }

    suspend fun saveBackupToFile(): File {
        val backupJson = exportToJson()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
        val fileName = "trening_tracker_backup_${dateFormat.format(Date())}.json"
        
        val backupDir = File(context.getExternalFilesDir(null), "backups")
        if (!backupDir.exists()) {
            backupDir.mkdirs()
        }
        
        val backupFile = File(backupDir, fileName)
        backupFile.writeText(backupJson)
        
        return backupFile
    }

    suspend fun restoreFromJson(jsonData: String): RestoreResult {
        return try {
            val backupData = gson.fromJson(jsonData, BackupData::class.java)
            restoreFromBackupData(backupData)
        } catch (e: Exception) {
            RestoreResult.Error("Błąd podczas przywracania danych: ${e.message}")
        }
    }

    suspend fun restoreFromFile(file: File): RestoreResult {
        return try {
            val jsonData = file.readText()
            restoreFromJson(jsonData)
        } catch (e: Exception) {
            RestoreResult.Error("Błąd podczas odczytu pliku: ${e.message}")
        }
    }

    private suspend fun restoreFromBackupData(backupData: BackupData): RestoreResult {
        return try {
            var restoredCount = 0

            // Restore exercises
            backupData.exercises.forEach { exercise ->
                try {
                    exerciseRepository.insertExercise(exercise.copy(id = 0))
                    restoredCount++
                } catch (e: Exception) {
                    // Exercise might already exist, skip
                }
            }

            // Restore body measurements
            backupData.bodyMeasurements.forEach { measurement ->
                try {
                    bodyMeasurementRepository.insertMeasurement(measurement.copy(id = 0))
                    restoredCount++
                } catch (e: Exception) {
                    // Measurement might already exist, skip
                }
            }

            // Restore workout plans
            backupData.workoutPlans.forEach { planWithExercises ->
                try {
                    val planId = workoutPlanRepository.insertWorkoutPlan(
                        planWithExercises.workoutPlan.copy(id = 0)
                    )
                    
                    planWithExercises.planExercises.forEach { planExercise ->
                        // Find matching exercise by name
                        val exercises = exerciseRepository.getAllExercises().first()
                        val matchingExercise = exercises.find { it.name == planExercise.exercise.name }
                        
                        matchingExercise?.let { exercise ->
                            workoutPlanRepository.insertWorkoutPlanExercise(
                                planExercise.workoutPlanExercise.copy(
                                    id = 0,
                                    workoutPlanId = planId,
                                    exerciseId = exercise.id
                                )
                            )
                        }
                    }
                    restoredCount++
                } catch (e: Exception) {
                    // Plan might already exist, skip
                }
            }

            RestoreResult.Success(restoredCount)
        } catch (e: Exception) {
            RestoreResult.Error("Błąd podczas przywracania: ${e.message}")
        }
    }

    fun getBackupFiles(): List<File> {
        val backupDir = File(context.getExternalFilesDir(null), "backups")
        return if (backupDir.exists()) {
            backupDir.listFiles { file -> file.extension == "json" }?.toList() ?: emptyList()
        } else {
            emptyList()
        }
    }
}

data class BackupData(
    val exercises: List<Exercise>,
    val workouts: List<WorkoutWithExercises>,
    val workoutPlans: List<WorkoutPlanWithExercises>,
    val bodyMeasurements: List<BodyMeasurement>,
    val exportDate: Long,
    val appVersion: String
)

sealed class RestoreResult {
    data class Success(val restoredItemsCount: Int) : RestoreResult()
    data class Error(val message: String) : RestoreResult()
}