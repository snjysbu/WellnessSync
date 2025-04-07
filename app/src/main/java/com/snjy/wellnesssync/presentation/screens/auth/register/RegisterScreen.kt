package com.snjy.wellnesssync.presentation.screens.auth.register

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.snjy.wellnesssync.domain.model.DietaryPreference
import com.snjy.wellnesssync.presentation.components.WellnessSyncButton
import com.snjy.wellnesssync.presentation.components.WellnessSyncTextField
import com.snjy.wellnesssync.presentation.theme.LightPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.events.collect { event ->
            when (event) {
                is RegisterEvent.RegisterSuccess -> {
                    Log.d("RegisterScreen", "Registration success, navigating...")
                    onRegisterSuccess()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Register") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LightPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WellnessSyncTextField(
                    value = state.name,
                    onValueChange = { viewModel.onNameChanged(it) },
                    label = "Name",
                    leadingIcon = Icons.Default.Person,
                    errorMessage = state.nameError,
                    imeAction = ImeAction.Next
                )

                Spacer(modifier = Modifier.height(16.dp))

                WellnessSyncTextField(
                    value = state.email,
                    onValueChange = { viewModel.onEmailChanged(it) },
                    label = "Email",
                    leadingIcon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email,
                    errorMessage = state.emailError,
                    imeAction = ImeAction.Next
                )

                Spacer(modifier = Modifier.height(16.dp))

                WellnessSyncTextField(
                    value = state.password,
                    onValueChange = { viewModel.onPasswordChanged(it) },
                    label = "Password",
                    isPassword = true,
                    errorMessage = state.passwordError,
                    imeAction = ImeAction.Next
                )

                Spacer(modifier = Modifier.height(16.dp))

                WellnessSyncTextField(
                    value = state.age,
                    onValueChange = { viewModel.onAgeChanged(it) },
                    label = "Age",
                    keyboardType = KeyboardType.Number,
                    errorMessage = state.ageError,
                    imeAction = ImeAction.Next
                )

                Spacer(modifier = Modifier.height(16.dp))

                WellnessSyncTextField(
                    value = state.height,
                    onValueChange = { viewModel.onHeightChanged(it) },
                    label = "Height (cm)",
                    keyboardType = KeyboardType.Decimal,
                    errorMessage = state.heightError,
                    imeAction = ImeAction.Next
                )

                Spacer(modifier = Modifier.height(16.dp))

                WellnessSyncTextField(
                    value = state.weight,
                    onValueChange = { viewModel.onWeightChanged(it) },
                    label = "Weight (kg)",
                    keyboardType = KeyboardType.Decimal,
                    errorMessage = state.weightError,
                    imeAction = ImeAction.Next
                )

                Spacer(modifier = Modifier.height(16.dp))

                WellnessSyncTextField(
                    value = state.profession,
                    onValueChange = { viewModel.onProfessionChanged(it) },
                    label = "Profession",
                    errorMessage = state.professionError,
                    imeAction = ImeAction.Next
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Dietary preference selection
                Text(
                    text = "Dietary Preference",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = state.dietaryPreference == DietaryPreference.VEGETARIAN.name,
                        onClick = { viewModel.onDietaryPreferenceChanged(DietaryPreference.VEGETARIAN.name) }
                    )
                    Text("Vegetarian")

                    Spacer(modifier = Modifier.weight(1f))

                    RadioButton(
                        selected = state.dietaryPreference == DietaryPreference.NON_VEGETARIAN.name,
                        onClick = { viewModel.onDietaryPreferenceChanged(DietaryPreference.NON_VEGETARIAN.name) }
                    )
                    Text("Non-Vegetarian")
                }

                if (state.dietaryPreferenceError != null) {
                    Text(
                        text = state.dietaryPreferenceError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                WellnessSyncButton(
                    text = "Register",
                    onClick = { viewModel.register() },
                    isLoading = state.isLoading,
                    enabled = !state.isLoading
                )
            }
        }
    }
}