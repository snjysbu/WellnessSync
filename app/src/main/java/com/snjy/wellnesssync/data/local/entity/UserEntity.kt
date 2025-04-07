package com.snjy.wellnesssync.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.snjy.wellnesssync.domain.model.DietaryPreference
import com.snjy.wellnesssync.domain.model.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val age: Int,
    val height: Double, // in cm
    val weight: Double, // in kg
    val profession: String,
    val dietaryPreference: String, // "VEGETARIAN" or "NON_VEGETARIAN"
    val profileImageUrl: String?
)

fun UserEntity.toDomainModel(): User {
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

fun User.toEntity(): UserEntity {
    return UserEntity(
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