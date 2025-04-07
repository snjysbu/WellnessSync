package com.snjy.wellnesssync.presentation.screens.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snjy.wellnesssync.domain.model.ActivityType
import com.snjy.wellnesssync.domain.usecase.activity.GetActivitiesUseCase
import com.snjy.wellnesssync.domain.usecase.activity.GetActivityStatsUseCase
import com.snjy.wellnesssync.domain.usecase.user.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getActivitiesUseCase: GetActivitiesUseCase,
    private val getActivityStatsUseCase: GetActivityStatsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    // Hardcoded user ID for now, in a real app this would come from the user repository
    private val userId = "current_user"

    init {
        loadUserProfile()
        loadRecentActivities()
        loadActivityStats()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            getUserProfileUseCase().fold(
                onSuccess = { user ->
                    _state.update {
                        it.copy(
                            userName = user.name
                        )
                    }
                },
                onFailure = {
                    // If we can't load the user profile, use a default name
                    _state.update {
                        it.copy(
                            userName = "Wellness User"
                        )
                    }
                }
            )
        }
    }

    private fun loadRecentActivities() {
        getActivitiesUseCase(userId)
            .onEach { activities ->
                // Only take the 3 most recent activities
                val recentActivities = activities.take(3).map { activity ->
                    val icon = when (activity.type) {
                        ActivityType.WORKOUT -> Icons.Default.DirectionsRun
                        ActivityType.MEDITATION -> Icons.Default.SelfImprovement
                        ActivityType.YOGA -> Icons.Default.SelfImprovement
                        else -> Icons.Default.DirectionsRun
                    }

                    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                    val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())

                    RecentActivityItem(
                        icon = icon,
                        title = activity.type.name.lowercase().replaceFirstChar { it.uppercase() },
                        time = "${dateFormat.format(activity.dateTime)} at ${timeFormat.format(activity.dateTime)}",
                        duration = "${activity.durationMinutes} min",
                        calories = "${activity.caloriesBurned} cal"
                    )
                }

                _state.update {
                    it.copy(
                        recentActivities = recentActivities
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadActivityStats() {
        getActivityStatsUseCase(userId)
            .onEach { activityStats ->
                val totalMinutes = activityStats.values.sum()

                // Get 7 days ago
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, -7)

                // In a real app, we would filter activities by date and calculate calories burned
                // For now, we'll use some placeholder values
                _state.update {
                    it.copy(
                        totalActivities = activityStats.size,
                        totalWorkoutMinutes = totalMinutes,
                        totalCaloriesBurned = totalMinutes * 5 // Rough estimate
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}

data class HomeState(
    val userName: String = "Wellness User",
    val totalActivities: Int = 0,
    val totalWorkoutMinutes: Int = 0,
    val totalCaloriesBurned: Int = 0,
    val recentActivities: List<RecentActivityItem> = emptyList()
)