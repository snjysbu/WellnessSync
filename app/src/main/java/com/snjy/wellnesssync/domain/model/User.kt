package com.snjy.wellnesssync.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val age: Int,
    val height: Double, // in cm
    val weight: Double, // in kg
    val profession: String,
    val dietaryPreference: DietaryPreference,
    val profileImageUrl: String? = null
)

enum class DietaryPreference {
    VEGETARIAN,
    NON_VEGETARIAN
}