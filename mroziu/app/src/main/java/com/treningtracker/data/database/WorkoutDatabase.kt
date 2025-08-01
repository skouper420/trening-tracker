package com.treningtracker.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.treningtracker.data.dao.*
import com.treningtracker.data.model.*

@Database(
    entities = [
        Exercise::class,
        Workout::class,
        WorkoutExercise::class,
        ExerciseSet::class,
        WorkoutPlan::class,
        WorkoutPlanExercise::class,
        BodyMeasurement::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class WorkoutDatabase : RoomDatabase() {
    
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun workoutPlanDao(): WorkoutPlanDao
    abstract fun bodyMeasurementDao(): BodyMeasurementDao
    
    companion object {
        @Volatile
        private var INSTANCE: WorkoutDatabase? = null
        
        fun getDatabase(context: Context): WorkoutDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WorkoutDatabase::class.java,
                    "workout_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}