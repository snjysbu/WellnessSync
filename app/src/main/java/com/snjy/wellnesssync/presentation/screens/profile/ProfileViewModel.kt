package com.snjy.wellnesssync.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snjy.wellnesssync.domain.model.User
import com.snjy.wellnesssync.domain.usecase.auth.LogoutUseCase
import com.snjy.wellnesssync.domain.usecase.user.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _profileState = MutableStateFlow(ProfileState())
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _profileState.update { it.copy(isLoading = true, error = null) }

            getUserProfileUseCase().fold(
                onSuccess = { user ->
                    _profileState.update {
                        it.copy(
                            isLoading = false,
                            user = user
                        )
                    }
                },
                onFailure = { error ->
                    _profileState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load profile"
                        )
                    }
                }
            )
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            logoutUseCase()
            // No need to handle the result as the caller will navigate away
        }
    }
}

data class ProfileState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)