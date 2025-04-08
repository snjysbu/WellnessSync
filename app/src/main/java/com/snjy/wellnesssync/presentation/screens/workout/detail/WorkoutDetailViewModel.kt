package com.snjy.wellnesssync.presentation.screens.workout.detail

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snjy.wellnesssync.data.remote.api.VideoService
import com.snjy.wellnesssync.domain.model.Workout
import com.snjy.wellnesssync.domain.usecase.workout.GetWorkoutsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutDetailViewModel @Inject constructor(
    private val getWorkoutsUseCase: GetWorkoutsUseCase,
    private val videoService: VideoService,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(WorkoutDetailState())
    val state: StateFlow<WorkoutDetailState> = _state.asStateFlow()

    fun loadWorkout(workoutId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            getWorkoutsUseCase.byId(workoutId).fold(
                onSuccess = { workout ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            workout = workout
                        )
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Failed to load workout"
                        )
                    }
                }
            )
        }
    }

    /**
     * Opens the workout video in the YouTube app or browser
     */
    fun openWorkoutVideo(videoUrl: String) {
        try {
            val videoId = videoService.extractVideoId(videoUrl)
            if (videoId != null) {
                videoService.openYouTubeVideo(context, videoId)
            } else {
                // Handle invalid URL gracefully
                Log.e("WorkoutDetailViewModel", "Invalid YouTube URL: $videoUrl")
                // Maybe show a toast to the user
            }
        } catch (e: Exception) {
            Log.e("WorkoutDetailViewModel", "Error opening video", e)
            // Handle exception gracefully
        }
    }
}

data class WorkoutDetailState(
    val isLoading: Boolean = false,
    val workout: Workout? = null,
    val errorMessage: String? = null
)