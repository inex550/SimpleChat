package com.example.simplechat.screens.searchuser.presentation

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.simplechat.R
import com.example.simplechat.core.ui.base.BaseBottomSheetDialogFragment
import com.example.simplechat.core.ui.extensions.launchWhenStarted
import com.example.simplechat.core.ui.extensions.makeVisible
import com.example.simplechat.core.ui.extensions.withArgs
import com.example.simplechat.databinding.FragmentSearchUserBinding
import com.example.simplechat.screens.chats.domain.models.User
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach
import java.io.Serializable

@AndroidEntryPoint
class SearchUserBottomSheetDialogFragment private constructor(): BaseBottomSheetDialogFragment(R.layout.fragment_search_user) {

    private var _binding: FragmentSearchUserBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchUserViewModel by viewModels()

    private val onUserSelectedListener: OnUserSelectedListener? by lazy {
        arguments?.getSerializable(ARG_USER_SELECTED_LISTENER) as? OnUserSelectedListener
    }

    private val usersAdapter: UsersAdapter = UsersAdapter { user ->
        onUserSelectedListener?.onUserSelected(user)
        dismiss()
    }

    override fun setupUi(view: View) {
        _binding = FragmentSearchUserBinding.bind(view)

        binding.usersRv.adapter = usersAdapter

        binding.usernameEt.addTextChangedListener { editable ->
            val username: String = editable.toString()
            viewModel.searchUsers(username)
        }
    }

    override fun setupViewModel() {
        viewModel.users.onEach { users ->
            binding.emptyUsersTv.isVisible = users.isEmpty()

            usersAdapter.setUsers(users)
        }.launchWhenStarted(lifecycleScope)

        viewModel.loading.onEach { loading ->
            if (loading) {
                binding.error.root.makeVisible(false)
                binding.emptyUsersTv.makeVisible(false)
            }

            binding.loadingPb.makeVisible(loading)
        }.launchWhenStarted(lifecycleScope)

        viewModel.error.onEach { error ->
            binding.error.root.makeVisible(true)
            binding.error.errorTv.text = error
        }.launchWhenStarted(lifecycleScope)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme).apply {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun interface OnUserSelectedListener: Serializable {
        fun onUserSelected(user: User)
    }

    companion object {

        private const val ARG_USER_SELECTED_LISTENER = "ARG_USER_SELECTED_LISTENER"

        fun newInstance(
            onUserSelectedListener: OnUserSelectedListener
        ): SearchUserBottomSheetDialogFragment = SearchUserBottomSheetDialogFragment().withArgs {
            putSerializable(ARG_USER_SELECTED_LISTENER, onUserSelectedListener)
        }
    }
}