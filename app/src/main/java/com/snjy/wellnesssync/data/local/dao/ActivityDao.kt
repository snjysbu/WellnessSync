package com.snjy.wellnesssync.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.snjy.wellnesssync.data.local.entity.ActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityEntity)

    @Query("SELECT * FROM activities WHERE userId = :userId ORDER BY dateTime DESC")
    fun getActivitiesByUserId(userId: String): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities WHERE userId = :userId AND type = :type ORDER BY dateTime DESC")
    fun getActivitiesByType(userId: String, type: String): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities WHERE userId = :userId AND dateTime BETWEEN :startTime AND :endTime ORDER BY dateTime DESC")
    fun getActivitiesByDateRange(userId: String, startTime: Long, endTime: Long): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities WHERE id = :activityId")
    suspend fun getActivityById(activityId: String): ActivityEntity?

    @Query("DELETE FROM activities WHERE id = :activityId")
    suspend fun deleteActivity(activityId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllActivities(activities: List<ActivityEntity>)
}