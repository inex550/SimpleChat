package com.example.simplechat.screens.profille.presentation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.text.Editable
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.loader.content.CursorLoader
import coil.load
import com.example.simplechat.R
import com.example.simplechat.core.network.di.NetworkModule
import com.example.simplechat.core.ui.base.BaseFragment
import com.example.simplechat.core.ui.dialog.ErrorDialog
import com.example.simplechat.core.ui.dialog.OkDialog
import com.example.simplechat.core.ui.extensions.launchWhenStarted
import com.example.simplechat.databinding.FragmentProfileBinding
import com.example.simplechat.screens.main.TabFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ProfileFragment: BaseFragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    private var avatarFilepath: String? = null

    private var usernameChanged = false
    private var avatarChanged = false

    private val imageResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data ?: let { return@registerForActivityResult }
                binding.avatarSiv.setImageURI(imageUri)

                avatarFilepath = getRealPathFromUri(imageUri)

                avatarChanged = true
                binding.saveBtn.isEnabled = true
            }
        }

    private val readStoragePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            val username = binding.usernameEt.text.toString()

            if (isGranted) {
                invokeChangeProfileData(username, avatarFilepath)
            } else {
                ErrorDialog(requireContext().getString(R.string.error_could_not_upload_avatar))
                    .show(parentFragmentManager, null)

                invokeChangeProfileData(username, null)
            }
        }

    override fun prepareUi() {
        _binding = FragmentProfileBinding.bind(requireView())

        binding.usernameEt.setText(viewModel.userPreferenceStorage.username)

        showAvatar(viewModel.userPreferenceStorage.avatar)

        setupProfileUi()
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

            if (usernameChanged && text == viewModel.userPreferenceStorage.username) {
                usernameChanged = false
                binding.saveBtn.isEnabled = false
            } else if (!usernameChanged && text != viewModel.userPreferenceStorage.username) {
                usernameChanged = true
                binding.saveBtn.isEnabled = true
            }
        }

        binding.saveBtn.setOnClickListener {
            val username = binding.usernameEt.text.toString()

            if (
                avatarChanged &&
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                OkDialog(requireContext().getString(R.string.warning_why_read_external_storage)) {
                    readStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }.show(parentFragmentManager, null)

                if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE))
                    invokeChangeProfileData(username, null)

                return@setOnClickListener
            }

            invokeChangeProfileData(username, avatarFilepath)
        }
    }

    private fun invokeChangeProfileData(username: String?, avatarFilepath: String?) {
        if ((!usernameChanged || username == null) && (!avatarChanged || avatarFilepath == null))
            return

        viewModel.changeProfileData(
            if (usernameChanged) username else null,
            if (avatarChanged) avatarFilepath else null
        )
    }

    override fun setupViewModel() {
        viewModel.loading.onEach { loading ->
            binding.usernameEt.isEnabled = !loading
            binding.avatarSiv.isClickable = !loading
        }.launchWhenStarted(lifecycleScope)

        viewModel.success.onEach {
            Toast.makeText(requireContext(), "Данные успешно изменены!", Toast.LENGTH_SHORT).show()

            usernameChanged = false
            avatarChanged = false

            (parentFragment as? TabFragment)?.updateTopPanelUsername()

            binding.saveBtn.isEnabled = false
        }.launchWhenStarted(lifecycleScope)

        viewModel.errorDialog.onEach { error ->
            ErrorDialog(error)
                .show(parentFragmentManager, null)
        }.launchWhenStarted(lifecycleScope)
    }

    private fun getRealPathFromUri(uri: Uri): String? {
        val cursor = CursorLoader(
            requireContext(),
            uri,
            arrayOf(MediaStore.Images.Media.DATA),
            null,
            null,
            null
        )
            .loadInBackground() ?: return null

        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

        if (cursor.moveToFirst()) {
            val path = cursor.getString(columnIndex)
            cursor.close()
            return path
        }

        cursor.close()
        return null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}