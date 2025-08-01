package com.treningtracker.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class WorkoutWithExercises(
    @Embedded val workout: Workout,
    @Relation(
        entity = WorkoutExercise::class,
        parentColumn = "id",
        entityColumn = "workoutId"
    )
    val workoutExercises: List<WorkoutExerciseWithDetails>
)

data class WorkoutExerciseWithDetails(
    @Embedded val workoutExercise: WorkoutExercise,
    @Relation(
        parentColumn = "exerciseId",
        entityColumn = "id"
    )
    val exercise: Exercise,
    @Relation(
        parentColumn = "id",
        entityColumn = "workoutExerciseId"
    )
    val sets: List<ExerciseSet>
)

data class WorkoutPlanWithExercises(
    @Embedded val workoutPlan: WorkoutPlan,
    @Relation(
        entity = WorkoutPlanExercise::class,
        parentColumn = "id",
        entityColumn = "workoutPlanId"
    )
    val planExercises: List<WorkoutPlanExerciseWithDetails>
)

data class WorkoutPlanExerciseWithDetails(
    @Embedded val workoutPlanExercise: WorkoutPlanExercise,
    @Relation(
        parentColumn = "exerciseId",
        entityColumn = "id"
    )
    val exercise: Exercise
)

data class ExerciseWithHistory(
    @Embedded val exercise: Exercise,
    @Relation(
        entity = WorkoutExercise::class,
        parentColumn = "id",
        entityColumn = "exerciseId"
    )
    val workoutExercises: List<WorkoutExerciseWithSets>
)

data class WorkoutExerciseWithSets(
    @Embedded val workoutExercise: WorkoutExercise,
    @Relation(
        parentColumn = "id",
        entityColumn = "workoutExerciseId"
    )
    val sets: List<ExerciseSet>
)