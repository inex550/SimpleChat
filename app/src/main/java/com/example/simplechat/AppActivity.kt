package com.example.simplechat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.simplechat.core.ui.navigation.Screens
import com.example.simplechat.core.preference.UserPreferenceStorage
import com.example.simplechat.core.ui.navigation.subnavigation.RouterProvider
import com.example.simplechat.databinding.ActivityAppBinding
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity : AppCompatActivity(), RouterProvider {

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    private val navigator = AppNavigator(this, R.id.container, supportFragmentManager)

    @Inject
    lateinit var userPreferenceStorage: UserPreferenceStorage

    lateinit var binding: ActivityAppBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (userPreferenceStorage.token == null)
            router.newRootScreen(Screens.loginScreen())
        else
            router.newRootScreen(Screens.mainScreen())
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    override fun provideRouter(): Router = router
}