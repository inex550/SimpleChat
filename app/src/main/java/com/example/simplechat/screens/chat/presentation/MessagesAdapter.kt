package com.example.simplechat.screens.chat.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.simplechat.R
import com.example.simplechat.core.coreimpl.network.di.NetworkModule
import com.example.simplechat.screens.chat.domain.models.Message

typealias OnLoaderEnabled = (first: Message) -> Unit

class MessagesAdapter(
    private val onLoaderEnabled: OnLoaderEnabled
): RecyclerView.Adapter<MessagesAdapter.BaseViewHolder>() {

    var isLoaderEnabled: Boolean = false
        private set

    private var currentUserId: Int? = null

    fun setCurrentUserId(userId: Int) {
        currentUserId = userId
    }

    private val messages: ArrayList<Message?> = arrayListOf()

    fun addMessages(items: List<Message>) {
        val startIndex = messages.size

        messages.addAll(items)
        notifyItemRangeInserted(startIndex, items.size)
    }

    fun addMessagesAtStart(items: List<Message>) {
        val startIndex = if (isLoaderEnabled) 1 else 0

        messages.addAll(startIndex, items)
        notifyItemRangeInserted(startIndex, items.size)
    }

    fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.lastIndex)
    }

    override fun getItemViewType(position: Int): Int =
        if (isLoaderEnabled && position == 0)
            LOADER_VIEW_TYPE
        else if (messages[position]?.senderId == currentUserId)
            MY_MESSAGE_VIEW_TYPE
        else
            USER_MESSAGE_VIEW_TYPE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        if (viewType == LOADER_VIEW_TYPE)
            LoaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false))
        else
            ViewHolder(LayoutInflater.from(parent.context).inflate(when (viewType) {
                MY_MESSAGE_VIEW_TYPE -> R.layout.item_my_message
                USER_MESSAGE_VIEW_TYPE -> R.layout.item_user_message
                else -> throw NoSuchElementException("No such itemViewType for \"$viewType\"")
            }, parent, false))

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (isLoaderEnabled && position == 0) return

        val message = messages[position - if (isLoaderEnabled) 1 else 0] ?: let { return }
        holder.bind(message)
    }

    override fun getItemCount(): Int = messages.size

    open class BaseViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        open fun bind(message: Message) {}
    }

    fun addRecyclerScrollListener(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager

        recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val position = layoutManager.findFirstVisibleItemPosition()
                if (!isLoaderEnabled && dy < 0 && position == 0)
                    addLoader()
            }
        })
    }

    fun addLoader() {
        if (isLoaderEnabled) return

        messages.add(0, null)
        isLoaderEnabled = true
        notifyItemInserted(0)

        onLoaderEnabled(messages[1]!!)
    }

    fun removeLoader() {
        if (!isLoaderEnabled) return

        messages.removeAt(0)
        isLoaderEnabled = false
        notifyItemRemoved(0)
    }

    class LoaderViewHolder(itemView: View): BaseViewHolder(itemView)

    class ViewHolder(itemView: View): BaseViewHolder(itemView) {

        private val messageTextTv: TextView = itemView.findViewById(R.id.message_text_tv)
        private val usernameTv: TextView = itemView.findViewById(R.id.username_tv)
        private val avatarIv: ImageView = itemView.findViewById(R.id.avatar_siv)

        override fun bind(message: Message) {
            messageTextTv.text = message.text
            usernameTv.text = message.sender.username

            if (message.sender.avatar == null)
                avatarIv.setImageResource(R.drawable.ic_not_avatar)
            else
                avatarIv.load(NetworkModule.BASE_IMAGE_URL + message.sender.avatar)
        }
    }

    companion object {
        private const val MY_MESSAGE_VIEW_TYPE = 1
        private const val USER_MESSAGE_VIEW_TYPE = 2
        private const val LOADER_VIEW_TYPE = 3
    }
}