package com.treningtracker.ui.screens.exercises

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.treningtracker.data.model.Exercise

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExerciseScreen(
    navController: NavController,
    viewModel: AddExerciseViewModel = hiltViewModel()
) {
    var exerciseName by remember { mutableStateOf("") }
    var usesWeight by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dodaj ćwiczenie") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wstecz")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = exerciseName,
                onValueChange = { exerciseName = it },
                label = { Text("Nazwa ćwiczenia") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = usesWeight,
                    onCheckedChange = { usesWeight = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ćwiczenie z obciążeniem")
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = {
                    if (exerciseName.isNotBlank()) {
                        val exercise = Exercise(
                            name = exerciseName.trim(),
                            usesWeight = usesWeight
                        )
                        viewModel.addExercise(exercise) {
                            navController.navigateUp()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = exerciseName.isNotBlank()
            ) {
                Text("Dodaj ćwiczenie")
            }
        }
    }
}