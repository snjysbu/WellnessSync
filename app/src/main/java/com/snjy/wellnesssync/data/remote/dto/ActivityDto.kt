package com.snjy.wellnesssync.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.snjy.wellnesssync.domain.model.Activity
import com.snjy.wellnesssync.domain.model.ActivityType
import java.util.Date

data class ActivityDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("user_id")
    val userId: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("duration_minutes")
    val durationMinutes: Int,

    @SerializedName("date_time")
    val dateTime: Long,

    @SerializedName("calories_burned")
    val caloriesBurned: Int,

    @SerializedName("notes")
    val notes: String?
)

fun ActivityDto.toDomainModel(): Activity {
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

fun Activity.toDto(): ActivityDto {
    return ActivityDto(
        id = id,
        userId = userId,
        type = type.name,
        durationMinutes = durationMinutes,
        dateTime = dateTime.time,
        caloriesBurned = caloriesBurned,
        notes = notes
    )
}