package com.example.simplechat.screens.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplechat.core.coreui.error.UiErrorHandler
import com.example.simplechat.core.coreui.navigation.Screens
import com.example.simplechat.core.coreapi.common.preference.UserPreferenceStorage
import com.example.simplechat.screens.auth.domain.repository.AuthRepository
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
class AuthViewModel @Inject constructor(
    val router: Router,
    val userPreferenceStorage: UserPreferenceStorage,
    private val repository: AuthRepository,
    private val errorHandler: UiErrorHandler,
): ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _authError = MutableStateFlow<String?>(null)
    val authError = _authError.filterNotNull().map {
        _authError.value = null
        it
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loading.value = true

            try {
                val userIdentifiers = repository.login(username, password)
                userPreferenceStorage.id = userIdentifiers.id
                userPreferenceStorage.username = userIdentifiers.username
                userPreferenceStorage.token = userIdentifiers.token

                router.newRootChain(Screens.chatsScreen())
            } catch (e: Exception) {
                e.printStackTrace()
                errorHandler.proceedError(e) { error ->
                    _authError.value = error
                }
            } finally {
                _loading.value = false
            }
        }
    }

    fun register(username: String, password: String) {
        viewModelScope.launch {
            _loading.value = true

            try {
                val userIdentifiers = repository.register(username, password)
                userPreferenceStorage.id = userIdentifiers.id
                userPreferenceStorage.username = userIdentifiers.username
                userPreferenceStorage.token = userIdentifiers.token

                router.newRootChain(Screens.chatsScreen())
            } catch (e: Exception) {
                errorHandler.proceedError(e) { error ->
                    _authError.value = error
                }
            } finally {
                _loading.value = false
            }
        }
    }
}