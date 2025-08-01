package com.treningtracker.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.treningtracker.data.model.WorkoutWithExercises
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutHistoryScreen(
    navController: NavController,
    viewModel: WorkoutHistoryViewModel = hiltViewModel()
) {
    val workouts by viewModel.workouts.collectAsStateWithLifecycle()
    val workoutsByDate by viewModel.workoutsByDate.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var viewMode by remember { mutableStateOf(ViewMode.LIST) }

    // Show snackbar for messages
    LaunchedEffect(uiState.message, uiState.error) {
        if (uiState.message != null || uiState.error != null) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historia treningów") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wstecz")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { 
                            viewMode = if (viewMode == ViewMode.LIST) ViewMode.CALENDAR else ViewMode.LIST
                        }
                    ) {
                        Icon(
                            if (viewMode == ViewMode.LIST) Icons.Default.CalendarMonth else Icons.Default.List,
                            contentDescription = if (viewMode == ViewMode.LIST) "Widok kalendarza" else "Widok listy"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (workouts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.FitnessCenter,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Brak treningów",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "Rozpocznij pierwszy trening, aby zobaczyć historię",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            when (viewMode) {
                ViewMode.LIST -> {
                    WorkoutListView(
                        workouts = workouts,
                        onWorkoutClick = { workout ->
                            navController.navigate("workout_details/${workout.workout.id}")
                        },
                        onDeleteWorkout = viewModel::deleteWorkout,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
                ViewMode.CALENDAR -> {
                    WorkoutCalendarView(
                        workoutsByDate = workoutsByDate,
                        onDateClick = { date ->
                            // Navigate to date-specific workout list or show details
                            val workoutsForDate = viewModel.getWorkoutsForDate(date)
                            if (workoutsForDate.size == 1) {
                                navController.navigate("workout_details/${workoutsForDate.first().workout.id}")
                            } else if (workoutsForDate.size > 1) {
                                // Could navigate to a date-specific list view
                                // For now, navigate to the first workout
                                navController.navigate("workout_details/${workoutsForDate.first().workout.id}")
                            }
                        },
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}

enum class ViewMode {
    LIST, CALENDAR
}

@Composable
fun WorkoutListView(
    workouts: List<WorkoutWithExercises>,
    onWorkoutClick: (WorkoutWithExercises) -> Unit,
    onDeleteWorkout: (com.treningtracker.data.model.Workout) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(workouts) { workout ->
            WorkoutHistoryCard(
                workout = workout,
                onClick = { onWorkoutClick(workout) },
                onDelete = { onDeleteWorkout(workout.workout) }
            )
        }
    }
}

@Composable
fun WorkoutHistoryCard(
    workout: WorkoutWithExercises,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = workout.workout.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${dateFormat.format(Date(workout.workout.date))} • ${timeFormat.format(Date(workout.workout.date))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Usuń")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Show workout summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${workout.workoutExercises.size} ćwiczeń",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${workout.workoutExercises.sumOf { it.sets.size }} serii",
                    style = MaterialTheme.typography.bodyMedium
                )
                workout.workout.duration?.let { duration ->
                    Text(
                        text = "${duration / 60}min",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Show exercise names (first few)
            if (workout.workoutExercises.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                val exerciseNames = workout.workoutExercises.take(3).map { it.exercise.name }
                val displayText = if (workout.workoutExercises.size > 3) {
                    exerciseNames.joinToString(", ") + " i ${workout.workoutExercises.size - 3} więcej"
                } else {
                    exerciseNames.joinToString(", ")
                }
                Text(
                    text = displayText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Usuń trening") },
            text = { Text("Czy na pewno chcesz usunąć ten trening? Ta operacja jest nieodwracalna.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Usuń")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Anuluj")
                }
            }
        )
    }
}

@Composable
fun WorkoutCalendarView(
    workoutsByDate: Map<Long, List<WorkoutWithExercises>>,
    onDateClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Group by month for better organization
        val workoutsByMonth = workoutsByDate.entries.groupBy { (date, _) ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = date
            calendar.get(Calendar.YEAR) to calendar.get(Calendar.MONTH)
        }

        workoutsByMonth.forEach { (yearMonth, dateEntries) ->
            item {
                val (year, month) = yearMonth
                val monthFormat = SimpleDateFormat("LLLL yyyy", Locale.getDefault())
                val calendar = Calendar.getInstance()
                calendar.set(year, month, 1)
                
                Text(
                    text = monthFormat.format(calendar.time),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(dateEntries.sortedByDescending { it.key }) { (date, workoutsForDate) ->
                CalendarDateCard(
                    date = date,
                    workouts = workoutsForDate,
                    onClick = { onDateClick(date) }
                )
            }
        }
    }
}

@Composable
fun CalendarDateCard(
    date: Long,
    workouts: List<WorkoutWithExercises>,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
    val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Date circle
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dateFormat.format(Date(date)),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dayFormat.format(Date(date)),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${workouts.size} ${if (workouts.size == 1) "trening" else "treningów"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
                if (workouts.isNotEmpty()) {
                    Text(
                        text = workouts.joinToString(", ") { it.workout.name },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Zobacz szczegóły",
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailsScreen(
    navController: NavController, 
    workoutId: Long,
    viewModel: WorkoutDetailsViewModel = hiltViewModel()
) {
    val workoutWithExercises by viewModel.workoutWithExercises.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(workoutId) {
        viewModel.loadWorkout(workoutId)
    }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Szczegóły treningu") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wstecz")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Usuń trening")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (workoutWithExercises != null) {
            WorkoutDetailsContent(
                workout = workoutWithExercises!!,
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nie znaleziono treningu",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        uiState.error?.let { error ->
            LaunchedEffect(error) {
                // Show error message
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Usuń trening") },
            text = { Text("Czy na pewno chcesz usunąć ten trening? Ta operacja jest nieodwracalna.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteWorkout {
                            navController.navigateUp()
                        }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Usuń")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Anuluj")
                }
            }
        )
    }
}

@Composable
fun WorkoutDetailsContent(
    workout: WorkoutWithExercises,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Workout header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = workout.workout.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${dateFormat.format(Date(workout.workout.date))} • ${timeFormat.format(Date(workout.workout.date))}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Workout statistics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        WorkoutStatItem(
                            label = "Ćwiczenia",
                            value = workout.workoutExercises.size.toString()
                        )
                        WorkoutStatItem(
                            label = "Serie",
                            value = workout.workoutExercises.sumOf { it.sets.size }.toString()
                        )
                        workout.workout.duration?.let { duration ->
                            WorkoutStatItem(
                                label = "Czas",
                                value = "${duration / 60}min"
                            )
                        }
                    }
                }
            }
        }

        // Exercises
        items(workout.workoutExercises) { workoutExercise ->
            ExerciseDetailsCard(workoutExercise = workoutExercise)
        }
    }
}

@Composable
fun WorkoutStatItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
fun ExerciseDetailsCard(
    workoutExercise: com.treningtracker.data.model.WorkoutExerciseWithDetails
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = workoutExercise.exercise.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            if (workoutExercise.sets.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                
                // Sets header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Seria",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Powtórzenia",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    if (workoutExercise.exercise.usesWeight) {
                        Text(
                            text = "Ciężar (kg)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                
                // Sets data
                workoutExercise.sets.sortedBy { it.setNumber }.forEach { set ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = set.setNumber.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = set.reps.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        if (workoutExercise.exercise.usesWeight) {
                            Text(
                                text = set.weight?.toString() ?: "-",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                    if (set != workoutExercise.sets.last()) {
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
                
                // Exercise summary
                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Łącznie: ${workoutExercise.sets.size} ${if (workoutExercise.sets.size == 1) "seria" else "serii"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "Powtórzenia: ${workoutExercise.sets.sumOf { it.reps }}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                
                if (workoutExercise.exercise.usesWeight) {
                    val maxWeight = workoutExercise.sets.mapNotNull { it.weight }.maxOrNull()
                    val totalVolume = workoutExercise.sets.sumOf { 
                        (it.weight ?: 0f) * it.reps 
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Maks. ciężar: ${maxWeight ?: 0} kg",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = "Objętość: ${totalVolume.toInt()} kg",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Brak serii",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}