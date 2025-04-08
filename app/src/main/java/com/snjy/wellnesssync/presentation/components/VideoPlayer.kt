package com.snjy.wellnesssync.presentation.components

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import com.snjy.wellnesssync.data.remote.api.VideoService
import com.snjy.wellnesssync.presentation.theme.LightPrimary
import com.snjy.wellnesssync.presentation.theme.SecondaryDark

@Composable
fun VideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier,
    thumbnailUrl: String? = null,
    autoPlay: Boolean = false
) {
    val context = LocalContext.current
    var playWhenReady by remember { mutableStateOf(autoPlay) }
    var isPlaying by remember { mutableStateOf(false) }
    var isBuffering by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isPlayerInitialized by remember { mutableStateOf(false) }

    // Create an ExoPlayer instance
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = autoPlay

            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isCurrentlyPlaying: Boolean) {
                    super.onIsPlayingChanged(isCurrentlyPlaying)
                    isPlaying = isCurrentlyPlaying
                    if (isCurrentlyPlaying) {
                        isPlayerInitialized = true
                    }
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    isBuffering = playbackState == Player.STATE_BUFFERING

                    if (playbackState == Player.STATE_IDLE) {
                        errorMessage = "Failed to load video"
                    } else {
                        errorMessage = null
                    }
                }
            })
        }
    }

    // Cleanup player when the composable is disposed
    DisposableEffect(key1 = exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (errorMessage != null) {
            // Error state
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = errorMessage ?: "Error loading video",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // Show thumbnail if available and player not yet initialized
            if (thumbnailUrl != null && !isPlayerInitialized && !autoPlay) {
                AsyncImage(
                    model = thumbnailUrl,
                    contentDescription = "Video Thumbnail",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Video player
            AndroidView(
                factory = { ctx ->
                    FrameLayout(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                update = { frameLayout ->
                    (frameLayout as FrameLayout).removeAllViews()
                    val playerView = androidx.media3.ui.PlayerView(context).apply {
                        player = exoPlayer
                        useController = false // We'll provide our own controls
                        layoutParams = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                        )
                    }
                    frameLayout.addView(playerView)
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Play/Pause button overlay
            if (!isBuffering) {
                FloatingActionButton(
                    onClick = {
                        if (isPlaying) {
                            exoPlayer.pause()
                        } else {
                            exoPlayer.play()
                        }
                        playWhenReady = !playWhenReady
                    },
                    containerColor = LightPrimary,
                    contentColor = SecondaryDark,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Loading indicator
            if (isBuffering) {
                CircularProgressIndicator(
                    color = LightPrimary,
                    modifier = Modifier.size(56.dp)
                )
            }
        }
    }
}

@Composable
fun YouTubeVideoPlayer(
    videoUrl: String,
    videoService: VideoService,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = false
) {
    val context = LocalContext.current

    // Extract video ID and get thumbnail
    val videoId = remember(videoUrl) {
        videoService.extractVideoId(videoUrl)
    }

    val thumbnailUrl = remember(videoId) {
        videoService.getThumbnailUrl(videoId)
    }

    // Launch YouTube app or fallback to browser when the video is played
    Box(modifier = modifier
        .fillMaxWidth()
        .aspectRatio(16f / 9f)
        .clip(RoundedCornerShape(12.dp))
        .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        // Show thumbnail
        thumbnailUrl?.let {
            AsyncImage(
                model = thumbnailUrl,
                contentDescription = "Video Thumbnail",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Play button
        FloatingActionButton(
            onClick = {
                videoService.openYouTubeVideo(context, videoId)
            },
            containerColor = LightPrimary,
            contentColor = SecondaryDark,
            modifier = Modifier.size(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play on YouTube",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}