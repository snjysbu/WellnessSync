package com.snjy.wellnesssync.domain.usecase.workout

import com.snjy.wellnesssync.domain.model.Activity
import com.snjy.wellnesssync.domain.model.ActivityType
import com.snjy.wellnesssync.domain.model.DifficultyLevel
import com.snjy.wellnesssync.domain.model.Workout
import com.snjy.wellnesssync.domain.model.WorkoutCategory
import com.snjy.wellnesssync.domain.repository.ActivityRepository
import com.snjy.wellnesssync.domain.repository.WorkoutRepository
import java.util.Date
import javax.inject.Inject

class LogWorkoutUseCase @Inject constructor(
    private val activityRepository: ActivityRepository,
    private val workoutRepository: WorkoutRepository
) {
    /**
     * Logs a completed workout as an activity.
     *
     * @param userId The ID of the user completing the workout
     * @param workoutId The ID of the completed workout
     * @param durationMinutes The actual duration in minutes (may differ from workout's default duration)
     * @param caloriesBurned Estimated calories burned
     * @param completionDate When the workout was completed (defaults to current time)
     * @param notes Optional notes about the workout session
     * @return Result containing the created Activity if successful
     */
    suspend operator fun invoke(
        userId: String,
        workoutId: String,
        durationMinutes: Int? = null,
        caloriesBurned: Int? = null,
        completionDate: Date = Date(),
        notes: String? = null
    ): Result<Activity> {
        if (userId.isBlank()) {
            return Result.failure(IllegalArgumentException("User ID cannot be empty"))
        }

        // Fetch the workout details
        return workoutRepository.getWorkoutById(workoutId).fold(
            onSuccess = { workout ->
                // Use provided duration or default workout duration
                val finalDuration = durationMinutes ?: workout.durationMinutes

                // Calculate estimated calories if not provided
                // A simple formula based on workout type and duration
                val finalCalories = caloriesBurned ?: calculateEstimatedCalories(workout, finalDuration)

                // Generate notes if not provided
                val finalNotes = notes ?: generateDefaultNotes(workout)

                // Map workout category to activity type
                val activityType = mapWorkoutCategoryToActivityType(workout)

                // Log the activity
                activityRepository.trackActivity(
                    userId = userId,
                    type = activityType,
                    durationMinutes = finalDuration,
                    dateTime = completionDate,
                    caloriesBurned = finalCalories,
                    notes = finalNotes
                )
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }

    /**
     * Maps workout category to activity type
     */
    private fun mapWorkoutCategoryToActivityType(workout: Workout): ActivityType {
        return when (workout.category) {
            WorkoutCategory.YOGA -> ActivityType.YOGA
            WorkoutCategory.MEDITATION -> ActivityType.MEDITATION
            WorkoutCategory.CARDIO, WorkoutCategory.HIIT -> ActivityType.RUNNING
            else -> ActivityType.WORKOUT
        }
    }

    /**
     * Calculates estimated calories burned based on workout type and duration
     */
    private fun calculateEstimatedCalories(workout: Workout, durationMinutes: Int): Int {
        // Very simple estimation - in a real app you would have more sophisticated calculations
        // that take into account user weight, intensity, etc.
        val baseCaloriesPerMinute = when (workout.category) {
            WorkoutCategory.CARDIO, WorkoutCategory.HIIT -> 10
            WorkoutCategory.STRENGTH, WorkoutCategory.FULL_BODY -> 8
            WorkoutCategory.FLEXIBILITY, WorkoutCategory.YOGA -> 5
            WorkoutCategory.MEDITATION -> 2
            else -> 6
        }

        val intensityMultiplier = when (workout.difficultyLevel) {
            DifficultyLevel.BEGINNER -> 0.8
            DifficultyLevel.INTERMEDIATE -> 1.0
            DifficultyLevel.ADVANCED -> 1.2
        }

        return (baseCaloriesPerMinute * durationMinutes * intensityMultiplier).toInt()
    }

    /**
     * Generates default notes based on workout information
     */
    private fun generateDefaultNotes(workout: Workout): String {
        return "Completed ${workout.name} (${workout.difficultyLevel.name.lowercase()} level)"
    }
}