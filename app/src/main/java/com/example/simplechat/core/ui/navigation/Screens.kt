package com.example.simplechat.core.ui.navigation

import com.example.simplechat.screens.auth.presentation.LoginFragment
import com.example.simplechat.screens.auth.presentation.RegisterFragment
import com.example.simplechat.screens.chat.presentation.ChatFragment
import com.example.simplechat.screens.chats.domain.models.Chat
import com.example.simplechat.screens.chats.presentation.ChatsFragment
import com.example.simplechat.screens.main.MainFragment
import com.example.simplechat.screens.main.TabFragment
import com.example.simplechat.screens.profille.presentation.ProfileFragment
import com.github.terrakok.cicerone.androidx.FragmentScreen

object Screens {

    fun loginScreen() = FragmentScreen { LoginFragment() }

    fun registerScreen() = FragmentScreen { RegisterFragment() }

    fun chatsScreen() = FragmentScreen { ChatsFragment() }

    fun chatScreen(chat: Chat) = FragmentScreen { ChatFragment.newInstance(chat) }

    fun profileScreen() = FragmentScreen { ProfileFragment() }

    fun mainScreen() = FragmentScreen { MainFragment() }

    fun tabScreen(tag: String) = FragmentScreen { TabFragment.newInstance(tag) }
}