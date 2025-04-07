package com.snjy.wellnesssync.presentation.screens.add_workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snjy.wellnesssync.domain.model.ActivityType
import com.snjy.wellnesssync.domain.usecase.activity.TrackActivityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddWorkoutViewModel @Inject constructor(
    private val trackActivityUseCase: TrackActivityUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AddWorkoutState())
    val state: StateFlow<AddWorkoutState> = _state.asStateFlow()

    // Hardcoded user ID for now, in a real app this would come from the user repository
    private val userId = "current_user"

    fun trackActivity(
        type: ActivityType,
        durationMinutes: Int,
        caloriesBurned: Int,
        notes: String? = null
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            trackActivityUseCase(
                userId = userId,
                type = type,
                durationMinutes = durationMinutes,
                dateTime = Date(), // Current time
                caloriesBurned = caloriesBurned,
                notes = notes
            ).fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            workoutAdded = true
                        )
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Failed to save activity"
                        )
                    }
                }
            )
        }
    }
}

data class AddWorkoutState(
    val isLoading: Boolean = false,
    val workoutAdded: Boolean = false,
    val errorMessage: String? = null
)