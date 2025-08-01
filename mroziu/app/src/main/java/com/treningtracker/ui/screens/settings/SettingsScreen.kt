package com.treningtracker.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val preferences by viewModel.userPreferencesFlow.collectAsStateWithLifecycle()
    val clipboardManager = LocalClipboardManager.current
    var showBackupDialog by remember { mutableStateOf(false) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.message, uiState.error) {
        if (uiState.message != null || uiState.error != null) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ustawienia") },
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
                    text = "Ustawienia aplikacji",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            // Appearance Section
            item {
                SettingsSection(title = "Wygląd") {
                    SettingsSwitch(
                        title = "Tryb ciemny",
                        description = "Używaj ciemnego motywu",
                        checked = preferences.darkMode,
                        onCheckedChange = viewModel::updateDarkMode,
                        icon = Icons.Default.DarkMode
                    )
                    
                    SettingsSwitch(
                        title = "Dynamiczne kolory",
                        description = "Używaj kolorów systemowych (Android 12+)",
                        checked = preferences.dynamicColor,
                        onCheckedChange = viewModel::updateDynamicColor,
                        icon = Icons.Default.Palette
                    )
                }
            }

            // Backup Section
            item {
                SettingsSection(title = "Kopia zapasowa") {
                    SettingsButton(
                        title = "Utwórz kopię zapasową",
                        description = "Zapisz wszystkie dane do pliku",
                        icon = Icons.Default.Backup,
                        onClick = { showBackupDialog = true },
                        isLoading = uiState.isCreatingBackup
                    )
                    
                    SettingsButton(
                        title = "Przywróć z kopii",
                        description = "Wczytaj dane z pliku kopii zapasowej",
                        icon = Icons.Default.Restore,
                        onClick = { showRestoreDialog = true },
                        isLoading = uiState.isRestoring
                    )
                    
                    SettingsSwitch(
                        title = "Automatyczna kopia zapasowa",
                        description = "Twórz kopie zapasowe automatycznie",
                        checked = preferences.autoBackup,
                        onCheckedChange = viewModel::updateAutoBackup,
                        icon = Icons.Default.CloudSync
                    )
                    
                    if (preferences.lastBackup > 0) {
                        val lastBackupDate = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                            .format(Date(preferences.lastBackup))
                        SettingsInfo(
                            title = "Ostatnia kopia zapasowa",
                            description = lastBackupDate,
                            icon = Icons.Default.Schedule
                        )
                    }
                }
            }

            // Data Export/Import Section
            item {
                SettingsSection(title = "Import/Export danych") {
                    SettingsButton(
                        title = "Eksportuj dane",
                        description = "Wyeksportuj dane do formatu JSON",
                        icon = Icons.Default.FileDownload,
                        onClick = { showExportDialog = true },
                        isLoading = uiState.isExporting
                    )
                }
            }

            // Notifications Section
            item {
                SettingsSection(title = "Powiadomienia") {
                    SettingsSwitch(
                        title = "Powiadomienia",
                        description = "Włącz powiadomienia aplikacji",
                        checked = preferences.notificationsEnabled,
                        onCheckedChange = viewModel::updateNotificationsEnabled,
                        icon = Icons.Default.Notifications
                    )
                    
                    SettingsSwitch(
                        title = "Przypomnienia o treningach",
                        description = "Przypominaj o planowanych treningach",
                        checked = preferences.workoutReminders,
                        onCheckedChange = viewModel::updateWorkoutReminders,
                        icon = Icons.Default.FitnessCenter,
                        enabled = preferences.notificationsEnabled
                    )
                    
                    SettingsSwitch(
                        title = "Przypomnienia o pomiarach",
                        description = "Przypominaj o pomiarach ciała",
                        checked = preferences.measurementReminders,
                        onCheckedChange = viewModel::updateMeasurementReminders,
                        icon = Icons.Default.MonitorWeight,
                        enabled = preferences.notificationsEnabled
                    )
                }
            }

            // App Info Section
            item {
                SettingsSection(title = "Informacje o aplikacji") {
                    SettingsInfo(
                        title = "Wersja aplikacji",
                        description = "1.0.0",
                        icon = Icons.Default.Info
                    )
                    
                    SettingsInfo(
                        title = "Autor",
                        description = "Trening Tracker Team",
                        icon = Icons.Default.Person
                    )
                }
            }

            // Messages
            uiState.message?.let { message ->
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text(
                            text = message,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            uiState.error?.let { error ->
                item {
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
        }
    }

    // Backup Dialog
    if (showBackupDialog) {
        AlertDialog(
            onDismissRequest = { showBackupDialog = false },
            title = { Text("Utwórz kopię zapasową") },
            text = { 
                Text("Czy chcesz utworzyć kopię zapasową wszystkich danych? Plik zostanie zapisany w pamięci urządzenia.") 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.createBackup()
                        showBackupDialog = false
                    }
                ) {
                    Text("Utwórz")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBackupDialog = false }) {
                    Text("Anuluj")
                }
            }
        )
    }

    // Restore Dialog
    if (showRestoreDialog) {
        RestoreDialog(
            onDismiss = { showRestoreDialog = false },
            onRestoreFromFile = { file ->
                viewModel.restoreFromFile(file)
                showRestoreDialog = false
            },
            backupFiles = viewModel.getBackupFiles()
        )
    }

    // Export Dialog
    if (showExportDialog) {
        ExportDialog(
            onDismiss = { showExportDialog = false },
            onExport = {
                viewModel.exportData()
                showExportDialog = false
            }
        )
    }

    // Show exported data
    uiState.exportedData?.let { data ->
        LaunchedEffect(data) {
            clipboardManager.setText(AnnotatedString(data))
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            content()
        }
    }
}

@Composable
fun SettingsSwitch(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}

@Composable
fun SettingsButton(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    isLoading: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
        
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
        } else {
            IconButton(onClick = onClick) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "Wykonaj",
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
fun SettingsInfo(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun RestoreDialog(
    onDismiss: () -> Unit,
    onRestoreFromFile: (File) -> Unit,
    backupFiles: List<File>
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Przywróć z kopii zapasowej") },
        text = {
            if (backupFiles.isEmpty()) {
                Text("Brak dostępnych plików kopii zapasowej.")
            } else {
                Column {
                    Text("Wybierz plik kopii zapasowej:")
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 200.dp)
                    ) {
                        items(backupFiles) { file ->
                            TextButton(
                                onClick = { onRestoreFromFile(file) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = file.name,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Zamknij")
            }
        }
    )
}

@Composable
fun ExportDialog(
    onDismiss: () -> Unit,
    onExport: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Eksportuj dane") },
        text = { 
            Text("Dane zostaną wyeksportowane do formatu JSON i skopiowane do schowka. Możesz je następnie zapisać w wybranym miejscu.") 
        },
        confirmButton = {
            TextButton(onClick = onExport) {
                Text("Eksportuj")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj")
            }
        }
    )
}