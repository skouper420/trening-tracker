package com.treningtracker.ui.screens.measurements

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.treningtracker.data.model.BodyMeasurement
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyMeasurementsScreen(
    navController: NavController,
    viewModel: BodyMeasurementsViewModel = hiltViewModel()
) {
    val measurements by viewModel.measurements.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Show snackbar for messages
    LaunchedEffect(uiState.message, uiState.error) {
        if (uiState.message != null || uiState.error != null) {
            // Auto-clear message after showing
            kotlinx.coroutines.delay(3000)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pomiary ciała") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wstecz")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("measurement_charts") }) {
                        Icon(Icons.Default.ShowChart, contentDescription = "Wykresy")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_body_measurement") }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Dodaj pomiar")
            }
        }
    ) { paddingValues ->
        if (measurements.isEmpty()) {
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
                        Icons.Default.StraightenOutlined,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Brak pomiarów",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "Dodaj pierwszy pomiar, aby śledzić postępy",
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(measurements) { measurement ->
                    MeasurementCard(
                        measurement = measurement,
                        onEdit = { navController.navigate("edit_body_measurement/${measurement.id}") },
                        onDelete = { viewModel.deleteMeasurement(measurement) }
                    )
                }
            }
        }
    }
}

@Composable
fun MeasurementCard(
    measurement: BodyMeasurement,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

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
                    text = dateFormat.format(Date(measurement.date)),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edytuj")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Usuń")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Display measurements in a grid
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                measurement.weight?.let {
                    MeasurementRow("Waga", "${it} kg")
                }
                measurement.waist?.let {
                    MeasurementRow("Talia", "${it} cm")
                }
                measurement.chest?.let {
                    MeasurementRow("Klatka", "${it} cm")
                }
                measurement.biceps?.let {
                    MeasurementRow("Biceps", "${it} cm")
                }
                measurement.forearm?.let {
                    MeasurementRow("Przedramię", "${it} cm")
                }
                measurement.thigh?.let {
                    MeasurementRow("Udo", "${it} cm")
                }
                measurement.calf?.let {
                    MeasurementRow("Łydka", "${it} cm")
                }
            }

            if (measurement.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Notatki: ${measurement.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Usuń pomiar") },
            text = { Text("Czy na pewno chcesz usunąć ten pomiar?") },
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
fun MeasurementRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBodyMeasurementScreen(
    navController: NavController,
    viewModel: AddBodyMeasurementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dodaj pomiary") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wstecz")
                    }
                }
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
                    text = "Wprowadź pomiary ciała",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = uiState.weight,
                    onValueChange = viewModel::updateWeight,
                    label = { Text("Waga (kg)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = uiState.waist,
                    onValueChange = viewModel::updateWaist,
                    label = { Text("Talia (cm)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = uiState.chest,
                    onValueChange = viewModel::updateChest,
                    label = { Text("Klatka piersiowa (cm)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = uiState.biceps,
                    onValueChange = viewModel::updateBiceps,
                    label = { Text("Biceps (cm)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = uiState.forearm,
                    onValueChange = viewModel::updateForearm,
                    label = { Text("Przedramię (cm)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = uiState.thigh,
                    onValueChange = viewModel::updateThigh,
                    label = { Text("Udo (cm)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = uiState.calf,
                    onValueChange = viewModel::updateCalf,
                    label = { Text("Łydka (cm)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = uiState.notes,
                    onValueChange = viewModel::updateNotes,
                    label = { Text("Notatki (opcjonalne)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }

            item {
                uiState.error?.let { error ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = error,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        viewModel.saveMeasurement {
                            navController.navigateUp()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Zapisz pomiary")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBodyMeasurementScreen(
    navController: NavController, 
    measurementId: Long,
    viewModel: EditBodyMeasurementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(measurementId) {
        viewModel.loadMeasurement(measurementId)
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
                title = { Text("Edytuj pomiary") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wstecz")
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
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Edytuj pomiary ciała",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                item {
                    OutlinedTextField(
                        value = uiState.weight,
                        onValueChange = viewModel::updateWeight,
                        label = { Text("Waga (kg)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = uiState.waist,
                        onValueChange = viewModel::updateWaist,
                        label = { Text("Talia (cm)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = uiState.chest,
                        onValueChange = viewModel::updateChest,
                        label = { Text("Klatka piersiowa (cm)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = uiState.biceps,
                        onValueChange = viewModel::updateBiceps,
                        label = { Text("Biceps (cm)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = uiState.forearm,
                        onValueChange = viewModel::updateForearm,
                        label = { Text("Przedramię (cm)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = uiState.thigh,
                        onValueChange = viewModel::updateThigh,
                        label = { Text("Udo (cm)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = uiState.calf,
                        onValueChange = viewModel::updateCalf,
                        label = { Text("Łydka (cm)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = uiState.notes,
                        onValueChange = viewModel::updateNotes,
                        label = { Text("Notatki (opcjonalne)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }

                item {
                    uiState.error?.let { error ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = error,
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                item {
                    Button(
                        onClick = {
                            viewModel.updateMeasurement {
                                navController.navigateUp()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isSaving
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Zapisz zmiany")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeasurementChartsScreen(
    navController: NavController,
    viewModel: com.treningtracker.ui.screens.charts.ChartsViewModel = hiltViewModel()
) {
    val bodyMeasurements by viewModel.bodyMeasurements.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedMeasurementType by remember { mutableStateOf(com.treningtracker.ui.screens.charts.MeasurementType.WEIGHT) }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wykresy postępów") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wstecz")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (bodyMeasurements.isEmpty()) {
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
                        Icons.Default.ShowChart,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Brak danych do wykresu",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "Dodaj pomiary ciała, aby zobaczyć wykresy postępów",
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
                        text = "Postępy pomiarów ciała",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    MeasurementTypeSelector(
                        selectedType = selectedMeasurementType,
                        onTypeSelected = { selectedMeasurementType = it }
                    )
                }

                item {
                    MeasurementChart(
                        measurementType = selectedMeasurementType,
                        viewModel = viewModel
                    )
                }

                item {
                    MeasurementStatistics(
                        measurements = bodyMeasurements,
                        measurementType = selectedMeasurementType
                    )
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
fun MeasurementTypeSelector(
    selectedType: com.treningtracker.ui.screens.charts.MeasurementType,
    onTypeSelected: (com.treningtracker.ui.screens.charts.MeasurementType) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Wybierz typ pomiaru",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(com.treningtracker.ui.screens.charts.MeasurementType.values()) { type ->
                    FilterChip(
                        onClick = { onTypeSelected(type) },
                        label = { Text(type.displayName) },
                        selected = selectedType == type
                    )
                }
            }
        }
    }
}

@Composable
fun MeasurementChart(
    measurementType: com.treningtracker.ui.screens.charts.MeasurementType,
    viewModel: com.treningtracker.ui.screens.charts.ChartsViewModel
) {
    var measurements by remember { mutableStateOf<List<BodyMeasurement>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(measurementType) {
        isLoading = true
        measurements = viewModel.getMeasurementsByType(measurementType)
        isLoading = false
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Wykres ${measurementType.displayName.lowercase()}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
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
            } else if (measurements.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Brak danych dla ${measurementType.displayName.lowercase()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                val entries = measurements.mapIndexed { index, measurement ->
                    val value = when (measurementType) {
                        com.treningtracker.ui.screens.charts.MeasurementType.WEIGHT -> measurement.weight
                        com.treningtracker.ui.screens.charts.MeasurementType.WAIST -> measurement.waist
                        com.treningtracker.ui.screens.charts.MeasurementType.CHEST -> measurement.chest
                        com.treningtracker.ui.screens.charts.MeasurementType.BICEPS -> measurement.biceps
                        com.treningtracker.ui.screens.charts.MeasurementType.FOREARM -> measurement.forearm
                        com.treningtracker.ui.screens.charts.MeasurementType.THIGH -> measurement.thigh
                        com.treningtracker.ui.screens.charts.MeasurementType.CALF -> measurement.calf
                    }
                    com.github.mikephil.charting.data.Entry(index.toFloat(), value ?: 0f)
                }.filter { it.y > 0f }

                if (entries.isNotEmpty()) {
                    com.treningtracker.ui.screens.charts.LineChartComposable(
                        entries = entries,
                        label = measurementType.displayName,
                        unit = measurementType.unit,
                        dates = measurements.map { it.date }
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Brak danych dla ${measurementType.displayName.lowercase()}",
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
fun MeasurementStatistics(
    measurements: List<BodyMeasurement>,
    measurementType: com.treningtracker.ui.screens.charts.MeasurementType
) {
    val values = measurements.mapNotNull { measurement ->
        when (measurementType) {
            com.treningtracker.ui.screens.charts.MeasurementType.WEIGHT -> measurement.weight
            com.treningtracker.ui.screens.charts.MeasurementType.WAIST -> measurement.waist
            com.treningtracker.ui.screens.charts.MeasurementType.CHEST -> measurement.chest
            com.treningtracker.ui.screens.charts.MeasurementType.BICEPS -> measurement.biceps
            com.treningtracker.ui.screens.charts.MeasurementType.FOREARM -> measurement.forearm
            com.treningtracker.ui.screens.charts.MeasurementType.THIGH -> measurement.thigh
            com.treningtracker.ui.screens.charts.MeasurementType.CALF -> measurement.calf
        }
    }

    if (values.isNotEmpty()) {
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatisticItem(
                        label = "Aktualna",
                        value = "${values.last()} ${measurementType.unit}"
                    )
                    StatisticItem(
                        label = "Najwyższa",
                        value = "${values.maxOrNull()} ${measurementType.unit}"
                    )
                    StatisticItem(
                        label = "Najniższa",
                        value = "${values.minOrNull()} ${measurementType.unit}"
                    )
                }

                if (values.size > 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                    val change = values.last() - values.first()
                    val changeText = if (change > 0) "+${change}" else change.toString()
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        StatisticItem(
                            label = "Zmiana ogółem",
                            value = "$changeText ${measurementType.unit}",
                            isChange = true,
                            isPositive = change > 0
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatisticItem(
    label: String,
    value: String,
    isChange: Boolean = false,
    isPositive: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isChange) {
                if (isPositive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            } else MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}