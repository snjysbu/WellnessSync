package com.snjy.wellnesssync.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.snjy.wellnesssync.presentation.components.WellnessSyncButton
import com.snjy.wellnesssync.presentation.theme.LightPrimary
import com.snjy.wellnesssync.presentation.theme.SecondaryDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToActivities: () -> Unit,
    onNavigateToAddWorkout: () -> Unit,
    onNavigateToWorkouts: () -> Unit,
    onNavigateToDevices: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("WellnessSync", color = Color.Black)
                },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = SecondaryDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LightPrimary
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ) {
                // First two items
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = true,
                    onClick = { /* Already at home */ }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Timeline, contentDescription = "Activities") },
                    label = { Text("Activities") },
                    selected = false,
                    onClick = onNavigateToActivities
                )

                // Empty center item to create space for FAB
                NavigationBarItem(
                    icon = { Box(modifier = Modifier.size(24.dp)) {} },
                    label = { Text("") },
                    selected = false,
                    onClick = { },
                    enabled = false
                )

                // Last two items
                NavigationBarItem(
                    icon = { Icon(Icons.Default.DirectionsRun, contentDescription = "Workouts") },
                    label = { Text("Workouts") },
                    selected = false,
                    onClick = onNavigateToWorkouts
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Devices, contentDescription = "Devices") },
                    label = { Text("Devices") },
                    selected = false,
                    onClick = onNavigateToDevices
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddWorkout,
                containerColor = LightPrimary,
                contentColor = SecondaryDark,
                elevation = FloatingActionButtonDefaults.elevation(8.dp),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Workout",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        floatingActionButtonPosition = androidx.compose.material3.FabPosition.Center
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Welcome message with date
                WelcomeSection(userName = state.userName)

                Spacer(modifier = Modifier.height(24.dp))

                // Activity Stats
                ActivityStatsSection(
                    totalActivities = state.totalActivities,
                    totalWorkoutMinutes = state.totalWorkoutMinutes,
                    totalCaloriesBurned = state.totalCaloriesBurned
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Quick Actions
                QuickActionsSection(
                    onChatWithAiClick = onNavigateToChat,
                    onExploreWorkoutsClick = onNavigateToWorkouts
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Recent Activities
                RecentActivitiesSection(
                    recentActivities = state.recentActivities,
                    onViewAllClick = onNavigateToActivities
                )
            }
        }
    }
}

@Composable
fun WelcomeSection(userName: String) {
    val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
    val date = dateFormat.format(Date())

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Hello, $userName",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = date,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ActivityStatsSection(
    totalActivities: Int,
    totalWorkoutMinutes: Int,
    totalCaloriesBurned: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "This Week's Activity",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stats row
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                StatItem(
                    value = totalActivities.toString(),
                    label = "Activities",
                    modifier = Modifier.weight(1f)
                )

                StatItem(
                    value = totalWorkoutMinutes.toString(),
                    label = "Minutes",
                    modifier = Modifier.weight(1f)
                )

                StatItem(
                    value = totalCaloriesBurned.toString(),
                    label = "Calories",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun StatItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun QuickActionsSection(
    onChatWithAiClick: () -> Unit,
    onExploreWorkoutsClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            ActionCard(
                icon = Icons.Default.SelfImprovement,
                title = "Chat with AI",
                description = "Get wellness tips and advice",
                onClick = onChatWithAiClick,
                modifier = Modifier
                    .weight(1f)
                    .height(150.dp)
                    .padding(end = 8.dp)
            )

            ActionCard(
                icon = Icons.Default.DirectionsRun,
                title = "Explore Workouts",
                description = "Find new exercises",
                onClick = onExploreWorkoutsClick,
                modifier = Modifier
                    .weight(1f)
                    .height(150.dp)
                    .padding(start = 8.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(LightPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SecondaryDark,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun RecentActivitiesSection(
    recentActivities: List<RecentActivityItem>,
    onViewAllClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Activities",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            androidx.compose.material3.TextButton(onClick = onViewAllClick) {
                Text(
                    text = "View All",
                    color = LightPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (recentActivities.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No recent activities. Start tracking your wellness journey!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                recentActivities.forEach { activity ->
                    RecentActivityCard(
                        activity = activity,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RecentActivityCard(
    activity: RecentActivityItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Activity icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(LightPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = activity.icon,
                    contentDescription = null,
                    tint = SecondaryDark,
                    modifier = Modifier.size(24.dp)
                )
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(16.dp))

            // Activity details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = activity.time,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Activity metrics
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = activity.duration,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = activity.calories,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

data class RecentActivityItem(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val time: String,
    val duration: String,
    val calories: String
)