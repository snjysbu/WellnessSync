package com.snjy.wellnesssync.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.snjy.wellnesssync.data.local.dao.ActivityDao
import com.snjy.wellnesssync.data.local.dao.ChatDao
import com.snjy.wellnesssync.data.local.dao.UserDao
import com.snjy.wellnesssync.data.local.dao.WorkoutDao
import com.snjy.wellnesssync.data.local.entity.ActivityEntity
import com.snjy.wellnesssync.data.local.entity.ChatEntity
import com.snjy.wellnesssync.data.local.entity.UserEntity
import com.snjy.wellnesssync.data.local.entity.WorkoutEntity

@Database(
    entities = [
        UserEntity::class,
        ActivityEntity::class,
        WorkoutEntity::class,
        ChatEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class WellnessSyncDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun activityDao(): ActivityDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun chatDao(): ChatDao
}