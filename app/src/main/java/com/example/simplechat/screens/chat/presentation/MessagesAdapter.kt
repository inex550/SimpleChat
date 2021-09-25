package com.example.simplechat.screens.chat.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplechat.R
import com.example.simplechat.screens.chat.domain.models.Message

class MessagesAdapter: RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    private var currentUserId: Int? = null

    fun setCurrentUserId(userId: Int) {
        currentUserId = userId
    }

    private val messages: ArrayList<Message> = arrayListOf()

    fun addMessages(items: List<Message>) {
        val insertStartIndex = messages.size

        messages.addAll(items)
        notifyItemRangeInserted(insertStartIndex, items.size)
    }

    fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.lastIndex)
    }

    override fun getItemViewType(position: Int): Int = when(messages[position].senderId) {
        currentUserId -> MY_MESSAGE_VIEW_TYPE
        else -> USER_MESSAGE_VIEW_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(when (viewType) {
            MY_MESSAGE_VIEW_TYPE -> R.layout.item_my_message
            USER_MESSAGE_VIEW_TYPE -> R.layout.item_user_message
            else -> throw NoSuchElementException("No such itemViewType for \"$viewType\"")
        }, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val messageTextTv: TextView = itemView.findViewById(R.id.message_text_tv)

        fun bind(message: Message) {
            messageTextTv.text = message.text
        }
    }

    companion object {
        private const val MY_MESSAGE_VIEW_TYPE = 1
        private const val USER_MESSAGE_VIEW_TYPE = 2
    }
}