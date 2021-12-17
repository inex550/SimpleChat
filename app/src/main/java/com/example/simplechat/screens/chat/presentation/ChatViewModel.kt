package com.example.simplechat.screens.chat.presentation

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplechat.core.coreui.error.UiErrorHandler
import com.example.simplechat.core.coreapi.common.preference.UserPreferenceStorage
import com.example.simplechat.screens.chat.domain.models.Message
import com.example.simplechat.screens.chat.domain.usecase.GetChatMessagesUseCase
import com.example.simplechat.screens.chat.domain.usecase.SendMessageUseCase
import com.example.simplechat.screens.chats.domain.models.Chat
import com.example.simplechat.services.updates.models.Update
import com.example.simplechat.services.updates.models.UpdateNet
import com.example.simplechat.services.updates.service.UpdatesService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    val userPreferenceStorage: UserPreferenceStorage,
    private val uiErrorHandler: UiErrorHandler,
    private val sendMessageUseCase: SendMessageUseCase,
    private val getChatMessagesUseCase: GetChatMessagesUseCase,
): ViewModel() {

    @SuppressLint("StaticFieldLeak")
    private var service: UpdatesService? = null

    fun setService(service: UpdatesService) {
        this.service = service
        service.setListener(updatesListener)
    }

    fun removeService() {
        service?.removeListener()
        service = null

        _sendEnabled.value = false
    }

    private val updatesListener = object : UpdatesService.UpdateListener {
        override fun onOpen() {
            _sendEnabled.value = true
        }

        override fun onUpdate(update: Update): Boolean {
            if (update.message == null) return false
            if (update.message.chatId != chat.id) return false

            _atEndMessage.value = update.message

            return true
        }

        override fun onClosed(text: String) {
            _sendEnabled.value = false
            _dialogError.value = text
        }
    }

    private lateinit var chat: Chat

    fun setChat(chat: Chat) {
        this.chat = chat
    }

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    private val _sendEnabled = MutableStateFlow(false)
    val sendEnabled: StateFlow<Boolean> = _sendEnabled

    private val _messages = MutableStateFlow<ArrayList<Message>?>(null)
    val messages: Flow<List<Message>> = _messages.filterNotNull().map {
        it
    }

    private val _atStartMessages = MutableStateFlow<List<Message>?>(null)
    val atStartMessages = _atStartMessages.filterNotNull().map {
        _messages.value?.addAll(0, it)
        _atStartMessages.value = null
        it
    }

    private val _atEndMessage = MutableStateFlow<Message?>(null)
    val atEndMessage = _atEndMessage.filterNotNull().map {
        _messages.value?.add(it)
        _atEndMessage.value = null
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

    private val _removeAdapterLoader = MutableStateFlow<Boolean?>(null)
    val removeAdapterLoader = _removeAdapterLoader.filterNotNull().map {
        _removeAdapterLoader.value = null
        it
    }

    fun loadFirstBatch(chat: Chat, batch: Int = 20) {
        viewModelScope.launch {
            _loading.value = true

            try {
                val messages = getChatMessagesUseCase.invoke(chat.id, null, batch)
                _messages.value = ArrayList(messages)
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

    fun loadOldMessagesAt(chat: Chat, start: Int? = null, batch: Int? = 20) {
        viewModelScope.launch {
            try {
                delay(1000)
                _atStartMessages.value = getChatMessagesUseCase.invoke(chat.id, start, batch)
            } catch (e: Exception) {
                e.printStackTrace()
                uiErrorHandler.proceedError(e) { error ->
                    _screenError.value = error
                }
            } finally {
                _removeAdapterLoader.value = true
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
}