package com.snjy.wellnesssync.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.snjy.wellnesssync.domain.model.ActivityType
import com.snjy.wellnesssync.presentation.theme.LightPrimary
import com.snjy.wellnesssync.presentation.theme.WellnessSyncTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ActivityCard(
    activityType: ActivityType,
    durationMinutes: Int,
    caloriesBurned: Int,
    date: Date,
    modifier: Modifier = Modifier,
    notes: String? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Activity icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(LightPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getActivityIcon(activityType),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Activity details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = activityType.name.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Date
                Text(
                    text = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(date),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (notes != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Duration and calories
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$durationMinutes min",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = Color.Red.copy(alpha = 0.8f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$caloriesBurned cal",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun getActivityIcon(activityType: ActivityType): ImageVector {
    return when (activityType) {
        ActivityType.WORKOUT -> Icons.Default.DirectionsRun
        ActivityType.MEDITATION -> Icons.Default.SelfImprovement
        ActivityType.YOGA -> Icons.Default.SelfImprovement
        ActivityType.WALKING -> Icons.Default.DirectionsRun
        ActivityType.RUNNING -> Icons.Default.DirectionsRun
        ActivityType.CYCLING -> Icons.Default.DirectionsRun
        ActivityType.SWIMMING -> Icons.Default.DirectionsRun
        ActivityType.OTHER -> Icons.Default.DirectionsRun
    }
}

@Preview(showBackground = true)
@Composable
fun ActivityCardPreview() {
    WellnessSyncTheme {
        ActivityCard(
            activityType = ActivityType.RUNNING,
            durationMinutes = 45,
            caloriesBurned = 320,
            date = Date(),
            modifier = Modifier.padding(16.dp),
            notes = "Morning run around the park."
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MeditationActivityCardPreview() {
    WellnessSyncTheme {
        ActivityCard(
            activityType = ActivityType.MEDITATION,
            durationMinutes = 20,
            caloriesBurned = 50,
            date = Date(),
            modifier = Modifier.padding(16.dp)
        )
    }
}