package com.example.simplechat.screens.profille.presentation

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.simplechat.AppActivity
import com.example.simplechat.R
import com.example.simplechat.core.coreimpl.network.di.NetworkModule
import com.example.simplechat.core.coreui.base.BaseFragment
import com.example.simplechat.core.coreui.dialog.ErrorDialog
import com.example.simplechat.core.coreui.navigation.Screens
import com.example.simplechat.core.coreui.util.launchWhenStarted
import com.example.simplechat.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ProfileFragment: BaseFragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    private var usernameChanged = false
    private var avatarChanged = false

    override fun prepareUi() {

        (activity as? AppActivity)?.showBottomNavigation()

        _binding = FragmentProfileBinding.bind(requireView())

        showUsername(viewModel.userPreferenceStorage.username.orEmpty())

        showAvatar(viewModel.userPreferenceStorage.avatar)

        binding.topPanel.exitToLoginIv.setOnClickListener {
            viewModel.userPreferenceStorage.clearPrefs()
            viewModel.router.newRootChain(Screens.loginScreen())
        }

        setupProfileUi()
    }

    private fun showUsername(username: String) {
        binding.usernameEt.setText(username)
        binding.topPanel.titleUsernameTv.text = username
    }

    private fun showAvatar(avatar: String?) {
        if (avatar != null)
            binding.avatarSiv.load(NetworkModule.BASE_IMAGE_URL + avatar)
    }

    private fun setupProfileUi() {
        binding.avatarSiv.setOnClickListener {
            val chooseImageIntent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }

            imageResultLauncher.launch(Intent.createChooser(chooseImageIntent, "Choose Picture"))
        }

        binding.usernameEt.addTextChangedListener { editable: Editable? ->
            val text = editable?.toString() ?: let { return@addTextChangedListener }

            if (avatarChanged) return@addTextChangedListener

            if (usernameChanged && text == viewModel.userPreferenceStorage.username) {
                usernameChanged = false
                binding.saveBtn.isEnabled = false
            }

            else if (!usernameChanged && text != viewModel.userPreferenceStorage.username) {
                usernameChanged = true
                binding.saveBtn.isEnabled = true
            }
        }

        binding.saveBtn.setOnClickListener {
            if (usernameChanged)
                viewModel.changeProfileUsername(binding.usernameEt.text.toString())
        }
    }

    override fun setupViewModel() {
        viewModel.loading.onEach { loading ->
            binding.saveBtn.isEnabled = !loading
            binding.avatarSiv.isClickable = !loading

            if (usernameChanged || avatarChanged)
                binding.saveBtn.isEnabled = !loading
        }.launchWhenStarted(lifecycleScope)

        viewModel.usernameSuccessChanged.onEach { username ->
            Toast.makeText(requireContext(), "Данные успешно изменены!", Toast.LENGTH_SHORT).show()

            usernameChanged = false

            viewModel.userPreferenceStorage.username = username
            showUsername(username)
        }.launchWhenStarted(lifecycleScope)

        viewModel.errorDialog.onEach { error ->
            ErrorDialog(error)
                .show(parentFragmentManager, null)
        }.launchWhenStarted(lifecycleScope)
    }

    private val imageResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data ?: let { return@registerForActivityResult }
            binding.avatarSiv.setImageURI(imageUri)

            avatarChanged = true
            binding.saveBtn.isEnabled = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}