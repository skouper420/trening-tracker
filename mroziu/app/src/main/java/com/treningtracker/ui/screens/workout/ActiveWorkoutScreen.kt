package com.treningtracker.ui.screens.workout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.treningtracker.data.model.ExerciseSet
import com.treningtracker.data.model.WorkoutExerciseWithDetails
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutScreen(
    navController: NavController,
    workoutId: Long,
    viewModel: ActiveWorkoutViewModel = hiltViewModel()
) {
    val workoutWithExercises by viewModel.workoutWithExercises.collectAsState()
    val elapsedTime by viewModel.elapsedTime.collectAsState()
    var showFinishDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(workoutId) {
        viewModel.loadWorkout(workoutId)
        viewModel.startTimer()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(workoutWithExercises?.workout?.name ?: "Trening")
                        Text(
                            text = formatTime(elapsedTime),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wstecz")
                    }
                },
                actions = {
                    IconButton(onClick = { showFinishDialog = true }) {
                        Icon(Icons.Default.Check, contentDescription = "Zakończ trening")
                    }
                }
            )
        }
    ) { paddingValues ->
        workoutWithExercises?.let { workout ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(workout.workoutExercises) { workoutExercise ->
                    ExerciseCard(
                        workoutExercise = workoutExercise,
                        onAddSet = { reps, weight ->
                            viewModel.addSet(workoutExercise.workoutExercise.id, reps, weight)
                        },
                        onUpdateSet = { set ->
                            viewModel.updateSet(set)
                        },
                        onDeleteSet = { set ->
                            viewModel.deleteSet(set)
                        }
                    )
                }
            }
        }
    }
    
    if (showFinishDialog) {
        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            title = { Text("Zakończ trening") },
            text = { Text("Czy na pewno chcesz zakończyć trening?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.finishWorkout {
                            navController.popBackStack()
                        }
                        showFinishDialog = false
                    }
                ) {
                    Text("Zakończ")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinishDialog = false }) {
                    Text("Anuluj")
                }
            }
        )
    }
}

@Composable
fun ExerciseCard(
    workoutExercise: WorkoutExerciseWithDetails,
    onAddSet: (Int, Float?) -> Unit,
    onUpdateSet: (ExerciseSet) -> Unit,
    onDeleteSet: (ExerciseSet) -> Unit
) {
    var reps by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Exercise header
            Text(
                text = workoutExercise.exercise.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Sets list
            if (workoutExercise.sets.isNotEmpty()) {
                Text(
                    text = "Serie:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                workoutExercise.sets.forEachIndexed { index, set ->
                    SetRow(
                        setNumber = index + 1,
                        set = set,
                        usesWeight = workoutExercise.exercise.usesWeight,
                        onUpdateSet = onUpdateSet,
                        onDeleteSet = onDeleteSet
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Add new set section
            Text(
                text = "Dodaj serię:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reps input
                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it },
                    label = { Text("Powtórzenia") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                
                // Weight input (if exercise uses weight)
                if (workoutExercise.exercise.usesWeight) {
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Waga (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Quick add buttons for reps
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickAddButton(
                    text = "+1",
                    onClick = { 
                        val currentReps = reps.toIntOrNull() ?: 0
                        reps = (currentReps + 1).toString()
                    }
                )
                QuickAddButton(
                    text = "+5",
                    onClick = { 
                        val currentReps = reps.toIntOrNull() ?: 0
                        reps = (currentReps + 5).toString()
                    }
                )
                QuickAddButton(
                    text = "+10",
                    onClick = { 
                        val currentReps = reps.toIntOrNull() ?: 0
                        reps = (currentReps + 10).toString()
                    }
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Add set button
                Button(
                    onClick = {
                        val repsInt = reps.toIntOrNull()
                        val weightFloat = if (workoutExercise.exercise.usesWeight) {
                            weight.toFloatOrNull()
                        } else null
                        
                        if (repsInt != null && repsInt > 0) {
                            onAddSet(repsInt, weightFloat)
                            reps = ""
                            if (workoutExercise.exercise.usesWeight) {
                                // Keep weight for next set
                            }
                        }
                    },
                    enabled = reps.toIntOrNull()?.let { it > 0 } == true
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Dodaj")
                }
            }
        }
    }
}

@Composable
fun SetRow(
    setNumber: Int,
    set: ExerciseSet,
    usesWeight: Boolean,
    onUpdateSet: (ExerciseSet) -> Unit,
    onDeleteSet: (ExerciseSet) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editReps by remember { mutableStateOf(set.reps.toString()) }
    var editWeight by remember { mutableStateOf(set.weight?.toString() ?: "") }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (set.isCompleted) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$setNumber.",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(32.dp)
            )
            
            if (isEditing) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = editReps,
                        onValueChange = { editReps = it },
                        label = { Text("Powt.") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (usesWeight) {
                        OutlinedTextField(
                            value = editWeight,
                            onValueChange = { editWeight = it },
                            label = { Text("Waga") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                IconButton(
                    onClick = {
                        val newReps = editReps.toIntOrNull()
                        val newWeight = if (usesWeight) editWeight.toFloatOrNull() else null
                        
                        if (newReps != null && newReps > 0) {
                            onUpdateSet(set.copy(reps = newReps, weight = newWeight))
                            isEditing = false
                        }
                    }
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Zapisz")
                }
            } else {
                Text(
                    text = if (usesWeight && set.weight != null) {
                        "${set.reps} × ${set.weight}kg"
                    } else {
                        "${set.reps} powtórzeń"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                
                Row {
                    Checkbox(
                        checked = set.isCompleted,
                        onCheckedChange = { isCompleted ->
                            onUpdateSet(set.copy(isCompleted = isCompleted))
                        }
                    )
                    
                    IconButton(onClick = { isEditing = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edytuj")
                    }
                    
                    IconButton(onClick = { onDeleteSet(set) }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Usuń",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickAddButton(
    text: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.size(48.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}

fun formatTime(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, secs)
    } else {
        String.format("%02d:%02d", minutes, secs)
    }
}