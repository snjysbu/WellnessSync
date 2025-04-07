package com.snjy.wellnesssync.domain.usecase.activity

import com.snjy.wellnesssync.domain.model.Activity
import com.snjy.wellnesssync.domain.model.ActivityType
import com.snjy.wellnesssync.domain.repository.ActivityRepository
import java.util.Date
import javax.inject.Inject

class TrackActivityUseCase @Inject constructor(
    private val activityRepository: ActivityRepository
) {
    suspend operator fun invoke(
        userId: String,
        type: ActivityType,
        durationMinutes: Int,
        dateTime: Date = Date(),
        caloriesBurned: Int,
        notes: String? = null
    ): Result<Activity> {
        // Basic validation
        if (userId.isBlank()) {
            return Result.failure(IllegalArgumentException("User ID cannot be empty"))
        }

        if (durationMinutes <= 0) {
            return Result.failure(IllegalArgumentException("Duration must be greater than 0"))
        }

        if (caloriesBurned < 0) {
            return Result.failure(IllegalArgumentException("Calories burned cannot be negative"))
        }

        return activityRepository.trackActivity(
            userId = userId,
            type = type,
            durationMinutes = durationMinutes,
            dateTime = dateTime,
            caloriesBurned = caloriesBurned,
            notes = notes
        )
    }
}