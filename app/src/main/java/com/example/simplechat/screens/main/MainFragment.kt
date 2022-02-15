package com.example.simplechat.screens.main

import android.os.Bundle
import com.example.simplechat.R
import com.example.simplechat.core.preference.UserPreferenceStorage
import com.example.simplechat.core.ui.base.BaseFragment
import com.example.simplechat.core.ui.navigation.Screens
import com.example.simplechat.databinding.FragmentMainBinding
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment: BaseFragment(R.layout.fragment_main) {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var baseRouter: Router

    @Inject
    lateinit var userPreferenceStorage: UserPreferenceStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (childFragmentManager.findFragmentById(R.id.main_container) == null)
            selectTab(TAG_CHATS)
    }

    override fun prepareUi() {
        _binding = FragmentMainBinding.bind(requireView())
        super.prepareUi()

        setupTopPanel()

        binding.navBnv.setOnItemSelectedListener { item ->
            val tag = when(item.itemId) {
                R.id.chats_item -> TAG_CHATS
                R.id.profile_item -> TAG_PROFILE
                else -> return@setOnItemSelectedListener false
            }

            selectTab(tag)

            true
        }
    }

    private fun selectTab(tag: String) {
        val fm = childFragmentManager

        val currentFragment = fm.fragments.firstOrNull { it.isVisible }
        val newFragment = fm.findFragmentByTag(tag)

        if (currentFragment != null && newFragment != null && currentFragment == newFragment)
            return

        val transaction = fm.beginTransaction()

        if (newFragment == null) {
            transaction.add(
                R.id.main_container,
                Screens.tabScreen(tag).createFragment(fm.fragmentFactory),
                tag
            )
        }

        if (newFragment != null)
            transaction.attach(newFragment)

        if (currentFragment != null)
            transaction.detach(currentFragment)

        transaction.commitNow()
    }

    private fun setupTopPanel() {
        binding.topPanel.titleUsernameTv.text = userPreferenceStorage.username

        binding.topPanel.exitToLoginIv.setOnClickListener {
            userPreferenceStorage.clearPrefs()
            baseRouter.newRootScreen(Screens.loginScreen())
        }
    }

    fun updateTopPanelUsername() {
        binding.topPanel.titleUsernameTv.text = userPreferenceStorage.username
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG_CHATS = "TAG_CHATS"
        const val TAG_PROFILE = "TAG_PROFILE"
    }
}