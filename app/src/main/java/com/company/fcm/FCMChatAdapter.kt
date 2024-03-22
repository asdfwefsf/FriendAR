package com.company.fcm

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.company.india1.UserItem
import com.company.india1.databinding.ItemFcmchatBinding

class FCMChatAdapter(

) : ListAdapter<FCMChatItem, FCMChatAdapter.ViewHolder>(differ) {

    var otherUserItem: UserItem? = null
    var myUserItem : UserItem? = null
    inner class ViewHolder(private val binding: ItemFcmchatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FCMChatItem) {
            if (item.myId != otherUserItem?.userUid) { // 친구가 카톡 보냈을 때
                binding.usernameTextView.isVisible = true
                binding.usernameTextView.text = myUserItem?.userName
                binding.usernameTextView.gravity = Gravity.START
                binding.messageTextView.text = item.message
                binding.messageTextView.gravity = Gravity.START
            } else { // 내가 카톡 보냈을 때
                binding.usernameTextView.isVisible = false
                binding.messageTextView.text = item.message
                binding.messageTextView.gravity = Gravity.END
            }
        }
//        binding.usernameTextView.isVisible = false
//        binding.messageTextView.text = item.message
//        binding.messageTextView.gravity = Gravity.END

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FCMChatAdapter.ViewHolder {
        return ViewHolder(
            ItemFcmchatBinding.inflate(
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
        val differ = object : DiffUtil.ItemCallback<FCMChatItem>() {
            override fun areItemsTheSame(oldItem: FCMChatItem, newItem: FCMChatItem): Boolean {
                return oldItem.friendId == newItem.friendId
            }

            override fun areContentsTheSame(oldItem: FCMChatItem, newItem: FCMChatItem): Boolean {
                return oldItem == newItem
            }

        }
    }
}