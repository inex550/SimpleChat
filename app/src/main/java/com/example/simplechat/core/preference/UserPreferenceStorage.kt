package com.example.simplechat.core.preference

import android.content.SharedPreferences
import com.example.simplechat.utils.preference.StringPreference
import com.example.simplechat.screens.auth.domain.models.UserIdentifiers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferenceStorage @Inject constructor(
    prefs: SharedPreferences
) {

    var id: String? by StringPreference(prefs, PREF_ID, null)
    var username: String? by StringPreference(prefs, PREF_USERNAME, null)
    var token: String? by StringPreference(prefs, PREF_TOKEN, null)
    var avatar: String? by StringPreference(prefs, PREF_AVATAR, null)

    fun setupWithUserIdentifiersModel(userIdentifiers: UserIdentifiers) {
        id = userIdentifiers.id
        token = userIdentifiers.token
        username = userIdentifiers.username
        avatar = userIdentifiers.avatar
    }

    fun clearPrefs() {
        id = null
        username = null
        token = null
    }

    companion object {
        private const val PREF_ID = "PREF_ID"
        private const val PREF_USERNAME = "PREF_USERNAME"
        private const val PREF_TOKEN = "PREF_TOKEN"
        private const val PREF_AVATAR = "PREF_AVATAR"
    }
}