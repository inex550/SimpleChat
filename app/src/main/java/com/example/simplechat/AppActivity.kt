package com.example.simplechat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.simplechat.core.coreui.navigation.Screens
import com.example.simplechat.core.coreapi.common.preference.UserPreferenceStorage
import com.example.simplechat.core.coreui.util.makeVisible
import com.example.simplechat.databinding.ActivityAppBinding
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

    lateinit var binding: ActivityAppBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupScreensBtv()

        if (userPreferenceStorage.token == null)
            router.newRootChain(Screens.loginScreen())
        else
            router.newRootChain(Screens.chatsScreen())
    }

    private fun setupScreensBtv() {
        binding.selectScreensBnv.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.chats_item -> {
                    router.newRootChain(Screens.chatsScreen())
                    true
                }
                R.id.profile_item -> {
                    router.newRootChain(Screens.profileScreen())
                    true
                }
                else -> false
            }
        }
    }

    fun showBottomNavigation() {
        binding.borderView.makeVisible(true)
        binding.selectScreensBnv.makeVisible(true)
    }
    fun hideBottomNavigation() {
        binding.borderView.makeVisible(false)
        binding.selectScreensBnv.makeVisible(false)
    }

    fun checkFirstBnvItem() {
        binding.selectScreensBnv.selectedItemId = R.id.chats_item
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