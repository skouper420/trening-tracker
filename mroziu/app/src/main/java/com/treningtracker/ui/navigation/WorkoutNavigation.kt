package com.treningtracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.treningtracker.ui.screens.home.HomeScreen
import com.treningtracker.ui.screens.exercises.ExercisesScreen
import com.treningtracker.ui.screens.exercises.AddExerciseScreen
import com.treningtracker.ui.screens.exercises.EditExerciseScreen
import com.treningtracker.ui.screens.workoutplans.WorkoutPlansScreen
import com.treningtracker.ui.screens.workoutplans.AddWorkoutPlanScreen
import com.treningtracker.ui.screens.workoutplans.EditWorkoutPlanScreen
import com.treningtracker.ui.screens.workout.StartWorkoutScreen
import com.treningtracker.ui.screens.workout.ActiveWorkoutScreen
import com.treningtracker.ui.screens.workout.WorkoutFromPlanScreen
import com.treningtracker.ui.screens.history.WorkoutHistoryScreen
import com.treningtracker.ui.screens.history.WorkoutDetailsScreen
import com.treningtracker.ui.screens.measurements.BodyMeasurementsScreen
import com.treningtracker.ui.screens.measurements.AddBodyMeasurementScreen
import com.treningtracker.ui.screens.measurements.EditBodyMeasurementScreen
import com.treningtracker.ui.screens.measurements.MeasurementChartsScreen
import com.treningtracker.ui.screens.exercises.ExerciseHistoryScreen
import com.treningtracker.ui.screens.charts.ExerciseProgressChartsScreen
import com.treningtracker.ui.screens.settings.SettingsScreen

@Composable
fun WorkoutNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        
        composable(Screen.Exercises.route) {
            ExercisesScreen(navController = navController)
        }
        
        composable(Screen.AddExercise.route) {
            AddExerciseScreen(navController = navController)
        }
        
        composable(
            Screen.EditExercise.route,
            arguments = listOf(navArgument("exerciseId") { type = NavType.LongType })
        ) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getLong("exerciseId") ?: 0L
            EditExerciseScreen(
                navController = navController,
                exerciseId = exerciseId
            )
        }
        
        composable(Screen.WorkoutPlans.route) {
            WorkoutPlansScreen(navController = navController)
        }
        
        composable(Screen.AddWorkoutPlan.route) {
            AddWorkoutPlanScreen(navController = navController)
        }
        
        composable(
            Screen.EditWorkoutPlan.route,
            arguments = listOf(navArgument("planId") { type = NavType.LongType })
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getLong("planId") ?: 0L
            EditWorkoutPlanScreen(
                navController = navController,
                planId = planId
            )
        }
        
        composable(Screen.StartWorkout.route) {
            StartWorkoutScreen(navController = navController)
        }
        
        composable(
            Screen.ActiveWorkout.route,
            arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: 0L
            ActiveWorkoutScreen(
                navController = navController,
                workoutId = workoutId
            )
        }
        
        composable(
            Screen.WorkoutFromPlan.route,
            arguments = listOf(navArgument("planId") { type = NavType.LongType })
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getLong("planId") ?: 0L
            WorkoutFromPlanScreen(
                navController = navController,
                planId = planId
            )
        }
        
        composable(Screen.WorkoutHistory.route) {
            WorkoutHistoryScreen(navController = navController)
        }
        
        composable(
            Screen.WorkoutDetails.route,
            arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: 0L
            WorkoutDetailsScreen(
                navController = navController,
                workoutId = workoutId
            )
        }
        
        composable(Screen.BodyMeasurements.route) {
            BodyMeasurementsScreen(navController = navController)
        }
        
        composable(Screen.AddBodyMeasurement.route) {
            AddBodyMeasurementScreen(navController = navController)
        }
        
        composable(
            Screen.EditBodyMeasurement.route,
            arguments = listOf(navArgument("measurementId") { type = NavType.LongType })
        ) { backStackEntry ->
            val measurementId = backStackEntry.arguments?.getLong("measurementId") ?: 0L
            EditBodyMeasurementScreen(
                navController = navController,
                measurementId = measurementId
            )
        }
        
        composable(Screen.MeasurementCharts.route) {
            MeasurementChartsScreen(navController = navController)
        }
        
        composable(
            Screen.ExerciseHistory.route,
            arguments = listOf(navArgument("exerciseId") { type = NavType.LongType })
        ) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getLong("exerciseId") ?: 0L
            ExerciseHistoryScreen(
                navController = navController,
                exerciseId = exerciseId
            )
        }
        
        composable(Screen.ExerciseProgressCharts.route) {
            ExerciseProgressChartsScreen(navController = navController)
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
}