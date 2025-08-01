package com.treningtracker.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.treningtracker.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trening Tracker") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Witaj w Trening Tracker!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            item {
                Text(
                    text = "Wybierz jedną z opcji poniżej:",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            
            item {
                HomeMenuCard(
                    title = "Rozpocznij Trening",
                    description = "Zacznij nową sesję treningową",
                    icon = Icons.Default.PlayArrow,
                    onClick = { navController.navigate(Screen.StartWorkout.route) }
                )
            }
            
            item {
                HomeMenuCard(
                    title = "Ćwiczenia",
                    description = "Zarządzaj swoimi ćwiczeniami",
                    icon = Icons.Default.FitnessCenter,
                    onClick = { navController.navigate(Screen.Exercises.route) }
                )
            }
            
            item {
                HomeMenuCard(
                    title = "Plany Treningowe",
                    description = "Twórz i edytuj plany treningowe",
                    icon = Icons.Default.Assignment,
                    onClick = { navController.navigate(Screen.WorkoutPlans.route) }
                )
            }
            
            item {
                HomeMenuCard(
                    title = "Historia Treningów",
                    description = "Przeglądaj poprzednie treningi",
                    icon = Icons.Default.History,
                    onClick = { navController.navigate(Screen.WorkoutHistory.route) }
                )
            }
            
            item {
                HomeMenuCard(
                    title = "Pomiary Ciała",
                    description = "Śledź swoje postępy",
                    icon = Icons.Default.MonitorWeight,
                    onClick = { navController.navigate(Screen.BodyMeasurements.route) }
                )
            }
            
            item {
                HomeMenuCard(
                    title = "Postępy Ćwiczeń",
                    description = "Wykresy postępów w ćwiczeniach",
                    icon = Icons.Default.TrendingUp,
                    onClick = { navController.navigate(Screen.ExerciseProgressCharts.route) }
                )
            }
            
            item {
                HomeMenuCard(
                    title = "Ustawienia",
                    description = "Konfiguracja aplikacji",
                    icon = Icons.Default.Settings,
                    onClick = { navController.navigate(Screen.Settings.route) }
                )
            }
        }
    }
}

@Composable
fun HomeMenuCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}