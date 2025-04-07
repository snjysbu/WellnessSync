package com.snjy.wellnesssync.presentation.screens.workout.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snjy.wellnesssync.domain.model.DifficultyLevel
import com.snjy.wellnesssync.domain.model.Workout
import com.snjy.wellnesssync.domain.model.WorkoutCategory
import com.snjy.wellnesssync.domain.usecase.workout.GetWorkoutsUseCase
import com.snjy.wellnesssync.domain.usecase.workout.SearchWorkoutsUseCase
import com.snjy.wellnesssync.utils.SupabaseDebug
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutListViewModel @Inject constructor(
    private val getWorkoutsUseCase: GetWorkoutsUseCase,
    private val searchWorkoutsUseCase: SearchWorkoutsUseCase
) : ViewModel() {

    private val TAG = "WorkoutListViewModel"
    private val _state = MutableStateFlow(WorkoutListState())
    val state: StateFlow<WorkoutListState> = _state.asStateFlow()

    init {
        Log.d(TAG, "Initializing WorkoutListViewModel")

        // Add this debug call
        viewModelScope.launch {
            SupabaseDebug.testWorkoutsConnection()
        }

        loadAllWorkouts()
    }

    private fun loadAllWorkouts() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }

        Log.d(TAG, "Loading workouts...")

        getWorkoutsUseCase()
            .onEach { workouts ->
                Log.d(TAG, "Received ${workouts.size} workouts")
                _state.update {
                    it.copy(
                        isLoading = false,
                        allWorkouts = workouts,
                        filteredWorkouts = applyFilters(workouts, it.searchQuery, it.selectedCategory, it.selectedDifficulty)
                    )
                }
            }
            .catch { error ->
                Log.e(TAG, "Error loading workouts: ${error.message}", error)
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load workouts: ${error.message}"
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun searchWorkouts(query: String) {
        _state.update { it.copy(searchQuery = query) }

        if (query.length < 2) {
            _state.update {
                it.copy(
                    filteredWorkouts = applyFilters(it.allWorkouts, "", it.selectedCategory, it.selectedDifficulty)
                )
            }
            return
        }

        searchWorkoutsUseCase(query)
            .onEach { searchResults ->
                Log.d(TAG, "Search returned ${searchResults.size} results")
                _state.update {
                    it.copy(
                        filteredWorkouts = applyFilters(searchResults, it.searchQuery, it.selectedCategory, it.selectedDifficulty)
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun filterByCategory(category: WorkoutCategory) {
        val currentCategory = _state.value.selectedCategory

        // Toggle category selection
        val newCategory = if (currentCategory == category) null else category

        _state.update {
            it.copy(
                selectedCategory = newCategory,
                filteredWorkouts = applyFilters(it.allWorkouts, it.searchQuery, newCategory, it.selectedDifficulty)
            )
        }
    }

    fun filterByDifficulty(difficulty: DifficultyLevel) {
        val currentDifficulty = _state.value.selectedDifficulty

        // Toggle difficulty selection
        val newDifficulty = if (currentDifficulty == difficulty) null else difficulty

        _state.update {
            it.copy(
                selectedDifficulty = newDifficulty,
                filteredWorkouts = applyFilters(it.allWorkouts, it.searchQuery, it.selectedCategory, newDifficulty)
            )
        }
    }

    private fun applyFilters(
        workouts: List<Workout>,
        searchQuery: String,
        category: WorkoutCategory?,
        difficulty: DifficultyLevel?
    ): List<Workout> {
        var filtered = workouts

        if (searchQuery.isNotBlank()) {
            filtered = filtered.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.description.contains(searchQuery, ignoreCase = true)
            }
        }

        if (category != null) {
            filtered = filtered.filter { it.category == category }
        }

        if (difficulty != null) {
            filtered = filtered.filter { it.difficultyLevel == difficulty }
        }

        return filtered
    }
}

data class WorkoutListState(
    val isLoading: Boolean = false,
    val allWorkouts: List<Workout> = emptyList(),
    val filteredWorkouts: List<Workout> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: WorkoutCategory? = null,
    val selectedDifficulty: DifficultyLevel? = null,
    val errorMessage: String? = null
)