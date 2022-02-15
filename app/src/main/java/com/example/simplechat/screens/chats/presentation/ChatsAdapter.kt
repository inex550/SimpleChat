package com.example.simplechat.screens.chats.presentation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.simplechat.R
import com.example.simplechat.core.network.di.NetworkModule
import com.example.simplechat.databinding.ItemChatBinding
import com.example.simplechat.screens.chats.domain.models.Chat

class ChatsAdapter(
    private val listener: Listener
): RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {

    private val chats: ArrayList<Chat> = arrayListOf()

    fun getChat(position: Int): Chat =
        chats[position]

    @SuppressLint("NotifyDataSetChanged")
    fun setChats(items: List<Chat>) {
        chats.apply {
            clear()
            addAll(items)

            listener.onChatsCountChanged(chats.size)
        }
        notifyDataSetChanged()
    }

    fun addChat(chat: Chat) {
        chats.add(chat)
        notifyItemInserted(chats.lastIndex)

        listener.onChatsCountChanged(chats.size)
    }

    fun removeChat(chat: Chat) {
        val chatIndex = chats.indexOf(chat)

        chats.removeAt(chatIndex)
        notifyItemRemoved(chatIndex)

        listener.onChatsCountChanged(chats.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(chats[position])
    }

    override fun getItemCount(): Int = chats.size

    interface Listener {
        fun onChatsCountChanged(count: Int)

        fun onChatDeleted(chat: Chat)

        fun onChatClicked(chat: Chat)
    }

    inner class ViewHolder(
        private val binding: ItemChatBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: Chat) {

            binding.chatNameTv.text = chat.name

            binding.chatTypeIconIv.setImageResource(
                when {
                    chat.user != null -> {
                        binding.chatIconIv.setImageResource(R.drawable.ic_not_avatar)
                        R.drawable.ic_person
                    }
                    chat.users != null -> R.drawable.ic_group
                    else -> 0
                }
            )

            chat.avatar?.let {
                binding.chatIconIv.load(NetworkModule.BASE_IMAGE_URL + chat.avatar)
            }

            if (chat.avatar != null)
                binding.chatIconIv.load(NetworkModule.BASE_IMAGE_URL + chat.avatar)

            binding.containerCl.setOnClickListener {
                listener.onChatClicked(chat)
            }
        }
    }
}