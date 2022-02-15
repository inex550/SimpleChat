package com.example.simplechat.screens.searchuser.presentation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.clear
import coil.load
import com.example.simplechat.R
import com.example.simplechat.utils.extensions.withBaseImageUrl
import com.example.simplechat.databinding.ItemUserBinding
import com.example.simplechat.screens.chats.domain.models.User

class UsersAdapter(
    private val listener: SearchUserBottomSheetDialogFragment.OnUserSelectedListener
): RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    private var users: List<User> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setUsers(newUsers: List<User>) {
        val usersDiffUtilCallback = UsersDiffUtilCallback(users, newUsers)
        val usersDiffResult = DiffUtil.calculateDiff(usersDiffUtilCallback)
        users = newUsers

        usersDiffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

    inner class ViewHolder(
        private val binding: ItemUserBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.usernameTv.text = user.username

            binding.avatarIv.clear()

            if (user.avatar == null)
                binding.avatarIv.setImageResource(R.drawable.ic_not_avatar)
            else
                binding.avatarIv.load(user.avatar.withBaseImageUrl())

            binding.root.setOnClickListener {
                listener.onUserSelected(user)
            }
        }
    }
}