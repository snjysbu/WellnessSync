package com.snjy.wellnesssync.presentation.screens.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snjy.wellnesssync.domain.usecase.auth.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<RegisterEvent>()
    val events: SharedFlow<RegisterEvent> = _events.asSharedFlow()

    fun onNameChanged(name: String) {
        _state.update { it.copy(name = name, nameError = null) }
    }

    fun onEmailChanged(email: String) {
        _state.update { it.copy(email = email, emailError = null) }
    }

    fun onPasswordChanged(password: String) {
        _state.update { it.copy(password = password, passwordError = null) }
    }

    fun onAgeChanged(age: String) {
        _state.update { it.copy(age = age, ageError = null) }
    }

    fun onHeightChanged(height: String) {
        _state.update { it.copy(height = height, heightError = null) }
    }

    fun onWeightChanged(weight: String) {
        _state.update { it.copy(weight = weight, weightError = null) }
    }

    fun onProfessionChanged(profession: String) {
        _state.update { it.copy(profession = profession, professionError = null) }
    }

    fun onDietaryPreferenceChanged(dietaryPreference: String) {
        _state.update { it.copy(dietaryPreference = dietaryPreference, dietaryPreferenceError = null) }
    }

    fun register() {
        if (!validateInputs()) {
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, generalError = null) }

            val ageInt = _state.value.age.toIntOrNull() ?: 0
            val heightDouble = _state.value.height.toDoubleOrNull() ?: 0.0
            val weightDouble = _state.value.weight.toDoubleOrNull() ?: 0.0

            registerUseCase(
                name = _state.value.name.trim(),
                email = _state.value.email.trim(),
                password = _state.value.password,
                age = ageInt,
                height = heightDouble,
                weight = weightDouble,
                profession = _state.value.profession.trim(),
                dietaryPreference = _state.value.dietaryPreference
            ).fold(
                onSuccess = {
                    _state.update { it.copy(isLoading = false) }
                    _events.emit(RegisterEvent.RegisterSuccess)
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            generalError = error.message ?: "An unknown error occurred"
                        )
                    }
                }
            )
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        if (_state.value.name.isBlank()) {
            _state.update { it.copy(nameError = "Name cannot be empty") }
            isValid = false
        }

        if (_state.value.email.isBlank()) {
            _state.update { it.copy(emailError = "Email cannot be empty") }
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(_state.value.email).matches()) {
            _state.update { it.copy(emailError = "Invalid email format") }
            isValid = false
        }

        if (_state.value.password.isBlank()) {
            _state.update { it.copy(passwordError = "Password cannot be empty") }
            isValid = false
        } else if (_state.value.password.length < 8) {  // Supabase often requires 8+ characters
            _state.update { it.copy(passwordError = "Password must be at least 8 characters") }
            isValid = false
        }

        if (_state.value.age.isBlank()) {
            _state.update { it.copy(ageError = "Age cannot be empty") }
            isValid = false
        } else {
            val ageInt = _state.value.age.toIntOrNull()
            if (ageInt == null || ageInt <= 0) {
                _state.update { it.copy(ageError = "Age must be a positive number") }
                isValid = false
            }
        }

        if (_state.value.height.isBlank()) {
            _state.update { it.copy(heightError = "Height cannot be empty") }
            isValid = false
        } else {
            val heightDouble = _state.value.height.toDoubleOrNull()
            if (heightDouble == null || heightDouble <= 0) {
                _state.update { it.copy(heightError = "Height must be a positive number") }
                isValid = false
            }
        }

        if (_state.value.weight.isBlank()) {
            _state.update { it.copy(weightError = "Weight cannot be empty") }
            isValid = false
        } else {
            val weightDouble = _state.value.weight.toDoubleOrNull()
            if (weightDouble == null || weightDouble <= 0) {
                _state.update { it.copy(weightError = "Weight must be a positive number") }
                isValid = false
            }
        }

        if (_state.value.profession.isBlank()) {
            _state.update { it.copy(professionError = "Profession cannot be empty") }
            isValid = false
        }

        if (_state.value.dietaryPreference.isBlank()) {
            _state.update { it.copy(dietaryPreferenceError = "Please select a dietary preference") }
            isValid = false
        }

        return isValid
    }
}

data class RegisterState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val age: String = "",
    val height: String = "",
    val weight: String = "",
    val profession: String = "",
    val dietaryPreference: String = "",
    val isLoading: Boolean = false,
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val ageError: String? = null,
    val heightError: String? = null,
    val weightError: String? = null,
    val professionError: String? = null,
    val dietaryPreferenceError: String? = null,
    val generalError: String? = null
)

sealed class RegisterEvent {
    data object RegisterSuccess : RegisterEvent()
}