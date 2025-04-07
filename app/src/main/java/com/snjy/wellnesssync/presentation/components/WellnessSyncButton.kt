package com.snjy.wellnesssync.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.snjy.wellnesssync.presentation.theme.LightPrimary
import com.snjy.wellnesssync.presentation.theme.SecondaryDark
import com.snjy.wellnesssync.presentation.theme.WellnessSyncTheme

@Composable
fun WellnessSyncButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    isSecondary: Boolean = false
) {
    val buttonColors = if (isSecondary) {
        ButtonDefaults.buttonColors(
            containerColor = SecondaryDark,
            contentColor = Color.White
        )
    } else {
        ButtonDefaults.buttonColors(
            containerColor = LightPrimary,
            contentColor = SecondaryDark
        )
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(8.dp),
        colors = buttonColors
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = if (isSecondary) LightPrimary else SecondaryDark,
                modifier = Modifier.padding(4.dp)
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WellnessSyncButtonPreview() {
    WellnessSyncTheme {
        WellnessSyncButton(
            text = "Primary Button",
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WellnessSyncButtonSecondaryPreview() {
    WellnessSyncTheme {
        WellnessSyncButton(
            text = "Secondary Button",
            onClick = {},
            modifier = Modifier.padding(16.dp),
            isSecondary = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WellnessSyncButtonLoadingPreview() {
    WellnessSyncTheme {
        WellnessSyncButton(
            text = "Loading Button",
            onClick = {},
            modifier = Modifier.padding(16.dp),
            isLoading = true
        )
    }
}