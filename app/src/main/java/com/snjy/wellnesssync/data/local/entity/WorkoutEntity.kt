package com.snjy.wellnesssync.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.snjy.wellnesssync.domain.model.DifficultyLevel
import com.snjy.wellnesssync.domain.model.Workout
import com.snjy.wellnesssync.domain.model.WorkoutCategory

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val category: String, // "STRENGTH", "CARDIO", etc.
    val difficultyLevel: String, // "BEGINNER", "INTERMEDIATE", "ADVANCED"
    val durationMinutes: Int,
    val description: String,
    val videoUrl: String,
    val thumbnailUrl: String
)

fun WorkoutEntity.toDomainModel(): Workout {
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

fun Workout.toEntity(): WorkoutEntity {
    return WorkoutEntity(
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