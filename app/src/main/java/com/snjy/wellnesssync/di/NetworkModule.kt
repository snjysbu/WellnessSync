package com.snjy.wellnesssync.di

import com.snjy.wellnesssync.data.remote.api.GeminiService
import com.snjy.wellnesssync.data.remote.api.SupabaseService
import com.snjy.wellnesssync.data.remote.api.VideoService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    const val SUPABASE_BASE_URL = "https://xukbpctoobhsgawkhjcq.supabase.co"
    const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inh1a2JwY3Rvb2Joc2dhd2toamNxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDM4NDUyMjUsImV4cCI6MjA1OTQyMTIyNX0.uSkH_8teysMv1WzYLhERy9KXakoAO3NZ8_8Zgo2l1K0"
    private const val GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/"

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                // Add Supabase headers to all requests
                val request = chain.request().newBuilder()
                    .addHeader("apikey", SUPABASE_ANON_KEY)
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    // Rest of your code remains the same
    @Provides
    @Singleton
    fun provideVideoService(): VideoService {
        return VideoService()
    }

    @Provides
    @Singleton
    @Named("supabaseRetrofit")
    fun provideSupabaseRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(SUPABASE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("geminiRetrofit")
    fun provideGeminiRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(GEMINI_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideSupabaseService(@Named("supabaseRetrofit") retrofit: Retrofit): SupabaseService {
        return retrofit.create(SupabaseService::class.java)
    }

    @Provides
    @Singleton
    fun provideGeminiService(@Named("geminiRetrofit") retrofit: Retrofit): GeminiService {
        return retrofit.create(GeminiService::class.java)
    }
}