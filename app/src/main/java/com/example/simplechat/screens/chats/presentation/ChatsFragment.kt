package com.example.simplechat.screens.chats.presentation

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.simplechat.AppActivity
import com.example.simplechat.R
import com.example.simplechat.core.coreui.base.BaseFragment
import com.example.simplechat.core.coreui.dialog.ErrorDialog
import com.example.simplechat.core.coreui.navigation.Screens
import com.example.simplechat.core.coreui.extensions.launchWhenStarted
import com.example.simplechat.core.coreui.extensions.makeVisible
import com.example.simplechat.databinding.FragmentChatsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ChatsFragment: BaseFragment(R.layout.fragment_chats) {

    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!

    val viewModel: ChatsViewModel by viewModels()

    private val chatsAdapter = ChatsAdapter(
        onChatClickListener = { chat ->
            viewModel.baseRouter.navigateTo(Screens.chatScreen(chat))
        },
        onDeleteChatClickListener = { chat ->
            viewModel.deleteChat(chat)
        }
    )

    override fun prepareUi() {
        _binding = FragmentChatsBinding.bind(requireView())

        binding.chatsRv.adapter = chatsAdapter

        chatsAdapter.setChatsCountChangedListener { count ->
            binding.noChatsLl.makeVisible(count == 0)
        }

        binding.addChatBtn.setOnClickListener {
            AddChatDialog(viewModel::addChat)
                .show(parentFragmentManager, null)
        }

        binding.repeatBtn.setOnClickListener {
            viewModel.loadChats()
        }
    }

    override fun setupViewModel() {
        viewModel.loading.onEach { loading ->
            if (loading) {
                binding.chatsContentCl.makeVisible(false)
                binding.errorContainerLl.makeVisible(false)
            }

            binding.loadingPb.makeVisible(loading)
        }.launchWhenStarted(lifecycleScope)

        viewModel.chats.onEach { chats ->
            binding.chatsContentCl.makeVisible(true)
            chatsAdapter.setChats(chats)

            binding.noChatsLl.makeVisible(chats.isEmpty())
        }.launchWhenStarted(lifecycleScope)

        viewModel.newChat.onEach { chat ->
            binding.noChatsLl.makeVisible(false)

            chatsAdapter.addChat(chat)
        }.launchWhenStarted(lifecycleScope)

        viewModel.removeChat.onEach { chat ->
            chatsAdapter.removeChat(chat)
            binding.noChatsLl.makeVisible(chatsAdapter.getChats().isEmpty())
        }.launchWhenStarted(lifecycleScope)

        viewModel.chatsError.onEach { error ->
            binding.errorContainerLl.makeVisible(true)
            binding.error.errorTv.text = error
        }.launchWhenStarted(lifecycleScope)

        viewModel.showError.onEach { error ->
            ErrorDialog(error)
                .show(parentFragmentManager, null)
        }.launchWhenStarted(lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}