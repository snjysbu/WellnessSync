package com.snjy.wellnesssync.data.remote.datasource

import android.util.Log
import com.snjy.wellnesssync.data.remote.api.SupabaseService
import com.snjy.wellnesssync.data.remote.api.VideoService
import com.snjy.wellnesssync.data.remote.dto.WorkoutDto
import javax.inject.Inject

class WorkoutRemoteDataSource @Inject constructor(
    private val supabaseService: SupabaseService,
    private val videoService: VideoService
) {
    private val tag = "WorkoutRemoteDataSource"

    suspend fun getAllWorkouts(token: String): Result<List<WorkoutDto>> {
        return try {
            Log.d(tag, "Fetching all workouts with token: $token")
            val response = supabaseService.getAllWorkouts(token)
            Log.d(tag, "Response code: ${response.code()}")

            if (response.isSuccessful && response.body() != null) {
                val workouts = response.body()!!
                Log.d(tag, "Fetched ${workouts.size} workouts successfully")

                // Enhance workouts with proper YouTube thumbnail URLs if needed
                val enhancedWorkouts = workouts.map { workout ->
                    val videoId = videoService.extractVideoId(workout.videoUrl)
                    val thumbnailUrl = videoService.getThumbnailUrl(videoId) ?: ""
                    workout.copy(thumbnailUrl = thumbnailUrl)
                }
                Result.success(enhancedWorkouts)
            } else {
                val errorMsg = "Failed to fetch workouts: ${response.message()} - Code: ${response.code()}"
                Log.e(tag, errorMsg)
                if (response.errorBody() != null) {
                    Log.e(tag, "Error body: ${response.errorBody()?.string()}")
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception fetching workouts", e)
            Result.failure(e)
        }
    }

    suspend fun getWorkoutsByCategory(token: String, category: String): Result<List<WorkoutDto>> {
        return try {
            Log.d(tag, "Fetching workouts by category: $category")
            val response = supabaseService.getWorkoutsByCategory(token, category)

            if (response.isSuccessful && response.body() != null) {
                val workouts = response.body()!!
                // Enhance workouts with proper YouTube thumbnail URLs if needed
                val enhancedWorkouts = workouts.map { workout ->
                    val videoId = videoService.extractVideoId(workout.videoUrl)
                    val thumbnailUrl = videoService.getThumbnailUrl(videoId) ?: ""
                    workout.copy(thumbnailUrl = thumbnailUrl)
                }
                Result.success(enhancedWorkouts)
            } else {
                val errorMsg = "Failed to fetch workouts by category: ${response.message()}"
                Log.e(tag, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception in getWorkoutsByCategory", e)
            Result.failure(e)
        }
    }

    suspend fun getWorkoutById(token: String, workoutId: String): Result<WorkoutDto> {
        return try {
            Log.d(tag, "Fetching workout by ID: $workoutId")
            val response = supabaseService.getWorkoutById(token, workoutId)

            if (response.isSuccessful && response.body() != null) {
                val workout = response.body()!!
                // Enhance workout with proper YouTube thumbnail URL if needed
                val videoId = videoService.extractVideoId(workout.videoUrl)
                val thumbnailUrl = videoService.getThumbnailUrl(videoId) ?: ""
                val enhancedWorkout = workout.copy(thumbnailUrl = thumbnailUrl)
                Result.success(enhancedWorkout)
            } else {
                val errorMsg = "Failed to fetch workout: ${response.message()}"
                Log.e(tag, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception in getWorkoutById", e)
            Result.failure(e)
        }
    }
}