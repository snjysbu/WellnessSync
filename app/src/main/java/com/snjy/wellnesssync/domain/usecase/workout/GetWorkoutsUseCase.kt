package com.snjy.wellnesssync.domain.usecase.workout

import com.snjy.wellnesssync.domain.model.DifficultyLevel
import com.snjy.wellnesssync.domain.model.Workout
import com.snjy.wellnesssync.domain.model.WorkoutCategory
import com.snjy.wellnesssync.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWorkoutsUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository
) {
    operator fun invoke(): Flow<List<Workout>> {
        return workoutRepository.getAllWorkouts()
    }

    fun byCategory(category: WorkoutCategory): Flow<List<Workout>> {
        return workoutRepository.getWorkoutsByCategory(category)
    }

    fun byDifficulty(difficultyLevel: DifficultyLevel): Flow<List<Workout>> {
        return workoutRepository.getWorkoutsByDifficulty(difficultyLevel)
    }

    suspend fun byId(workoutId: String): Result<Workout> {
        return workoutRepository.getWorkoutById(workoutId)
    }

    suspend fun refresh(): Result<Unit> {
        return workoutRepository.refreshWorkouts()
    }
}