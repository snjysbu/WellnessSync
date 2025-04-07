package com.snjy.wellnesssync.data.remote.api

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.net.toUri

@Singleton
class VideoService @Inject constructor() {

    fun extractVideoId(videoUrl: String): String {
        // Extract YouTube video ID from a full URL
        val pattern = "(?<=watch\\?v=|/videos/|embed/|youtu.be/|/v/|/e/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#&?\\n]*"
        val regex = Regex(pattern)
        val matcher = regex.find(videoUrl)

        return matcher?.value ?: videoUrl
    }

    fun getThumbnailUrl(videoId: String): String {
        // YouTube thumbnail URL format
        return "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
    }

    fun createExoPlayer(context: Context, videoUrl: String): ExoPlayer {
        // If it's a YouTube URL, convert it to an embeddable URL
        val finalUrl = if (videoUrl.contains("youtube.com") || videoUrl.contains("youtu.be")) {
            val videoId = extractVideoId(videoUrl)
            "https://www.youtube.com/embed/$videoId"
        } else {
            videoUrl
        }

        // Create the player
        val player = ExoPlayer.Builder(context).build()
        val mediaItem = MediaItem.fromUri(finalUrl.toUri())
        player.setMediaItem(mediaItem)
        player.prepare()

        return player
    }
}