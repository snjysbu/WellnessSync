package com.snjy.wellnesssync.domain.usecase.workout

import com.snjy.wellnesssync.domain.model.Workout
import com.snjy.wellnesssync.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

class SearchWorkoutsUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository
) {
    operator fun invoke(query: String): Flow<List<Workout>> {
        if (query.length < 2) {
            return emptyFlow()
        }

        return workoutRepository.searchWorkouts(query)
    }
}