package com.snjy.wellnesssync.domain.model

data class Workout(
    val id: String,
    val name: String,
    val category: WorkoutCategory,
    val difficultyLevel: DifficultyLevel,
    val durationMinutes: Int,
    val description: String,
    val videoUrl: String,
    val thumbnailUrl: String
)

enum class WorkoutCategory {
    STRENGTH,
    CARDIO,
    FLEXIBILITY,
    HIIT,
    YOGA,
    PILATES,
    MEDITATION,
    FULL_BODY,
    UPPER_BODY,
    LOWER_BODY,
    CORE
}

enum class DifficultyLevel {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED
}