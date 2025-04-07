package com.snjy.wellnesssync.di

import com.snjy.wellnesssync.data.local.dao.ActivityDao
import com.snjy.wellnesssync.data.local.dao.ChatDao
import com.snjy.wellnesssync.data.local.dao.UserDao
import com.snjy.wellnesssync.data.local.dao.WorkoutDao
import com.snjy.wellnesssync.data.preferences.UserPreferences
import com.snjy.wellnesssync.data.remote.api.GeminiService
import com.snjy.wellnesssync.data.remote.api.SupabaseService
import com.snjy.wellnesssync.data.remote.api.VideoService
import com.snjy.wellnesssync.data.remote.datasource.ActivityRemoteDataSource
import com.snjy.wellnesssync.data.remote.datasource.ChatRemoteDataSource
import com.snjy.wellnesssync.data.remote.datasource.UserRemoteDataSource
import com.snjy.wellnesssync.data.remote.datasource.WorkoutRemoteDataSource
import com.snjy.wellnesssync.data.repository.ActivityRepositoryImpl
import com.snjy.wellnesssync.data.repository.ChatRepositoryImpl
import com.snjy.wellnesssync.data.repository.UserRepositoryImpl
import com.snjy.wellnesssync.data.repository.WorkoutRepositoryImpl
import com.snjy.wellnesssync.domain.repository.ActivityRepository
import com.snjy.wellnesssync.domain.repository.ChatRepository
import com.snjy.wellnesssync.domain.repository.UserRepository
import com.snjy.wellnesssync.domain.repository.WorkoutRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRemoteDataSource(supabaseService: SupabaseService): UserRemoteDataSource {
        return UserRemoteDataSource(supabaseService)
    }

    @Provides
    @Singleton
    fun provideActivityRemoteDataSource(supabaseService: SupabaseService): ActivityRemoteDataSource {
        return ActivityRemoteDataSource(supabaseService)
    }

    @Provides
    @Singleton
    fun provideWorkoutRemoteDataSource(
        supabaseService: SupabaseService,
        videoService: VideoService
    ): WorkoutRemoteDataSource {
        return WorkoutRemoteDataSource(supabaseService, videoService)
    }

    @Provides
    @Singleton
    fun provideChatRemoteDataSource(geminiService: GeminiService): ChatRemoteDataSource {
        return ChatRemoteDataSource(geminiService)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        userDao: UserDao,
        userRemoteDataSource: UserRemoteDataSource,
        userPreferences: UserPreferences
    ): UserRepository {
        return UserRepositoryImpl(userDao, userRemoteDataSource, userPreferences)
    }

    @Provides
    @Singleton
    fun provideActivityRepository(
        activityDao: ActivityDao,
        activityRemoteDataSource: ActivityRemoteDataSource
    ): ActivityRepository {
        return ActivityRepositoryImpl(activityDao, activityRemoteDataSource)
    }

    @Provides
    @Singleton
    fun provideWorkoutRepository(
        workoutDao: WorkoutDao,
        workoutRemoteDataSource: WorkoutRemoteDataSource
    ): WorkoutRepository {
        return WorkoutRepositoryImpl(workoutDao, workoutRemoteDataSource)
    }

    @Provides
    @Singleton
    fun provideChatRepository(
        chatDao: ChatDao,
        chatRemoteDataSource: ChatRemoteDataSource
    ): ChatRepository {
        return ChatRepositoryImpl(chatDao, chatRemoteDataSource)
    }
}