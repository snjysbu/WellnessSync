package com.snjy.wellnesssync.data.repository

import android.util.Log
import com.snjy.wellnesssync.data.local.dao.WorkoutDao
import com.snjy.wellnesssync.data.local.entity.toEntity
import com.snjy.wellnesssync.data.local.entity.toDomainModel
import com.snjy.wellnesssync.data.remote.datasource.WorkoutRemoteDataSource
import com.snjy.wellnesssync.data.remote.dto.toDomainModel
import com.snjy.wellnesssync.di.NetworkModule
import com.snjy.wellnesssync.domain.model.DifficultyLevel
import com.snjy.wellnesssync.domain.model.Workout
import com.snjy.wellnesssync.domain.model.WorkoutCategory
import com.snjy.wellnesssync.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WorkoutRepositoryImpl @Inject constructor(
    private val workoutDao: WorkoutDao,
    private val workoutRemoteDataSource: WorkoutRemoteDataSource
) : WorkoutRepository {

    private val TAG = "WorkoutRepositoryImpl"

    override fun getAllWorkouts(): Flow<List<Workout>> {
        return flow {
            // Force refresh workouts when getAllWorkouts is called
            refreshWorkouts()

            // Emit workouts from local database
            emitAll(workoutDao.getAllWorkouts().map { entities ->
                Log.d(TAG, "getAllWorkouts local DB returning ${entities.size} workouts")
                entities.map { it.toDomainModel() }
            })
        }
    }

    override fun getWorkoutsByCategory(category: WorkoutCategory): Flow<List<Workout>> {
        return workoutDao.getWorkoutsByCategory(category.name).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getWorkoutsByDifficulty(difficultyLevel: DifficultyLevel): Flow<List<Workout>> {
        return workoutDao.getWorkoutsByDifficultyLevel(difficultyLevel.name).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getWorkoutById(workoutId: String): Result<Workout> {
        // First try to get from local DB
        val localWorkout = workoutDao.getWorkoutById(workoutId)
        if (localWorkout != null) {
            return Result.success(localWorkout.toDomainModel())
        }

        // Using the Supabase anon key directly for auth
        val token = "Bearer ${NetworkModule.SUPABASE_ANON_KEY}"

        // Fall back to remote
        return workoutRemoteDataSource.getWorkoutById(token, workoutId).map { workoutDto ->
            val workout = workoutDto.toDomainModel()
            workoutDao.insertWorkout(workout.toEntity())
            workout
        }
    }

    override fun searchWorkouts(query: String): Flow<List<Workout>> {
        return workoutDao.searchWorkouts(query).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun refreshWorkouts(): Result<Unit> {
        Log.d(TAG, "refreshWorkouts called")

        // Using the Supabase anon key directly for auth
        val token = "Bearer ${NetworkModule.SUPABASE_ANON_KEY}"

        return try {
            workoutRemoteDataSource.getAllWorkouts(token).fold(
                onSuccess = { workouts ->
                    Log.d(TAG, "Successfully fetched ${workouts.size} workouts from remote")
                    // Populate local database with fetched workouts
                    val workoutEntities = workouts.map { it.toDomainModel().toEntity() }

                    // Clear existing workouts and insert new ones
                    workoutDao.deleteAllWorkouts()
                    workoutDao.insertAllWorkouts(workoutEntities)

                    Log.d(TAG, "Successfully inserted ${workoutEntities.size} workouts into local DB")
                    Result.success(Unit)
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to fetch workouts: ${error.message}")
                    Result.failure(error)
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Exception during workout refresh: ${e.message}")
            Result.failure(e)
        }
    }
}