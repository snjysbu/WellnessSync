package com.snjy.wellnesssync.utils

import android.util.Log
import com.snjy.wellnesssync.di.NetworkModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

object SupabaseDebug {
    private const val TAG = "SupabaseDebug"

    /**
     * Test direct connection to Supabase to debug issues
     */
    suspend fun testWorkoutsConnection() {
        withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build()

                val request = Request.Builder()
                    .url("${NetworkModule.SUPABASE_BASE_URL}/rest/v1/workouts?select=*")
                    .addHeader("apikey", NetworkModule.SUPABASE_ANON_KEY)
                    .addHeader("Authorization", "Bearer ${NetworkModule.SUPABASE_ANON_KEY}")
                    .addHeader("Content-Type", "application/json")
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    Log.d(TAG, "Direct connection successful: $responseBody")
                } else {
                    Log.e(TAG, "Direct connection failed: ${response.code} - ${response.message}")
                    val responseBody = response.body?.string()
                    Log.e(TAG, "Error body: $responseBody")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception in direct connection test", e)
            }
        }
    }
}