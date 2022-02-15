package com.example.simplechat.screens.main

import android.os.Bundle
import com.example.simplechat.R
import com.example.simplechat.core.ui.base.BaseFragment
import com.example.simplechat.core.ui.extensions.withArgs
import com.example.simplechat.core.ui.navigation.Screens
import com.example.simplechat.core.ui.navigation.subnavigation.CiceroneHolder
import com.example.simplechat.core.ui.navigation.subnavigation.RouterProvider
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TabFragment: BaseFragment(R.layout.fragment_tab), RouterProvider {

    @Inject
    lateinit var ciceroneHolder: CiceroneHolder

    private val screenTag: String by lazy {
        arguments?.getString(ARG_TAG).orEmpty()
    }

    private val cicerone: Cicerone<Router> by lazy {
        ciceroneHolder.getCicerone(screenTag)
    }

    private val localRouter get() = cicerone.router

    private val navigatorHolder get() = cicerone.getNavigatorHolder()

    private val navigator get() = AppNavigator(requireActivity(), R.id.tab_container, childFragmentManager)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (childFragmentManager.findFragmentById(R.id.tab_container) == null) {
            val screen = when(screenTag) {
                MainFragment.TAG_CHATS -> Screens.chatsScreen()
                MainFragment.TAG_PROFILE -> Screens.profileScreen()
                else -> throw IllegalStateException("Not fount fragment with tag $tag")
            }

            localRouter.newRootScreen(screen)
        }
    }

    override fun onResume() {
        super.onResume()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.setNavigator(navigator)
        super.onPause()
    }

    fun updateTopPanelUsername() {
        (parentFragment as? MainFragment)?.updateTopPanelUsername()
    }

    companion object {
        private const val ARG_TAG = "ARG_TAG"

        fun newInstance(tag: String): TabFragment = TabFragment().withArgs {
            putString(ARG_TAG, tag)
        }
    }

    override fun provideRouter(): Router = localRouter
}