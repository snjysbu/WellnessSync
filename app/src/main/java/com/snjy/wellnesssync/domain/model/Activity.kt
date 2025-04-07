package com.snjy.wellnesssync.domain.model

import java.util.Date

data class Activity(
    val id: String,
    val userId: String,
    val type: ActivityType,
    val durationMinutes: Int,
    val dateTime: Date,
    val caloriesBurned: Int, // Estimated calories
    val notes: String? = null
)

enum class ActivityType {
    WORKOUT,
    MEDITATION,
    YOGA,
    WALKING,
    RUNNING,
    CYCLING,
    SWIMMING,
    OTHER
}