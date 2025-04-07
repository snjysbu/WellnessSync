package com.snjy.wellnesssync.domain.repository

import com.snjy.wellnesssync.domain.model.Activity
import com.snjy.wellnesssync.domain.model.ActivityType
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface ActivityRepository {
    suspend fun trackActivity(
        userId: String,
        type: ActivityType,
        durationMinutes: Int,
        dateTime: Date,
        caloriesBurned: Int,
        notes: String?
    ): Result<Activity>

    fun getActivities(userId: String): Flow<List<Activity>>

    fun getActivitiesByType(userId: String, type: ActivityType): Flow<List<Activity>>

    fun getActivitiesByDateRange(userId: String, startDate: Date, endDate: Date): Flow<List<Activity>>

    fun getActivityStats(userId: String): Flow<Map<ActivityType, Int>> // ActivityType to total minutes

    suspend fun deleteActivity(activityId: String): Result<Unit>
}