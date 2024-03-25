package com.company.Friend

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.company.india1.Key
import com.company.india1.R
import com.company.india1.databinding.ItemFriendBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.UUID

class FriendAdapter(
    private var goToFriendHomeListener: GoToFriendHomeListener,
    private val goToFriendChatListener : GoToFriendChatListener,
) : ListAdapter<FriendItem, FriendAdapter.ViewHolder>(differ) {
    // UserAdapter에서 item_user.xml을 클릭을 했을 때 가져온 User Id로 Chatting을 만들어 줄 수 있어.
    // 따라서 UserAdapter의 생성자에 ClickListener를 하나 만들어 준다. : onClick을 활용해서 User Id를 가져 올 거야.
    private val currentUser = Firebase.auth.uid
    private var otherUserUid : String? = null
    private var nanSu = ""

    inner class ViewHolder(private val binding: ItemFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FriendItem) {
            binding.nicknameTextView.text = item.friendId
            binding.descriptionTextView.text = item.friendState
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemFriendBinding.inflate( // inflate()의 Parameter 3가지 : context , ViewGroup , attachToParent
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
        holder.itemView.findViewById<Button>(R.id.goToFriendHomeButton).setOnClickListener {
            goToFriendHomeListener.GoToFriendHome()
        }
        holder.itemView.findViewById<Button>(R.id.goToFriendChatButton).setOnClickListener {
            val otherUserEmail = holder.itemView.findViewById<TextView>(R.id.nicknameTextView).text.toString()
            searchUserUidByEmail(otherUserEmail) {
                val ChatRoomsDb = Firebase.database.reference.child(Key.DB_CHAT_ROOMS).child(currentUser!!).child(it)
                otherUserUid = it
                Log.d("FriendAdapter", "1: $otherUserUid")


                ChatRoomsDb.get().addOnSuccessListener {
                    if (it.value != null) { // 기존 채팅방이 존재해
                        val chatroom = it.getValue(ChatRoomsItem::class.java)
                        nanSu = chatroom?.nanSu!!
                        Log.d("FriendAdapter", "2: $nanSu")

                        goToFriendChatListener.GoToFriendChat(otherUserEmail , otherUserUid!! , nanSu)
                    } else{ // 기존 채팅방이 없어 : 새로 만들어줘야해
                        val chatRooms = mutableMapOf<String , Any>()
                        nanSu = UUID.randomUUID().toString()
                        chatRooms["nanSu"] = nanSu
                        chatRooms["lastMessage"] = "최근 메시지"
                        chatRooms["otherUserEmail"] = otherUserEmail
                        chatRooms["otherUserUid"] = otherUserUid!!
                        Firebase.database.reference.child(Key.DB_CHAT_ROOMS).child(currentUser).child(otherUserUid.toString())
                            .updateChildren(chatRooms)
                        goToFriendChatListener.GoToFriendChat(otherUserEmail , otherUserUid!! , nanSu)
                    }
                }
            }

        }
    }


    companion object {
        val differ = object : DiffUtil.ItemCallback<FriendItem>() { // Interface니까 object로 구현.
            override fun areItemsTheSame(oldItem: FriendItem, newItem: FriendItem): Boolean { // UserItem.kt에서 data class UserItem를 받아와서 사용 할 거야.
                return oldItem.friendId == newItem.friendId // UserId는 아마 Firebase의 UID가 될 것이야.
            }

            override fun areContentsTheSame(oldItem: FriendItem, newItem: FriendItem): Boolean {
                return oldItem == newItem
            }

        }
    }

    interface GoToFriendHomeListener {
        fun GoToFriendHome()
    }
    interface GoToFriendChatListener {
        fun GoToFriendChat(friendId : String , otherUserUid : String , nanSu : String)
    }
//    interface FriendClickListener {
//        // friendId를 ChatActivity에 보냄.
//        fun onFriendClick(friendId: String)
//    }

    private fun searchUserUidByEmail(wantedEmail : String , callback : (String) -> Unit) {
        Firebase.database.reference.child(Key.DB_USERS)
            .orderByChild("userName").equalTo(wantedEmail).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (data in snapshot.children) {
                            otherUserUid = data.child("userUid").getValue(String::class.java).toString()
                        }
                        callback(otherUserUid!!)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                }
            )

    }
}

