package com.example.simplechat.screens.profille.presentation

import android.util.Log
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
import timber.log.Timber
import java.net.URI
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

    private val _success = MutableStateFlow<Boolean?>(null)
    val success = _success.filterNotNull().map {
        _success.value = null
        it
    }

    fun changeProfileData(username: String?, avatarFilepath: String?) {
        viewModelScope.launch {
            _loading.value = true

            try {
                if (avatarFilepath != null) {
                    val filename = profileRepository.uploadImageByURI(avatarFilepath).name
                    profileRepository.changeAvatar(filename)
                    userPreferenceStorage.avatar = filename
                }

                if (username != null) {
                    profileRepository.changeUsername(username)
                    userPreferenceStorage.username = username
                }

                _success.value = true
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