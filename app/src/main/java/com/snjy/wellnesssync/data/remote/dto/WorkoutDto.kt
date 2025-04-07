package com.snjy.wellnesssync.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.snjy.wellnesssync.domain.model.DifficultyLevel
import com.snjy.wellnesssync.domain.model.Workout
import com.snjy.wellnesssync.domain.model.WorkoutCategory

data class WorkoutDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("category")
    val category: String,

    @SerializedName("difficulty_level")
    val difficultyLevel: String,

    @SerializedName("duration_minutes")
    val durationMinutes: Int,

    @SerializedName("description")
    val description: String,

    @SerializedName("video_url")
    val videoUrl: String,

    @SerializedName("thumbnail_url")
    val thumbnailUrl: String
)

fun WorkoutDto.toDomainModel(): Workout {
    return Workout(
        id = id,
        name = name,
        category = WorkoutCategory.valueOf(category),
        difficultyLevel = DifficultyLevel.valueOf(difficultyLevel),
        durationMinutes = durationMinutes,
        description = description,
        videoUrl = videoUrl,
        thumbnailUrl = thumbnailUrl
    )
}

fun Workout.toDto(): WorkoutDto {
    return WorkoutDto(
        id = id,
        name = name,
        category = category.name,
        difficultyLevel = difficultyLevel.name,
        durationMinutes = durationMinutes,
        description = description,
        videoUrl = videoUrl,
        thumbnailUrl = thumbnailUrl
    )
}