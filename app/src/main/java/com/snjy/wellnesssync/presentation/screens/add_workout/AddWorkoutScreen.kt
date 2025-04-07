package com.snjy.wellnesssync.presentation.screens.add_workout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.snjy.wellnesssync.domain.model.ActivityType
import com.snjy.wellnesssync.presentation.components.WellnessSyncButton
import com.snjy.wellnesssync.presentation.components.WellnessSyncTextField
import com.snjy.wellnesssync.presentation.theme.LightPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWorkoutScreen(
    onNavigateBack: () -> Unit,
    onWorkoutAdded: () -> Unit,
    viewModel: AddWorkoutViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedActivityType by remember { mutableStateOf(ActivityType.WORKOUT) }
    var durationMinutes by remember { mutableFloatStateOf(30f) }
    var caloriesBurned by remember { mutableFloatStateOf(200f) }
    var notes by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    // Show snackbar when error occurs
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    // Navigate back when workout added successfully
    LaunchedEffect(state.workoutAdded) {
        if (state.workoutAdded) {
            onWorkoutAdded()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Activity") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LightPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Date and time section
                Text(
                    text = "Date and Time",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Show current date and time (not editable for this simple implementation)
                val dateTimeFormat = SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a", Locale.getDefault())
                val currentDateTime = dateTimeFormat.format(Date())

                Text(
                    text = currentDateTime,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Activity type selection
                Text(
                    text = "Activity Type",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = selectedActivityType.name.lowercase().replaceFirstChar { it.uppercase() },
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.exposedDropdownSize()
                    ) {
                        ActivityType.values().forEach { activityType ->
                            DropdownMenuItem(
                                text = {
                                    Text(activityType.name.lowercase().replaceFirstChar { it.uppercase() })
                                },
                                onClick = {
                                    selectedActivityType = activityType
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Duration selection
                Text(
                    text = "Duration (minutes)",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Slider(
                        value = durationMinutes,
                        onValueChange = { durationMinutes = it },
                        valueRange = 5f..120f,
                        steps = 23,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = durationMinutes.toInt().toString(),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Calories burned
                Text(
                    text = "Calories Burned",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Slider(
                        value = caloriesBurned,
                        onValueChange = { caloriesBurned = it },
                        valueRange = 50f..1000f,
                        steps = 19,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = caloriesBurned.toInt().toString(),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Notes
                Text(
                    text = "Notes (Optional)",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                WellnessSyncTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = "Add any details about this activity...",
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Save button
                WellnessSyncButton(
                    text = "Save Activity",
                    onClick = {
                        viewModel.trackActivity(
                            type = selectedActivityType,
                            durationMinutes = durationMinutes.toInt(),
                            caloriesBurned = caloriesBurned.toInt(),
                            notes = notes.ifBlank { null }
                        )
                    },
                    isLoading = state.isLoading
                )
            }
        }
    }
}