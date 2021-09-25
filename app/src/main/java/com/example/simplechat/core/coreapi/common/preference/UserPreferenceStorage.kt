package com.example.simplechat.core.coreapi.common.preference

interface UserPreferenceStorage {

    var id: String?
    var username: String?
    var token: String?

    fun clearPrefs()
}