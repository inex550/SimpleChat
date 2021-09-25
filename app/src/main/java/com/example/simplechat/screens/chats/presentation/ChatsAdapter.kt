package com.example.simplechat.screens.chats.presentation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simplechat.R
import com.example.simplechat.databinding.ItemChatBinding
import com.example.simplechat.screens.chats.domain.models.Chat

typealias ChatsCountChangedListener = (Int) -> Unit

typealias OnDeleteChatClickListener = (Chat) -> Unit

typealias OnChatClickListener = (Chat) -> Unit

class ChatsAdapter(
    private val onChatClickListener: OnChatClickListener,
    private val onDeleteChatClickListener: OnDeleteChatClickListener,
): RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {

    private val chats: ArrayList<Chat> = arrayListOf()

    fun getChats(): List<Chat> = chats

    @SuppressLint("NotifyDataSetChanged")
    fun setChats(items: List<Chat>) {
        chats.apply {
            clear()
            addAll(items)

            chatsCountChangedListener?.invoke(chats.size)
        }
        notifyDataSetChanged()
    }

    fun addChat(chat: Chat) {
        chats.add(chat)
        notifyItemInserted(chats.lastIndex)

        chatsCountChangedListener?.invoke(chats.size)
    }

    fun removeChat(chat: Chat) {
        val chatIndex = chats.indexOf(chat)

        chats.removeAt(chatIndex)
        notifyItemRemoved(chatIndex)

        chatsCountChangedListener?.invoke(chats.size)
    }

    private var chatsCountChangedListener: ChatsCountChangedListener? = null

    fun setChatsCountChangedListener(listener: ChatsCountChangedListener) {
        chatsCountChangedListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(chats[position])
    }

    override fun getItemCount(): Int = chats.size

    inner class ViewHolder(
        private val binding: ItemChatBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: Chat) {

            binding.chatNameTv.text = chat.name

            binding.chatTypeIconIv.setImageResource(
                when {
                    chat.user != null -> R.drawable.ic_person
                    chat.users != null -> R.drawable.ic_group
                    else -> 0
                }
            )

            binding.removeChatBtn.setOnClickListener {
                onDeleteChatClickListener(chat)
            }

            binding.containerCl.setOnClickListener {
                onChatClickListener(chat)
            }
        }
    }
}