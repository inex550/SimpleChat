package com.example.simplechat.screens.searchuser.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplechat.core.preference.UserPreferenceStorage
import com.example.simplechat.core.ui.error.UiErrorHandler
import com.example.simplechat.screens.chats.domain.models.User
import com.example.simplechat.screens.searchuser.domain.repository.SearchUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchUserViewModel @Inject constructor(
    private val repository: SearchUserRepository,
    private val errorHandler: UiErrorHandler,
): ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error: MutableStateFlow<String?> = MutableStateFlow(null)
    val error = _error.filterNotNull().map {
        _error.value = null
        it
    }

    private val _users: MutableStateFlow<List<User>?> = MutableStateFlow(null)
    val users = _users.filterNotNull().map {
        _users.value = null
        it
    }

    private var searchJob: Job? = null

    fun searchUsers(query: String) {
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            _loading.value = true

            delay(500)

            try {
                _users.value = repository.searchUsers(query)
            } catch (e: Exception) {
                e.printStackTrace()
                errorHandler.proceedError(e) { error ->
                    _error.value = error
                }
            } finally {
                _loading.value = false
            }
        }
    }
}