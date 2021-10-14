package com.example.simplechat.core.coreapi.common.preference

import com.example.simplechat.screens.auth.domain.models.UserIdentifiers

interface UserPreferenceStorage {

    var id: String?
    var token: String?
    var username: String?
    var avatar: String?

    fun setupWithUserIdentifiersModel(userIdentifiers: UserIdentifiers)

    fun clearPrefs()
}