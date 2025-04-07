package com.snjy.wellnesssync.domain.repository

import com.snjy.wellnesssync.domain.model.DifficultyLevel
import com.snjy.wellnesssync.domain.model.Workout
import com.snjy.wellnesssync.domain.model.WorkoutCategory
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    fun getAllWorkouts(): Flow<List<Workout>>

    fun getWorkoutsByCategory(category: WorkoutCategory): Flow<List<Workout>>

    fun getWorkoutsByDifficulty(difficultyLevel: DifficultyLevel): Flow<List<Workout>>

    suspend fun getWorkoutById(workoutId: String): Result<Workout>

    fun searchWorkouts(query: String): Flow<List<Workout>>

    suspend fun refreshWorkouts(): Result<Unit>
}