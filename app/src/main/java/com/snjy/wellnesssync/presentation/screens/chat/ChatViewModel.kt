package com.snjy.wellnesssync.presentation.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snjy.wellnesssync.domain.model.ChatMessage
import com.snjy.wellnesssync.domain.model.MessageSender
import com.snjy.wellnesssync.domain.usecase.chat.GetChatHistoryUseCase
import com.snjy.wellnesssync.domain.usecase.chat.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    private val getChatHistoryUseCase: GetChatHistoryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    // Hardcoded user ID for now, in a real app this would come from the user repository
    private val userId = "current_user"

    init {
        loadChatHistory()
    }

    private fun loadChatHistory() {
        getChatHistoryUseCase(userId)
            .onEach { messages ->
                _state.update { it.copy(messages = messages) }
            }
            .launchIn(viewModelScope)
    }

    fun sendMessage(messageContent: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            sendMessageUseCase(userId, messageContent).fold(
                onSuccess = { botResponse ->
                    _state.update { it.copy(isLoading = false) }
                },
                onFailure = { error ->
                    // If the API call fails, still show the user message but add an error message
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Failed to get response"
                        )
                    }
                }
            )
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            getChatHistoryUseCase.clear(userId)
        }
    }
}

data class ChatState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)