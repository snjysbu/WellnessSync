package com.snjy.wellnesssync.presentation.screens.workout.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.snjy.wellnesssync.domain.model.DifficultyLevel
import com.snjy.wellnesssync.domain.model.Workout
import com.snjy.wellnesssync.domain.model.WorkoutCategory
import com.snjy.wellnesssync.presentation.components.WellnessSyncTextField
import com.snjy.wellnesssync.presentation.components.WorkoutCard
import com.snjy.wellnesssync.presentation.theme.LightPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutListScreen(
    onNavigateBack: () -> Unit,
    onWorkoutClick: (String) -> Unit,
    viewModel: WorkoutListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Workouts", color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LightPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search field
            WellnessSyncTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.searchWorkouts(it)
                },
                placeholder = "Search workouts...",
                leadingIcon = Icons.Default.Search,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Categories filter
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(WorkoutCategory.values()) { category ->
                    AssistChip(
                        onClick = { viewModel.filterByCategory(category) },
                        label = { Text(category.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (state.selectedCategory == category)
                                LightPrimary else MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            // Difficulty filter
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
            ) {
                items(DifficultyLevel.values()) { level ->
                    AssistChip(
                        onClick = { viewModel.filterByDifficulty(level) },
                        label = { Text(level.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (state.selectedDifficulty == level)
                                LightPrimary else MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = LightPrimary)
                } else if (state.filteredWorkouts.isEmpty()) {
                    EmptyWorkoutsView()
                } else {
                    WorkoutsList(
                        workouts = state.filteredWorkouts,
                        onWorkoutClick = onWorkoutClick
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkoutsList(
    workouts: List<Workout>,
    onWorkoutClick: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp)
    ) {
        items(workouts) { workout ->
            WorkoutCard(
                name = workout.name,
                category = workout.category,
                difficultyLevel = workout.difficultyLevel,
                durationMinutes = workout.durationMinutes,
                thumbnailUrl = workout.thumbnailUrl,
                onClick = { onWorkoutClick(workout.id) },
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun EmptyWorkoutsView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No Workouts Found",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Try adjusting your search or filters to find workouts.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}