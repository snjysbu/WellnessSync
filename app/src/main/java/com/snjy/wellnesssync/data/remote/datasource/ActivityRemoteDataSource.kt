package com.snjy.wellnesssync.data.remote.datasource

import android.util.Log
import com.snjy.wellnesssync.data.remote.api.SupabaseService
import com.snjy.wellnesssync.data.remote.dto.ActivityDto
import javax.inject.Inject

class ActivityRemoteDataSource @Inject constructor(
    private val supabaseService: SupabaseService
) {
    private val tag = "ActivityRemoteDataSource"

    suspend fun getUserActivities(token: String, userId: String): Result<List<ActivityDto>> {
        return try {
            Log.d(tag, "Fetching activities for user: $userId")
            val response = supabaseService.getUserActivities("Bearer $token", userId)

            if (response.isSuccessful && response.body() != null) {
                Log.d(tag, "Successfully fetched activities")
                Result.success(response.body()!!)
            } else {
                val errorMsg = "Failed to fetch activities: ${response.message()}"
                Log.e(tag, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception while fetching activities", e)
            Result.failure(e)
        }
    }

    suspend fun createActivity(token: String, activityDto: ActivityDto): Result<ActivityDto> {
        return try {
            Log.d(tag, "Creating activity for user: ${activityDto.userId}")
            // Fixed the method call by adding the missing 'prefer' parameter
            val response = supabaseService.createActivity(
                token = "Bearer $token",
                prefer = "return=representation",
                activity = activityDto
            )

            if (response.isSuccessful && response.body() != null) {
                Log.d(tag, "Successfully created activity")
                Result.success(response.body()!!)
            } else {
                val errorMsg = "Failed to create activity: ${response.message()}"
                Log.e(tag, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception while creating activity", e)
            Result.failure(e)
        }
    }

    suspend fun deleteActivity(token: String, activityId: String): Result<Unit> {
        return try {
            Log.d(tag, "Deleting activity: $activityId")
            val response = supabaseService.deleteActivity("Bearer $token", activityId)

            if (response.isSuccessful) {
                Log.d(tag, "Successfully deleted activity")
                Result.success(Unit)
            } else {
                val errorMsg = "Failed to delete activity: ${response.message()}"
                Log.e(tag, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception while deleting activity", e)
            Result.failure(e)
        }
    }
}