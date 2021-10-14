package com.example.simplechat.screens.profille.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplechat.core.coreapi.common.preference.UserPreferenceStorage
import com.example.simplechat.core.coreui.error.UiErrorHandler
import com.example.simplechat.screens.profille.domain.repository.ProfileRepository
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val router: Router,
    val userPreferenceStorage: UserPreferenceStorage,
    private val uiErrorHandler: UiErrorHandler,
    private val profileRepository: ProfileRepository,
): ViewModel() {

    private val _loading = MutableStateFlow<Boolean?>(null)
    val loading = _loading.filterNotNull()

    private val _errorDialog = MutableStateFlow<String?>(null)
    val errorDialog = _errorDialog.filterNotNull().map {
        _errorDialog.value = null
        it
    }

    private val _usernameSuccessChanged = MutableStateFlow<String?>(null)
    val usernameSuccessChanged = _usernameSuccessChanged.filterNotNull().map {
        _usernameSuccessChanged.value = null
        it
    }

    fun changeProfileImage() {

    }

    fun changeProfileUsername(username: String) {
        viewModelScope.launch {
            _loading.value = true

            try {
                profileRepository.changeUsername(username)
                _usernameSuccessChanged.value = username
            } catch (e: Exception) {
                uiErrorHandler.proceedError(e) { error ->
                    _errorDialog.value = error
                }
            } finally {
                _loading.value = false
            }
        }
    }
}