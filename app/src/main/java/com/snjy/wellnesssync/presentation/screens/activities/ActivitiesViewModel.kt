package com.snjy.wellnesssync.presentation.screens.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snjy.wellnesssync.domain.model.Activity
import com.snjy.wellnesssync.domain.usecase.activity.GetActivitiesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ActivitiesViewModel @Inject constructor(
    private val getActivitiesUseCase: GetActivitiesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ActivitiesState())
    val state: StateFlow<ActivitiesState> = _state.asStateFlow()

    // Hardcoded user ID for now, in a real app this would come from the user repository
    private val userId = "current_user"

    init {
        loadActivities()
    }

    private fun loadActivities() {
        _state.update { it.copy(isLoading = true) }

        getActivitiesUseCase(userId)
            .onEach { activities ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        activities = activities.sortedByDescending { activity -> activity.dateTime }
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}

data class ActivitiesState(
    val isLoading: Boolean = false,
    val activities: List<Activity> = emptyList(),
    val errorMessage: String? = null
)