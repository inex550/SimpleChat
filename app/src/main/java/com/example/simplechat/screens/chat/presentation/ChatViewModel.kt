package com.example.simplechat.screens.chat.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplechat.core.coreui.error.UiErrorHandler
import com.example.simplechat.core.coreapi.common.preference.UserPreferenceStorage
import com.example.simplechat.screens.chat.domain.models.Message
import com.example.simplechat.screens.chat.domain.models.Update
import com.example.simplechat.screens.chat.domain.usecase.GetChatMessagesUseCase
import com.example.simplechat.screens.chat.domain.usecase.SendMessageUseCase
import com.example.simplechat.screens.chat.domain.websocket.UpdatesWebSocket
import com.example.simplechat.screens.chats.domain.models.Chat
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    val router: Router,
    val userPreferenceStorage: UserPreferenceStorage,
    private val updatesWebSocket: UpdatesWebSocket,
    private val uiErrorHandler: UiErrorHandler,
    private val sendMessageUseCase: SendMessageUseCase,
    private val getChatMessagesUseCase: GetChatMessagesUseCase,
): ViewModel() {

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    private val _sendEnabled = MutableStateFlow(false)
    val sendEnabled: StateFlow<Boolean> = _sendEnabled

    private val _messages = MutableStateFlow<List<Message>?>(null)
    val messages = _messages.filterNotNull().map {
        _messages.value = null
        it
    }

    private val _screenError = MutableStateFlow<String?>(null)
    val screenError = _screenError.filterNotNull().map {
        _screenError.value = null
        it
    }

    private val _dialogError = MutableStateFlow<String?>(null)
    val dialogError = _dialogError.filterNotNull().map {
        _dialogError.value = null
        it
    }

    init {
        updatesWebSocket.setUpdatesConnectionListener(object : UpdatesWebSocket.UpdatesConnectionListener {
            override fun onOpen() {
                _sendEnabled.value = true
            }

            override fun onNewUpdates(updates: List<Update>) {
                _messages.value = updates.mapNotNull { update -> update.message }
            }

            override fun onClosed(text: String) {
                _dialogError.value = text
                _sendEnabled.value = false
            }

        })

        updatesWebSocket.start()
    }

    fun getMessages(chat: Chat, start: Int? = null, batch: Int? = 20) {
        viewModelScope.launch {
            try {
                _messages.value = getChatMessagesUseCase.invoke(chat.id, start, batch)
            } catch (e: Exception) {
                e.printStackTrace()
                uiErrorHandler.proceedError(e) { error ->
                    _screenError.value = error
                }
            } finally {
                _loading.value = false
            }
        }
    }

    fun sendMessage(chat: Chat, text: String) {
        viewModelScope.launch {
            _sendEnabled.value = false

            try {
                sendMessageUseCase.invoke(chat.id, text)
            } catch (e: Exception) {
                e.printStackTrace()
                uiErrorHandler.proceedError(e) { error ->
                    _dialogError.value = error
                }
            } finally {
                _sendEnabled.value = true
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        updatesWebSocket.close()
    }
}