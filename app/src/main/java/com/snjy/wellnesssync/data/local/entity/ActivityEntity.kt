package com.snjy.wellnesssync.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.snjy.wellnesssync.domain.model.Activity
import com.snjy.wellnesssync.domain.model.ActivityType
import java.util.Date

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val type: String, // "WORKOUT", "MEDITATION", etc.
    val durationMinutes: Int,
    val dateTime: Long, // Timestamp
    val caloriesBurned: Int,
    val notes: String?
)

fun ActivityEntity.toDomainModel(): Activity {
    return Activity(
        id = id,
        userId = userId,
        type = ActivityType.valueOf(type),
        durationMinutes = durationMinutes,
        dateTime = Date(dateTime),
        caloriesBurned = caloriesBurned,
        notes = notes
    )
}

fun Activity.toEntity(): ActivityEntity {
    return ActivityEntity(
        id = id,
        userId = userId,
        type = type.name,
        durationMinutes = durationMinutes,
        dateTime = dateTime.time,
        caloriesBurned = caloriesBurned,
        notes = notes
    )
}