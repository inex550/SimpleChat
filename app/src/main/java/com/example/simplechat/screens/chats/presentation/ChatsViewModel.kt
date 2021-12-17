package com.example.simplechat.screens.chats.presentation

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplechat.core.coreui.error.UiErrorHandler
import com.example.simplechat.core.coreapi.common.preference.UserPreferenceStorage
import com.example.simplechat.screens.chats.domain.models.Chat
import com.example.simplechat.screens.chats.domain.usecase.CreatePrivateChatUseCase
import com.example.simplechat.screens.chats.domain.usecase.DeleteChatUseCase
import com.example.simplechat.screens.chats.domain.usecase.GetMyChatsUseCase
import com.example.simplechat.services.updates.models.Update
import com.example.simplechat.services.updates.models.UpdateNet
import com.example.simplechat.services.updates.models.UpdateType
import com.example.simplechat.services.updates.service.UpdatesService
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
class ChatsViewModel @Inject constructor (
    val baseRouter: Router,
    val userPreferenceStorage: UserPreferenceStorage,
    private val getMyChatsUseCase: GetMyChatsUseCase,
    private val createPrivateChatUseCase: CreatePrivateChatUseCase,
    private val deleteChatUseCase: DeleteChatUseCase,
    private val uiErrorHandler: UiErrorHandler
): ViewModel() {

    @SuppressLint("StaticFieldLeak")
    private var service: UpdatesService? = null

    val hasService get() = service != null

    fun setService(service: UpdatesService) {
        this.service = service
        service.setListener(updatesListener)
    }

    fun removeService() {
        service?.removeListener()
        service = null
    }

    private val updatesListener = object : UpdatesService.UpdateListener {
        override fun onOpen() {}

        override fun onUpdate(update: Update): Boolean {
            if (update.chat == null) return false

            if (update.type == UpdateType.NEW)
                _newChat.value = update.chat

            else if (update.type == UpdateType.REMOVE)
                _removeChat.value = update.chat

            return true
        }

        override fun onClosed(text: String) {
            _dialogError.value = text
        }
    }

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    private val _chats = MutableStateFlow<ArrayList<Chat>?>(null)
    val chats = _chats.filterNotNull().map {
        _chats.value as List<Chat>
    }

    private val _newChat = MutableStateFlow<Chat?>(null)
    val newChat = _newChat.filterNotNull().map {
        _newChat.value = null
        _chats.value?.add(it)
        it
    }

    private val _removeChat = MutableStateFlow<Chat?>(null)
    val removeChat = _removeChat.filterNotNull().map {
        _removeChat.value = null
        _chats.value?.remove(it)
        it
    }

    private val _chatsError = MutableStateFlow<String?>(null)
    val chatsError = _chatsError.filterNotNull().map {
        _chatsError.value = null
        it
    }

    private val _dialogError = MutableStateFlow<String?>(null)
    val dialogError = _dialogError.filterNotNull().map {
        _dialogError.value = null
        it
    }

    init {
        loadChats()
    }

    fun loadChats() {
        viewModelScope.launch {
            _loading.value = true

            try {
                val chats = getMyChatsUseCase.invoke()

                _chats.value = ArrayList(chats)
            } catch (e: Exception) {
                uiErrorHandler.proceedError(e) { error ->
                    _chatsError.value = error
                }
            } finally {
                _loading.value = false
            }
        }
    }

    fun addChat(username: String) {
        viewModelScope.launch {
            try {
                createPrivateChatUseCase.invoke(username)
            } catch (e: Exception) {
                uiErrorHandler.proceedError(e) { error ->
                    _dialogError.value = error
                }
            }
        }
    }

    fun deleteChat(chat: Chat) {
        viewModelScope.launch {
            try {
                deleteChatUseCase.invoke(chat.id)
            } catch (e: Exception) {
                uiErrorHandler.proceedError(e) { error ->
                    _dialogError.value = error
                }
            }
        }
    }
}