package com.snjy.wellnesssync.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.snjy.wellnesssync.presentation.theme.LightPrimary
import com.snjy.wellnesssync.presentation.theme.WellnessSyncTheme

@Composable
fun WellnessSyncTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
    errorMessage: String? = null,
    isError: Boolean = errorMessage != null,
    maxLines: Int = 1
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = label?.let { { Text(text = it) } },
        placeholder = placeholder?.let { { Text(text = it) } },
        leadingIcon = leadingIcon?.let {
            { Icon(imageVector = it, contentDescription = null) }
        },
        trailingIcon = when {
            isPassword -> {
                {
                    val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = icon, contentDescription = null)
                    }
                }
            }
            trailingIcon != null && onTrailingIconClick != null -> {
                {
                    IconButton(onClick = onTrailingIconClick) {
                        Icon(imageVector = trailingIcon, contentDescription = null)
                    }
                }
            }
            else -> null
        },
        isError = isError,
        visualTransformation = when {
            isPassword && !passwordVisible -> PasswordVisualTransformation()
            else -> VisualTransformation.None
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onDone = { onImeAction() },
            onNext = { onImeAction() },
            onSearch = { onImeAction() },
            onSend = { onImeAction() },
            onGo = { onImeAction() }
        ),
        singleLine = maxLines == 1,
        maxLines = maxLines,
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = LightPrimary,
            focusedLabelColor = LightPrimary,
            cursorColor = LightPrimary
        )
    )

    if (errorMessage != null) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WellnessSyncTextFieldPreview() {
    WellnessSyncTheme {
        WellnessSyncTextField(
            value = "",
            onValueChange = {},
            label = "Email",
            placeholder = "Enter your email",
            leadingIcon = Icons.Default.Email,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WellnessSyncTextFieldPasswordPreview() {
    WellnessSyncTheme {
        WellnessSyncTextField(
            value = "password123",
            onValueChange = {},
            label = "Password",
            isPassword = true,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WellnessSyncTextFieldErrorPreview() {
    WellnessSyncTheme {
        WellnessSyncTextField(
            value = "invalid@email",
            onValueChange = {},
            label = "Email",
            leadingIcon = Icons.Default.Email,
            errorMessage = "Invalid email format",
            modifier = Modifier.padding(16.dp)
        )
    }
}