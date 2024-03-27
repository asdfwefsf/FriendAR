package com.company.chat

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.company.india1.UserItem
import com.company.india1.databinding.ItemChatBinding

class ChatAdapter(
//    private val friendId : String
) : ListAdapter<ChatItem, ChatAdapter.ViewHolder>(differ) {

    var otherUserItem: UserItem? = null

    inner class ViewHolder(private val binding : ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item : ChatItem) {
            if (item.myId == otherUserItem?.userUid) { // 친구가 카톡 보냈을 때
                binding.usernameTextView.isVisible = true
                binding.usernameTextView.text = otherUserItem?.userName
                binding.usernameTextView.gravity = Gravity.START
                binding.messageTextView.text = item.message
                binding.messageTextView.gravity = Gravity.START
            } else { // 내가 카톡 보냈을 때
                binding.usernameTextView.isVisible = false
                binding.messageTextView.text = item.message
                binding.messageTextView.gravity = Gravity.END
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ViewHolder {
        return ViewHolder(
            ItemChatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val differ = object : DiffUtil.ItemCallback<ChatItem>() {
            override fun areItemsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
                return oldItem.friendId == newItem.friendId
            }

            override fun areContentsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
                return oldItem == newItem
            }

        }
    }
}