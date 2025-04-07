package com.snjy.wellnesssync.di

import android.content.Context
import androidx.room.Room
import com.snjy.wellnesssync.data.local.dao.ActivityDao
import com.snjy.wellnesssync.data.local.dao.ChatDao
import com.snjy.wellnesssync.data.local.dao.UserDao
import com.snjy.wellnesssync.data.local.dao.WorkoutDao
import com.snjy.wellnesssync.data.local.database.WellnessSyncDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WellnessSyncDatabase {
        return Room.databaseBuilder(
            context,
            WellnessSyncDatabase::class.java,
            "wellness_sync_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideUserDao(database: WellnessSyncDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideActivityDao(database: WellnessSyncDatabase): ActivityDao {
        return database.activityDao()
    }

    @Provides
    fun provideWorkoutDao(database: WellnessSyncDatabase): WorkoutDao {
        return database.workoutDao()
    }

    @Provides
    fun provideChatDao(database: WellnessSyncDatabase): ChatDao {
        return database.chatDao()
    }
}