package com.example.simplechat.core.coreui.navigation

import android.os.Bundle
import com.example.simplechat.core.coreui.util.withArgs
import com.example.simplechat.screens.auth.presentation.LoginFragment
import com.example.simplechat.screens.auth.presentation.RegisterFragment
import com.example.simplechat.screens.chat.presentation.ChatFragment
import com.example.simplechat.screens.chats.domain.models.Chat
import com.example.simplechat.screens.chats.presentation.ChatsFragment
import com.example.simplechat.screens.profille.presentation.ProfileFragment
import com.github.terrakok.cicerone.androidx.FragmentScreen

object Screens {

    fun loginScreen() = FragmentScreen { LoginFragment() }

    fun registerScreen() = FragmentScreen { RegisterFragment() }

    fun chatsScreen() = FragmentScreen { ChatsFragment() }

    fun chatScreen(chat: Chat) = FragmentScreen { ChatFragment.newInstance(chat) }

    fun profileScreen() = FragmentScreen { ProfileFragment() }
}