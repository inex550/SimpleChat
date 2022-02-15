package com.example.simplechat.screens.chats.presentation

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.simplechat.R
import com.example.simplechat.core.ui.base.BaseFragment
import com.example.simplechat.core.ui.dialog.ErrorDialog
import com.example.simplechat.core.ui.navigation.Screens
import com.example.simplechat.core.ui.extensions.launchWhenStarted
import com.example.simplechat.core.ui.extensions.makeVisible
import com.example.simplechat.databinding.FragmentChatsBinding
import com.example.simplechat.screens.chats.domain.models.Chat
import com.example.simplechat.screens.searchuser.presentation.SearchUserBottomSheetDialogFragment
import com.example.simplechat.services.updates.service.UpdatesService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ChatsFragment: BaseFragment(R.layout.fragment_chats) {

    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!

    val viewModel: ChatsViewModel by viewModels()

    private val chatsAdapter = ChatsAdapter(object: ChatsAdapter.Listener {
        override fun onChatsCountChanged(count: Int) {
            binding.noChatsLl.makeVisible(count == 0)
        }

        override fun onChatDeleted(chat: Chat) {
            viewModel.deleteChat(chat)
        }

        override fun onChatClicked(chat: Chat) {
            viewModel.baseRouter.navigateTo(Screens.chatScreen(chat))
        }
    })

    private val swipeToDeleteListener = object: SwipeToDeleteCallback.Listener {
        override fun onItemDeleteSwiped(position: Int) {
            val chat = chatsAdapter.getChat(position)
            viewModel.deleteChat(chat)
        }
    }

    private val onUserSelectedListener = SearchUserBottomSheetDialogFragment.OnUserSelectedListener { user ->
        viewModel.addChat(user.username)
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder) {
            val service = (binder as UpdatesService.UpdatesBinder).getService()
            viewModel.setService(service)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            viewModel.removeService()
        }
    }

    override fun prepareUi() {
        _binding = FragmentChatsBinding.bind(requireView())

        binding.chatsRv.adapter = chatsAdapter

        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(requireContext(), swipeToDeleteListener))
        itemTouchHelper.attachToRecyclerView(binding.chatsRv)

        binding.addChatBtn.setOnClickListener {
            SearchUserBottomSheetDialogFragment.newInstance(onUserSelectedListener)
                .show(requireActivity().supportFragmentManager, null)
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
            if (!viewModel.hasService)
                UpdatesService.bindService(serviceConnection)

            binding.chatsContentCl.makeVisible(true)
            chatsAdapter.setChats(chats)
        }.launchWhenStarted(lifecycleScope)

        viewModel.newChat.onEach { chat ->
            binding.noChatsLl.makeVisible(false)

            chatsAdapter.addChat(chat)
        }.launchWhenStarted(lifecycleScope)

        viewModel.removeChat.onEach { chat ->
            chatsAdapter.removeChat(chat)
        }.launchWhenStarted(lifecycleScope)

        viewModel.chatsError.onEach { error ->
            binding.errorContainerLl.makeVisible(true)
            binding.error.errorTv.text = error
        }.launchWhenStarted(lifecycleScope)

        viewModel.dialogError.onEach { error ->
            ErrorDialog(error)
                .show(parentFragmentManager, null)
        }.launchWhenStarted(lifecycleScope)
    }

    override fun onPause() {
        if (viewModel.hasService) {
            UpdatesService.unbindService(serviceConnection)
            viewModel.removeService()
        }
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}