package com.snjy.wellnesssync.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.snjy.wellnesssync.presentation.theme.LightPrimary
import com.snjy.wellnesssync.presentation.theme.WellnessSyncTheme

@Composable
fun ProgressIndicator(
    percentage: Float,
    label: String,
    maxValue: Float,
    currentValue: Float,
    modifier: Modifier = Modifier,
    color: Color = LightPrimary,
    backgroundColor: Color = Color.LightGray.copy(alpha = 0.3f),
    animationDuration: Int = 200
) {
    // Ensure percentage is between 0 and 1
    val normalizedPercentage = percentage.coerceIn(0f, 1f)

    // Animation state
    var animationPlayed by remember { mutableStateOf(false) }
    val currentPercentage = animateFloatAsState(
        targetValue = if (animationPlayed) normalizedPercentage else 0f,
        animationSpec = tween(durationMillis = animationDuration),
        label = "Progress animation"
    )

    // Trigger animation when the component is first shown
    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(150.dp)
        ) {
            // Background circle
            Canvas(modifier = Modifier.size(150.dp)) {
                drawArc(
                    color = backgroundColor,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    size = Size(size.width, size.height),
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Progress arc
            Canvas(modifier = Modifier.size(150.dp)) {
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = 360 * currentPercentage.value,
                    useCenter = false,
                    size = Size(size.width, size.height),
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Percentage text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${(currentPercentage.value * 100).toInt()}%",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "$currentValue / $maxValue",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Label text
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun LinearProgressIndicator(
    percentage: Float,
    label: String,
    maxValue: Float,
    currentValue: Float,
    modifier: Modifier = Modifier,
    color: Color = LightPrimary,
    backgroundColor: Color = Color.LightGray.copy(alpha = 0.3f),
    animationDuration: Int = 200
) {
    // Ensure percentage is between 0 and 1
    val normalizedPercentage = percentage.coerceIn(0f, 1f)

    // Animation state
    var animationPlayed by remember { mutableStateOf(false) }
    val currentPercentage = animateFloatAsState(
        targetValue = if (animationPlayed) normalizedPercentage else 0f,
        animationSpec = tween(durationMillis = animationDuration),
        label = "Progress animation"
    )

    // Trigger animation when the component is first shown
    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Header with label and values
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.CenterStart)
            )

            Text(
                text = "$currentValue/$maxValue",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }

        // Progress bar
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .size(height = 12.dp, width = 0.dp)
        ) {
            // Background line
            drawLine(
                color = backgroundColor,
                start = Offset(0f, size.height / 2),
                end = Offset(size.width, size.height / 2),
                strokeWidth = size.height,
                cap = StrokeCap.Round
            )

            // Progress line
            drawLine(
                color = color,
                start = Offset(0f, size.height / 2),
                end = Offset(size.width * currentPercentage.value, size.height / 2),
                strokeWidth = size.height,
                cap = StrokeCap.Round
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CircularProgressIndicatorPreview() {
    WellnessSyncTheme {
        ProgressIndicator(
            percentage = 0.75f,
            label = "Daily Steps",
            maxValue = 10000f,
            currentValue = 7500f,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LinearProgressIndicatorPreview() {
    WellnessSyncTheme {
        LinearProgressIndicator(
            percentage = 0.65f,
            label = "Water Intake",
            maxValue = 8f,
            currentValue = 5.2f,
            modifier = Modifier.padding(16.dp)
        )
    }
}