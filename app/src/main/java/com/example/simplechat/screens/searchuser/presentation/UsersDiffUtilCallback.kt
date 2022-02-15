package com.example.simplechat.screens.searchuser.presentation

import androidx.recyclerview.widget.DiffUtil
import com.example.simplechat.screens.chats.domain.models.User

class UsersDiffUtilCallback(
    private val oldList: List<User>,
    private val newList: List<User>
): DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldUser = oldList[oldItemPosition]
        val newUser = newList[newItemPosition]
        return oldUser.id == newUser.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldUser = oldList[oldItemPosition]
        val newUser = newList[newItemPosition]
        return oldUser.username == newUser.username && oldUser.avatar == newUser.avatar
    }
}