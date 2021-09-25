package com.example.simplechat.core.coreimpl.common.preference

import android.content.Context
import android.content.SharedPreferences
import com.example.simplechat.R
import com.example.simplechat.core.coreimpl.common.data.StringPreference
import com.example.simplechat.core.coreapi.common.preference.UserPreferenceStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferenceStorageImpl @Inject constructor(
    prefs: SharedPreferences
): UserPreferenceStorage {

    override var id: String? by StringPreference(prefs, PREF_ID, null)
    override var username: String? by StringPreference(prefs, PREF_USERNAME, null)
    override var token: String? by StringPreference(prefs, PREF_TOKEN, null)

    override fun clearPrefs() {
        id = null
        username = null
        token = null
    }

    companion object {
        private const val PREF_ID = "PREF_ID"
        private const val PREF_USERNAME = "PREF_USERNAME"
        private const val PREF_TOKEN = "PREF_TOKEN"
    }
}