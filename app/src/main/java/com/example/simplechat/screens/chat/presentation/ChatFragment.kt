package com.example.simplechat.screens.chat.presentation

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.simplechat.R
import com.example.simplechat.core.coreui.base.BaseFragment
import com.example.simplechat.core.coreui.dialog.ErrorDialog
import com.example.simplechat.core.coreui.extensions.launchWhenStarted
import com.example.simplechat.core.coreui.extensions.makeVisible
import com.example.simplechat.core.coreui.extensions.withArgs
import com.example.simplechat.databinding.FragmentChatBinding
import com.example.simplechat.screens.chats.domain.models.Chat
import com.example.simplechat.services.updates.service.UpdatesService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach
import java.lang.IllegalArgumentException

@AndroidEntryPoint
class ChatFragment private constructor(): BaseFragment(R.layout.fragment_chat) {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val chat: Chat? by lazy(LazyThreadSafetyMode.NONE) {
        arguments?.getParcelable(ARG_CHAT) as? Chat
    }

    private val viewModel: ChatViewModel by viewModels()

    private val messagesAdapter = MessagesAdapter(
        onLoaderEnabled = { message ->
            viewModel.loadOldMessagesAt(chat!!, message.id-1)
        }
    )

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

        if (chat == null) {
            Toast.makeText(requireContext(), "Чат не был передан", Toast.LENGTH_SHORT).show()
            getRouter().exit()
            return
        }

        viewModel.setChat(chat!!)

        _binding = FragmentChatBinding.bind(requireView())

        viewModel.loadFirstBatch(chat!!)

        messagesAdapter.setCurrentUserId(viewModel.userPreferenceStorage.id?.toInt() ?: -1)
        binding.messagesRv.adapter = messagesAdapter
        messagesAdapter.addRecyclerScrollListener(binding.messagesRv)

        binding.chatNameTv.text = chat?.name

        binding.sendBtn.setOnClickListener {
            val messageText = binding.messageEt.text.toString()

            if (messageText.isEmpty()) return@setOnClickListener

            binding.messageEt.setText("")
            viewModel.sendMessage(chat!!, messageText)
        }

        binding.repeatBtn.setOnClickListener {
            viewModel.loadFirstBatch(chat!!)
        }

        binding.backIv.setOnClickListener {
            getRouter().exit()
        }
    }

    override fun setupViewModel() {

        viewModel.loading.onEach { loading ->
            if (loading) {
                binding.errorContainerLl.makeVisible(false)
                binding.chatContentCl.makeVisible(false)
            }

            binding.loadingPb.makeVisible(loading)
        }.launchWhenStarted(lifecycleScope)

        viewModel.sendEnabled.onEach { loading ->
            binding.sendBtn.isEnabled = loading
        }.launchWhenStarted(lifecycleScope)

        viewModel.atStartMessages.onEach { messages ->
            messagesAdapter.addMessagesAtStart(messages)
        }.launchWhenStarted(lifecycleScope)

        viewModel.atEndMessage.onEach { message ->
            messagesAdapter.addMessage(message)
            binding.messagesRv.scrollToPosition(messagesAdapter.itemCount - 1)
        }.launchWhenStarted(lifecycleScope)

        viewModel.messages.onEach { messages ->
            UpdatesService.bindService(requireActivity().application, serviceConnection)

            binding.chatContentCl.makeVisible(true)

            messagesAdapter.addMessages(messages)
            binding.messagesRv.scrollToPosition(messagesAdapter.itemCount - 1)
        }.launchWhenStarted(lifecycleScope)

        viewModel.removeAdapterLoader.onEach {
            messagesAdapter.removeLoader()
        }.launchWhenStarted(lifecycleScope)

        viewModel.screenError.onEach { error ->
            binding.errorContainerLl.makeVisible(true)
            binding.error.errorTv.text = error
        }.launchWhenStarted(lifecycleScope)

        viewModel.dialogError.onEach { error ->
            ErrorDialog(error)
                .show(parentFragmentManager, null)
        }.launchWhenStarted(lifecycleScope)
    }

    override fun onPause() {
        try {
            requireActivity().unbindService(serviceConnection)
        }
        catch (e: IllegalArgumentException) {}
        finally {
            viewModel.removeService()
        }

        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_CHAT = "chat"

        fun newInstance(chat: Chat): ChatFragment = ChatFragment().withArgs {
            putParcelable(ARG_CHAT, chat)
        }
    }
}