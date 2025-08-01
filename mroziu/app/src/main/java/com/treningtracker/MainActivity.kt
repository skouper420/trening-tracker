package com.treningtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.treningtracker.data.preferences.UserPreferences
import com.treningtracker.ui.navigation.WorkoutNavigation
import com.treningtracker.ui.theme.TreningTrackerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var userPreferences: UserPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val preferences by userPreferences.userPreferencesFlow.collectAsState(
                initial = com.treningtracker.data.preferences.UserPreferencesData()
            )
            
            TreningTrackerTheme(
                darkTheme = preferences.darkMode,
                dynamicColor = preferences.dynamicColor
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    WorkoutNavigation(navController = navController)
                }
            }
        }
    }
}