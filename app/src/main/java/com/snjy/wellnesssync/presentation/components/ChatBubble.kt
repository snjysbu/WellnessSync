package com.snjy.wellnesssync.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.snjy.wellnesssync.domain.model.MessageSender
import com.snjy.wellnesssync.presentation.theme.LightPrimary
import com.snjy.wellnesssync.presentation.theme.SecondaryDark
import com.snjy.wellnesssync.presentation.theme.WellnessSyncTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatBubble(
    message: String,
    sender: MessageSender,
    timestamp: Date,
    modifier: Modifier = Modifier
) {
    val isUser = sender == MessageSender.USER
    val timeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(timestamp)

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isUser) {
            // Bot avatar
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(LightPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "AI",
                    style = MaterialTheme.typography.labelMedium,
                    color = SecondaryDark
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
            modifier = Modifier.weight(4f, fill = false)
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 0.dp,
                    bottomEnd = if (isUser) 0.dp else 16.dp
                ),
                color = if (isUser) LightPrimary else MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isUser) SecondaryDark else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Text(
                text = timeString,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)
            )
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))

            // User avatar
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(SecondaryDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.size(24.dp)
                )
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserChatBubblePreview() {
    WellnessSyncTheme {
        ChatBubble(
            message = "Hey, can you recommend a quick workout for me today?",
            sender = MessageSender.USER,
            timestamp = Date(),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BotChatBubblePreview() {
    WellnessSyncTheme {
        ChatBubble(
            message = "Sure! I recommend a 20-minute HIIT workout focusing on full body. Would you like me to suggest some specific exercises?",
            sender = MessageSender.BOT,
            timestamp = Date(),
            modifier = Modifier.padding(16.dp)
        )
    }
}