package com.snjy.wellnesssync.data.repository

import android.util.Log
import com.snjy.wellnesssync.data.local.dao.ActivityDao
import com.snjy.wellnesssync.data.local.entity.toEntity
import com.snjy.wellnesssync.data.local.entity.toDomainModel
import com.snjy.wellnesssync.data.preferences.UserPreferences
import com.snjy.wellnesssync.data.remote.datasource.ActivityRemoteDataSource
import com.snjy.wellnesssync.data.remote.dto.ActivityDto
import com.snjy.wellnesssync.data.remote.dto.toDomainModel
import com.snjy.wellnesssync.domain.model.Activity
import com.snjy.wellnesssync.domain.model.ActivityType
import com.snjy.wellnesssync.domain.repository.ActivityRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class ActivityRepositoryImpl @Inject constructor(
    private val activityDao: ActivityDao,
    private val activityRemoteDataSource: ActivityRemoteDataSource,
    private val userPreferences: UserPreferences
) : ActivityRepository {

    private val tag = "ActivityRepository"
    private val scope = CoroutineScope(Dispatchers.IO)

    override suspend fun trackActivity(
        userId: String,
        type: ActivityType,
        durationMinutes: Int,
        dateTime: Date,
        caloriesBurned: Int,
        notes: String?
    ): Result<Activity> {
        val activityId = UUID.randomUUID().toString()
        val activityDto = ActivityDto(
            id = activityId,
            userId = userId,
            type = type.name,
            durationMinutes = durationMinutes,
            dateTime = dateTime.time,
            caloriesBurned = caloriesBurned,
            notes = notes
        )

        return try {
            // Get auth token from preferences
            val token = userPreferences.authToken.first() ?: "dummy_token"

            // Try to save remotely first
            activityRemoteDataSource.createActivity(token, activityDto).fold(
                onSuccess = { remoteActivity ->
                    // Save to local DB on success
                    val activity = remoteActivity.toDomainModel()
                    activityDao.insertActivity(activity.toEntity())
                    Result.success(activity)
                },
                onFailure = { error ->
                    Log.e(tag, "Failed to save activity remotely: ${error.message}")
                    // Fall back to local-only if remote fails
                    val activity = Activity(
                        id = activityId,
                        userId = userId,
                        type = type,
                        durationMinutes = durationMinutes,
                        dateTime = dateTime,
                        caloriesBurned = caloriesBurned,
                        notes = notes
                    )
                    activityDao.insertActivity(activity.toEntity())
                    Result.success(activity)
                }
            )
        } catch (e: Exception) {
            Log.e(tag, "Exception when tracking activity", e)
            Result.failure(e)
        }
    }

    override fun getActivities(userId: String): Flow<List<Activity>> {
        // Immediately return the flow from the local database
        val localActivities = activityDao.getActivitiesByUserId(userId).map { entities ->
            entities.map { it.toDomainModel() }
        }

        // Fetch from remote and update local DB in the background
        scope.launch {
            try {
                val token = userPreferences.authToken.first() ?: return@launch

                Log.d(tag, "Fetching activities from remote for user: $userId")
                activityRemoteDataSource.getUserActivities(token, userId).fold(
                    onSuccess = { activityDtos ->
                        Log.d(tag, "Successfully fetched ${activityDtos.size} activities from remote")
                        // Convert DTOs to entities and save to local database
                        val activities = activityDtos.map { it.toDomainModel().toEntity() }
                        activityDao.insertAllActivities(activities)
                    },
                    onFailure = { error ->
                        Log.e(tag, "Failed to fetch activities from remote", error)
                        // Continue with local data
                    }
                )
            } catch (e: Exception) {
                Log.e(tag, "Exception when fetching remote activities", e)
            }
        }

        return localActivities
    }

    override fun getActivitiesByType(userId: String, type: ActivityType): Flow<List<Activity>> {
        // Attempt to refresh data from remote
        refreshActivitiesInBackground(userId)

        // Return data from local database
        return activityDao.getActivitiesByType(userId, type.name).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getActivitiesByDateRange(
        userId: String,
        startDate: Date,
        endDate: Date
    ): Flow<List<Activity>> {
        // Attempt to refresh data from remote
        refreshActivitiesInBackground(userId)

        return activityDao.getActivitiesByDateRange(
            userId,
            startDate.time,
            endDate.time
        ).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getActivityStats(userId: String): Flow<Map<ActivityType, Int>> {
        // Attempt to refresh data from remote
        refreshActivitiesInBackground(userId)

        return activityDao.getActivitiesByUserId(userId).map { entities ->
            val activitiesByType = entities.groupBy { ActivityType.valueOf(it.type) }
            activitiesByType.mapValues { (_, activities) ->
                activities.sumOf { it.durationMinutes }
            }
        }
    }

    override suspend fun deleteActivity(activityId: String): Result<Unit> {
        return try {
            val token = userPreferences.authToken.first() ?: "dummy_token"

            // Try to delete remotely first
            activityRemoteDataSource.deleteActivity(token, activityId).fold(
                onSuccess = {
                    // Delete from local DB on success
                    activityDao.deleteActivity(activityId)
                    Result.success(Unit)
                },
                onFailure = { error ->
                    Log.e(tag, "Failed to delete activity remotely: ${error.message}")
                    // Fall back to local-only delete if remote fails
                    activityDao.deleteActivity(activityId)
                    Result.success(Unit)
                }
            )
        } catch (e: Exception) {
            Log.e(tag, "Exception when deleting activity", e)
            Result.failure(e)
        }
    }

    // Helper method to refresh activities from remote in the background
    private fun refreshActivitiesInBackground(userId: String) {
        scope.launch {
            try {
                val token = userPreferences.authToken.first() ?: return@launch

                activityRemoteDataSource.getUserActivities(token, userId).fold(
                    onSuccess = { activityDtos ->
                        val activities = activityDtos.map { it.toDomainModel().toEntity() }
                        activityDao.insertAllActivities(activities)
                    },
                    onFailure = { error ->
                        Log.e(tag, "Failed to refresh activities from remote", error)
                    }
                )
            } catch (e: Exception) {
                Log.e(tag, "Exception when refreshing activities", e)
            }
        }
    }
}