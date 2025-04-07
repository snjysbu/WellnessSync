package com.snjy.wellnesssync.data.remote.datasource

import com.snjy.wellnesssync.data.remote.api.SupabaseService
import com.snjy.wellnesssync.data.remote.dto.ActivityDto
import javax.inject.Inject

class ActivityRemoteDataSource @Inject constructor(
    private val supabaseService: SupabaseService
) {
    suspend fun getUserActivities(token: String, userId: String): Result<List<ActivityDto>> {
        return try {
            val response = supabaseService.getUserActivities("Bearer $token", userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch activities: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createActivity(token: String, activityDto: ActivityDto): Result<ActivityDto> {
        return try {
            val response = supabaseService.createActivity("Bearer $token", activityDto)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to create activity: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteActivity(token: String, activityId: String): Result<Unit> {
        return try {
            val response = supabaseService.deleteActivity("Bearer $token", activityId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete activity: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}