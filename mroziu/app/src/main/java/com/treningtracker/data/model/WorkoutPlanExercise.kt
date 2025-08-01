package com.treningtracker.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Entity(
    tableName = "workout_plan_exercises",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutPlan::class,
            parentColumns = ["id"],
            childColumns = ["workoutPlanId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@Parcelize
data class WorkoutPlanExercise(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val workoutPlanId: Long,
    val exerciseId: Long,
    val orderIndex: Int,
    val suggestedSets: Int = 3,
    val suggestedReps: Int = 10,
    val suggestedWeight: Float? = null
) : Parcelable