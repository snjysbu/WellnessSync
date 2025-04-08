package com.snjy.wellnesssync.data.remote.api

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.net.toUri

interface VideoService {
    /**
     * Extract YouTube video ID from a YouTube URL
     * @param url YouTube URL (e.g., https://youtu.be/9zt6Hc84rjg or https://www.youtube.com/watch?v=9zt6Hc84rjg)
     * @return The video ID or null if not a valid YouTube URL
     */
    fun extractVideoId(url: String): String?

    /**
     * Get the thumbnail URL for a YouTube video
     * @param videoId YouTube video ID
     * @return URL to the thumbnail image
     */
    fun getThumbnailUrl(videoId: String?): String?

    /**
     * Open the YouTube video in the YouTube app or browser
     * @param context Android context
     * @param videoId YouTube video ID
     */
    fun openYouTubeVideo(context: Context, videoId: String?)
}

@Singleton
class YouTubeVideoService @Inject constructor() : VideoService {

    companion object {
        private const val TAG = "YouTubeVideoService"

        // YouTube URL patterns
        private val YOUTUBE_URL_PATTERNS = listOf(
            "https?://youtu\\.be/([a-zA-Z0-9_-]{11})",             // youtu.be short links
            "https?://www\\.youtube\\.com/watch\\?v=([a-zA-Z0-9_-]{11})",  // Standard YouTube URLs
            "https?://www\\.youtube\\.com/embed/([a-zA-Z0-9_-]{11})",      // Embed URLs
            "https?://www\\.youtube\\.com/v/([a-zA-Z0-9_-]{11})"           // Old style URLs
        )

        // High quality thumbnail URL format
        private const val THUMBNAIL_URL_FORMAT = "https://img.youtube.com/vi/%s/hqdefault.jpg"

        // YouTube app and web URLs
        private const val YOUTUBE_APP_URI = "vnd.youtube:%s"
        private const val YOUTUBE_WEB_URL = "https://www.youtube.com/watch?v=%s"
    }

    override fun extractVideoId(url: String): String? {
        // Try each pattern until we find a match
        for (pattern in YOUTUBE_URL_PATTERNS) {
            val regex = Regex(pattern)
            val matchResult = regex.find(url)
            if (matchResult != null && matchResult.groupValues.size > 1) {
                return matchResult.groupValues[1]
            }
        }

        // If we reach here, no pattern matched
        Log.w(TAG, "Could not extract video ID from URL: $url")
        return null
    }

    override fun getThumbnailUrl(videoId: String?): String? {
        if (videoId == null) return null
        return THUMBNAIL_URL_FORMAT.format(videoId)
    }

    // In your YouTubeVideoService
    override fun openYouTubeVideo(context: Context, videoId: String?) {
        if (videoId == null) {
            Log.e(TAG, "Cannot open YouTube video: video ID is null")
            return
        }

        try {
            // First try to open the YouTube app with FLAG_ACTIVITY_NEW_TASK
            val appIntent = Intent(Intent.ACTION_VIEW, YOUTUBE_APP_URI.format(videoId).toUri())
            appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startActivity(appIntent)
        } catch (ex: Exception) {
            try {
                // If YouTube app is not installed, open in browser
                Log.i(TAG, "YouTube app not installed, opening in browser", ex)
                val webIntent = Intent(Intent.ACTION_VIEW, YOUTUBE_WEB_URL.format(videoId).toUri())
                webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(webIntent)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to open YouTube in browser", e)
            }
        }
    }
}