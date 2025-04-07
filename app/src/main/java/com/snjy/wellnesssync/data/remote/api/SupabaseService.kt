package com.snjy.wellnesssync.data.remote.api

import com.snjy.wellnesssync.data.remote.dto.ActivityDto
import com.snjy.wellnesssync.data.remote.dto.UserDto
import com.snjy.wellnesssync.data.remote.dto.WorkoutDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface SupabaseService {
    // Authentication
    @POST("auth/v1/signup")
    suspend fun registerUser(@Body user: UserDto): Response<UserDto>

    @POST("auth/v1/token?grant_type=password")
    suspend fun loginUser(@Body credentials: Map<String, String>): Response<Map<String, Any>>

    @POST("auth/v1/logout")
    suspend fun logoutUser(@Header("Authorization") token: String): Response<Unit>

    // User
    @GET("rest/v1/users")
    suspend fun getUserProfile(
        @Header("Authorization") token: String,
        @Query("id") userId: String,
        @Query("select") select: String = "*"
    ): Response<UserDto>

    @PUT("rest/v1/users")
    suspend fun updateUserProfile(
        @Header("Authorization") token: String,
        @Body user: UserDto
    ): Response<UserDto>

    // Activities
    @GET("rest/v1/activities")
    suspend fun getUserActivities(
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,
        @Query("select") select: String = "*"
    ): Response<List<ActivityDto>>

    @POST("rest/v1/activities")
    suspend fun createActivity(
        @Header("Authorization") token: String,
        @Body activity: ActivityDto
    ): Response<ActivityDto>

    @DELETE("rest/v1/activities")
    suspend fun deleteActivity(
        @Header("Authorization") token: String,
        @Query("id") activityId: String
    ): Response<Unit>

    // Workouts
    @GET("rest/v1/workouts")
    suspend fun getAllWorkouts(
        @Header("Authorization") token: String,
        @Query("select") select: String = "*"
    ): Response<List<WorkoutDto>>

    @GET("rest/v1/workouts")
    suspend fun getWorkoutsByCategory(
        @Header("Authorization") token: String,
        @Query("category") category: String,
        @Query("select") select: String = "*"
    ): Response<List<WorkoutDto>>

    @GET("rest/v1/workouts")
    suspend fun getWorkoutById(
        @Header("Authorization") token: String,
        @Query("id") workoutId: String,
        @Query("select") select: String = "*"
    ): Response<WorkoutDto>
}