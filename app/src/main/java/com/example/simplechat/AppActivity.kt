package com.example.simplechat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.simplechat.core.coreui.navigation.Screens
import com.example.simplechat.core.coreapi.common.preference.UserPreferenceStorage
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity : AppCompatActivity() {

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    @Inject
    lateinit var userPreferenceStorage: UserPreferenceStorage

    private val navigator = AppNavigator(this, R.id.container)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)

        if (userPreferenceStorage.token == null)
            router.newRootChain(Screens.loginScreen())
        else
            router.newRootChain(Screens.chatsScreen())
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }
}