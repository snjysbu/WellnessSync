package com.snjy.wellnesssync.data.repository

import com.snjy.wellnesssync.data.local.dao.ActivityDao
import com.snjy.wellnesssync.data.local.entity.toEntity
import com.snjy.wellnesssync.data.local.entity.toDomainModel
import com.snjy.wellnesssync.data.remote.datasource.ActivityRemoteDataSource
import com.snjy.wellnesssync.data.remote.dto.ActivityDto
import com.snjy.wellnesssync.data.remote.dto.toDomainModel
import com.snjy.wellnesssync.domain.model.Activity
import com.snjy.wellnesssync.domain.model.ActivityType
import com.snjy.wellnesssync.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class ActivityRepositoryImpl @Inject constructor(
    private val activityDao: ActivityDao,
    private val activityRemoteDataSource: ActivityRemoteDataSource
) : ActivityRepository {

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
            // For simplicity, we're assuming an authorized token is available elsewhere
            // In a real implementation, this would be retrieved from UserPreferences
            val dummyToken = "dummy_token"

            // Try to save remotely first
            activityRemoteDataSource.createActivity(dummyToken, activityDto).fold(
                onSuccess = { remoteActivity ->
                    // Save to local DB on success
                    val activity = remoteActivity.toDomainModel()
                    activityDao.insertActivity(activity.toEntity())
                    Result.success(activity)
                },
                onFailure = {
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
            Result.failure(e)
        }
    }

    override fun getActivities(userId: String): Flow<List<Activity>> {
        return activityDao.getActivitiesByUserId(userId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getActivitiesByType(userId: String, type: ActivityType): Flow<List<Activity>> {
        return activityDao.getActivitiesByType(userId, type.name).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getActivitiesByDateRange(
        userId: String,
        startDate: Date,
        endDate: Date
    ): Flow<List<Activity>> {
        return activityDao.getActivitiesByDateRange(
            userId,
            startDate.time,
            endDate.time
        ).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getActivityStats(userId: String): Flow<Map<ActivityType, Int>> {
        return activityDao.getActivitiesByUserId(userId).map { entities ->
            val activitiesByType = entities.groupBy { ActivityType.valueOf(it.type) }
            activitiesByType.mapValues { (_, activities) ->
                activities.sumOf { it.durationMinutes }
            }
        }
    }

    override suspend fun deleteActivity(activityId: String): Result<Unit> {
        return try {
            // For simplicity, we're assuming an authorized token is available elsewhere
            // In a real implementation, this would be retrieved from UserPreferences
            val dummyToken = "dummy_token"

            // Try to delete remotely first
            activityRemoteDataSource.deleteActivity(dummyToken, activityId).fold(
                onSuccess = {
                    // Delete from local DB on success
                    activityDao.deleteActivity(activityId)
                    Result.success(Unit)
                },
                onFailure = {
                    // Fall back to local-only delete if remote fails
                    activityDao.deleteActivity(activityId)
                    Result.success(Unit)
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}