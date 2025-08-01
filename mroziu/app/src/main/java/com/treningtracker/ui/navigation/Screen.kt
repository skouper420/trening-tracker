package com.treningtracker.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Exercises : Screen("exercises")
    object AddExercise : Screen("add_exercise")
    object EditExercise : Screen("edit_exercise/{exerciseId}") {
        fun createRoute(exerciseId: Long) = "edit_exercise/$exerciseId"
    }
    object WorkoutPlans : Screen("workout_plans")
    object AddWorkoutPlan : Screen("add_workout_plan")
    object EditWorkoutPlan : Screen("edit_workout_plan/{planId}") {
        fun createRoute(planId: Long) = "edit_workout_plan/$planId"
    }
    object StartWorkout : Screen("start_workout")
    object ActiveWorkout : Screen("active_workout/{workoutId}") {
        fun createRoute(workoutId: Long) = "active_workout/$workoutId"
    }
    object WorkoutFromPlan : Screen("workout_from_plan/{planId}") {
        fun createRoute(planId: Long) = "workout_from_plan/$planId"
    }
    object WorkoutHistory : Screen("workout_history")
    object WorkoutDetails : Screen("workout_details/{workoutId}") {
        fun createRoute(workoutId: Long) = "workout_details/$workoutId"
    }
    object BodyMeasurements : Screen("body_measurements")
    object AddBodyMeasurement : Screen("add_body_measurement")
    object EditBodyMeasurement : Screen("edit_body_measurement/{measurementId}") {
        fun createRoute(measurementId: Long) = "edit_body_measurement/$measurementId"
    }
    object MeasurementCharts : Screen("measurement_charts")
    object ExerciseHistory : Screen("exercise_history/{exerciseId}") {
        fun createRoute(exerciseId: Long) = "exercise_history/$exerciseId"
    }
    object ExerciseProgressCharts : Screen("exercise_progress_charts")
    object Settings : Screen("settings")
}