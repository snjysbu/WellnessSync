package com.snjy.wellnesssync.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.snjy.wellnesssync.data.local.entity.WorkoutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllWorkouts(workouts: List<WorkoutEntity>)

    @Query("SELECT * FROM workouts ORDER BY name ASC")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE category = :category ORDER BY name ASC")
    fun getWorkoutsByCategory(category: String): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE difficultyLevel = :difficultyLevel ORDER BY name ASC")
    fun getWorkoutsByDifficultyLevel(difficultyLevel: String): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    suspend fun getWorkoutById(workoutId: String): WorkoutEntity?

    @Query("SELECT * FROM workouts WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchWorkouts(query: String): Flow<List<WorkoutEntity>>

    @Query("DELETE FROM workouts")
    suspend fun deleteAllWorkouts()
}