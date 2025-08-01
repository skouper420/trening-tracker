package com.treningtracker.ui.screens.charts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.treningtracker.data.model.Exercise
import com.github.mikephil.charting.data.Entry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseProgressChartsScreen(
    navController: NavController,
    viewModel: ChartsViewModel = hiltViewModel()
) {
    val exercises by viewModel.exercises.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedExercise by remember { mutableStateOf<Exercise?>(null) }

    LaunchedEffect(exercises) {
        if (selectedExercise == null && exercises.isNotEmpty()) {
            selectedExercise = exercises.first()
        }
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
                title = { Text("Postępy ćwiczeń") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wstecz")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (exercises.isEmpty()) {
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
                        Icons.Default.TrendingUp,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Brak ćwiczeń",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "Dodaj ćwiczenia i wykonaj treningi, aby zobaczyć postępy",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Postępy ćwiczeń",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    ExerciseSelector(
                        exercises = exercises,
                        selectedExercise = selectedExercise,
                        onExerciseSelected = { selectedExercise = it }
                    )
                }

                selectedExercise?.let { exercise ->
                    item {
                        ExerciseProgressChart(
                            exercise = exercise,
                            viewModel = viewModel
                        )
                    }

                    item {
                        ExerciseProgressStatistics(
                            exercise = exercise,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }

        uiState.error?.let { error ->
            LaunchedEffect(error) {
                // Error handling could show a snackbar here
            }
        }
    }
}

@Composable
fun ExerciseSelector(
    exercises: List<Exercise>,
    selectedExercise: Exercise?,
    onExerciseSelected: (Exercise) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Wybierz ćwiczenie",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(exercises) { exercise ->
                    FilterChip(
                        onClick = { onExerciseSelected(exercise) },
                        label = { 
                            Text(
                                text = exercise.name,
                                maxLines = 1
                            ) 
                        },
                        selected = selectedExercise?.id == exercise.id
                    )
                }
            }
        }
    }
}

@Composable
fun ExerciseProgressChart(
    exercise: Exercise,
    viewModel: ChartsViewModel
) {
    var progressionData by remember { mutableStateOf<List<ExerciseProgressionData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var chartType by remember { mutableStateOf(ExerciseChartType.WEIGHT) }

    LaunchedEffect(exercise.id) {
        isLoading = true
        progressionData = viewModel.getExerciseProgression(exercise.id)
        isLoading = false
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                Text(
                    text = "Postęp: ${exercise.name}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                if (exercise.usesWeight) {
                    Row {
                        FilterChip(
                            onClick = { chartType = ExerciseChartType.WEIGHT },
                            label = { Text("Ciężar") },
                            selected = chartType == ExerciseChartType.WEIGHT
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        FilterChip(
                            onClick = { chartType = ExerciseChartType.VOLUME },
                            label = { Text("Objętość") },
                            selected = chartType == ExerciseChartType.VOLUME
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (progressionData.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Brak danych dla tego ćwiczenia",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                val entries = when (chartType) {
                    ExerciseChartType.WEIGHT -> {
                        if (exercise.usesWeight) {
                            progressionData.mapIndexed { index, data ->
                                Entry(index.toFloat(), data.weight ?: 0f)
                            }.filter { it.y > 0f }
                        } else {
                            progressionData.mapIndexed { index, data ->
                                Entry(index.toFloat(), data.reps.toFloat())
                            }
                        }
                    }
                    ExerciseChartType.VOLUME -> {
                        progressionData.mapIndexed { index, data ->
                            Entry(index.toFloat(), data.volume)
                        }.filter { it.y > 0f }
                    }
                    ExerciseChartType.REPS -> {
                        progressionData.mapIndexed { index, data ->
                            Entry(index.toFloat(), data.reps.toFloat())
                        }
                    }
                }

                if (entries.isNotEmpty()) {
                    val label = when (chartType) {
                        ExerciseChartType.WEIGHT -> if (exercise.usesWeight) "Ciężar" else "Powtórzenia"
                        ExerciseChartType.VOLUME -> "Objętość"
                        ExerciseChartType.REPS -> "Powtórzenia"
                    }
                    val unit = when (chartType) {
                        ExerciseChartType.WEIGHT -> if (exercise.usesWeight) "kg" else "reps"
                        ExerciseChartType.VOLUME -> "kg"
                        ExerciseChartType.REPS -> "reps"
                    }

                    LineChartComposable(
                        entries = entries,
                        label = label,
                        unit = unit,
                        dates = progressionData.map { it.date }
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Brak danych dla wybranego typu wykresu",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseProgressStatistics(
    exercise: Exercise,
    viewModel: ChartsViewModel
) {
    var progressionData by remember { mutableStateOf<List<ExerciseProgressionData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(exercise.id) {
        isLoading = true
        progressionData = viewModel.getExerciseProgression(exercise.id)
        isLoading = false
    }

    if (!isLoading && progressionData.isNotEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Statystyki",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (exercise.usesWeight) {
                    val weights = progressionData.mapNotNull { it.weight }.filter { it > 0f }
                    val volumes = progressionData.map { it.volume }.filter { it > 0f }
                    
                    if (weights.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatisticItem(
                                label = "Maks. ciężar",
                                value = "${weights.maxOrNull()} kg"
                            )
                            StatisticItem(
                                label = "Śr. ciężar",
                                value = "${String.format("%.1f", weights.average())} kg"
                            )
                            StatisticItem(
                                label = "Maks. objętość",
                                value = "${volumes.maxOrNull()?.toInt()} kg"
                            )
                        }
                        
                        if (weights.size > 1) {
                            Spacer(modifier = Modifier.height(12.dp))
                            val weightChange = weights.last() - weights.first()
                            val changeText = if (weightChange > 0) "+${weightChange}" else weightChange.toString()
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                StatisticItem(
                                    label = "Postęp ciężaru",
                                    value = "$changeText kg",
                                    isChange = true,
                                    isPositive = weightChange > 0
                                )
                            }
                        }
                    }
                } else {
                    val reps = progressionData.map { it.reps }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatisticItem(
                            label = "Maks. powtórzenia",
                            value = "${reps.maxOrNull()} reps"
                        )
                        StatisticItem(
                            label = "Śr. powtórzenia",
                            value = "${String.format("%.1f", reps.average())} reps"
                        )
                        StatisticItem(
                            label = "Łącznie serii",
                            value = "${progressionData.size}"
                        )
                    }
                }
            }
        }
    }
}

enum class ExerciseChartType {
    WEIGHT, VOLUME, REPS
}