package com.example.githubuserinfo.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.githubuserinfo.data.model.GithubUserSummary
import com.example.githubuserinfo.databinding.ItemUserBinding

class UserListAdapter(
    private val onUserClicked: (GithubUserSummary) -> Unit
) : ListAdapter<GithubUserSummary, UserListAdapter.UserViewHolder>(UserDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemUserBinding.inflate(inflater, parent, false)
        return UserViewHolder(binding, onUserClicked)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    object UserDiffCallback : DiffUtil.ItemCallback<GithubUserSummary>() {
            override fun areItemsTheSame(oldItem: GithubUserSummary, newItem: GithubUserSummary): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: GithubUserSummary, newItem: GithubUserSummary): Boolean {
                return oldItem == newItem
            }
    }

    class UserViewHolder(
        private val binding: ItemUserBinding,
        private val onUserClicked: (GithubUserSummary) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private var currentUser: GithubUserSummary? = null

        fun bind(user: GithubUserSummary) {
            currentUser = user
            binding.usernameTextView.text = user.login
            binding.typeTextView.text = user.type ?: "User"
            binding.avatarImageView.load(user.avatarUrl) {
                crossfade(true)
            }

            binding.root.setOnClickListener {
                onUserClicked(user)
            }
        }
    }
}

