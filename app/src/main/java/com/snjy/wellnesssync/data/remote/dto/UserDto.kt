package com.snjy.wellnesssync.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.snjy.wellnesssync.domain.model.DietaryPreference
import com.snjy.wellnesssync.domain.model.User

data class UserDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("age")
    val age: Int,

    @SerializedName("height")
    val height: Double,

    @SerializedName("weight")
    val weight: Double,

    @SerializedName("profession")
    val profession: String,

    @SerializedName("dietary_preference")
    val dietaryPreference: String,

    @SerializedName("profile_image_url")
    val profileImageUrl: String?
)

fun UserDto.toDomainModel(): User {
    return User(
        id = id,
        name = name,
        email = email,
        age = age,
        height = height,
        weight = weight,
        profession = profession,
        dietaryPreference = DietaryPreference.valueOf(dietaryPreference),
        profileImageUrl = profileImageUrl
    )
}

fun User.toDto(): UserDto {
    return UserDto(
        id = id,
        name = name,
        email = email,
        age = age,
        height = height,
        weight = weight,
        profession = profession,
        dietaryPreference = dietaryPreference.name,
        profileImageUrl = profileImageUrl
    )
}