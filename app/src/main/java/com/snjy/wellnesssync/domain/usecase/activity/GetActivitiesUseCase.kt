package com.snjy.wellnesssync.domain.usecase.activity

import com.snjy.wellnesssync.domain.model.Activity
import com.snjy.wellnesssync.domain.model.ActivityType
import com.snjy.wellnesssync.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class GetActivitiesUseCase @Inject constructor(
    private val activityRepository: ActivityRepository
) {
    operator fun invoke(userId: String): Flow<List<Activity>> {
        return activityRepository.getActivities(userId)
    }

    fun byType(userId: String, type: ActivityType): Flow<List<Activity>> {
        return activityRepository.getActivitiesByType(userId, type)
    }

    fun byDateRange(userId: String, startDate: Date, endDate: Date): Flow<List<Activity>> {
        return activityRepository.getActivitiesByDateRange(userId, startDate, endDate)
    }
}