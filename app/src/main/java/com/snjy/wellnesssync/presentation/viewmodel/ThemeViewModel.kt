package com.snjy.wellnesssync.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snjy.wellnesssync.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.getUserThemePreference()
                .collect { isDarkMode ->
                    _isDarkTheme.value = isDarkMode
                }
        }
    }

    fun toggleTheme() {
        viewModelScope.launch {
            userRepository.setUserThemePreference(!isDarkTheme.value)
        }
    }
}