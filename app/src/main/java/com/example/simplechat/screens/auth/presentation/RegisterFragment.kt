package com.example.simplechat.screens.auth.presentation

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.simplechat.AppActivity
import com.example.simplechat.R
import com.example.simplechat.core.coreui.base.BaseFragment
import com.example.simplechat.core.coreui.dialog.ErrorDialog
import com.example.simplechat.core.coreui.navigation.Screens
import com.example.simplechat.core.coreui.extensions.launchWhenStarted
import com.example.simplechat.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class RegisterFragment: BaseFragment(R.layout.fragment_register) {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()

    override fun prepareUi() {
        _binding = FragmentRegisterBinding.bind(requireView())

        binding.registerBtn.setOnClickListener {
            val username = binding.usernameEt.text.toString()
            val password = binding.passwordEt.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                ErrorDialog(requireContext().getString(R.string.error_field_empty))
                    .show(parentFragmentManager, null)

                return@setOnClickListener
            }

            viewModel.register(username, password)
        }

        binding.toLoginTv.setOnClickListener {
            getRouter().newRootChain(Screens.loginScreen())
        }
    }

    override fun setupViewModel() {
        viewModel.success.onEach {
            getRouter().newRootScreen(Screens.mainScreen())
        }.launchWhenStarted(lifecycleScope)

        viewModel.loading.onEach { loading ->
            binding.registerBtn.isEnabled = !loading
        }.launchWhenStarted(lifecycleScope)

        viewModel.authError.onEach { error ->
            ErrorDialog(error)
                .show(parentFragmentManager, null)
        }.launchWhenStarted(lifecycleScope)
    }
}