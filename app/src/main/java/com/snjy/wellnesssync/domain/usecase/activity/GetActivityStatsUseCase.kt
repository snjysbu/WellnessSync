package com.snjy.wellnesssync.domain.usecase.activity

import com.snjy.wellnesssync.domain.model.ActivityType
import com.snjy.wellnesssync.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetActivityStatsUseCase @Inject constructor(
    private val activityRepository: ActivityRepository
) {
    operator fun invoke(userId: String): Flow<Map<ActivityType, Int>> {
        return activityRepository.getActivityStats(userId)
    }
}