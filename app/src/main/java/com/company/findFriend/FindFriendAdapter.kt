package com.company.findFriend

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.company.Friend.FriendItem
import com.company.india1.Key
import com.company.india1.R
import com.company.india1.databinding.ItemFindFriendBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException


class FindFriendAdapter(
    private val onClick: (FriendItem) -> Unit,
    private var goToFriendHomeListener: GoToFriendHomeListener,

    ) : ListAdapter<FriendItem, FindFriendAdapter.ViewHolder>(differ) {
    // UserAdapter에서 item_user.xml을 클릭을 했을 때 가져온 User Id로 Chatting을 만들어 줄 수 있어.
    // 따라서 UserAdapter의 생성자에 ClickListener를 하나 만들어 준다. : onClick을 활용해서 User Id를 가져 올 거야.
    //
    private lateinit var otherUserFcmToken: String
    private var otherUserUid = ""
    private var emailAddress = ""
    private var descriptionMessage = ""
    private var currentUserUid = Firebase.auth.uid!!.toString()
    private var otherUserEmailAddress = "ddd"


    inner class ViewHolder(private val binding: ItemFindFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FriendItem) {
            binding.nicknameTextView.text = item.friendId
            binding.descriptionTextView.text = item.friendState

            binding.root.setOnClickListener {
                onClick(item) // item_user.xml의 View 요소들 중 하나를 클릭 했을 때 item을 Return 해준다.
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemFindFriendBinding.inflate( // inflate()의 Parameter 3가지 : context , ViewGroup , attachToParent
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
        holder.itemView.findViewById<ImageView>(R.id.profileImageView).setOnClickListener {
            goToFriendHomeListener.onItemDeleteClick(FriendItem = FriendItem())
        }
        otherUserFcmToken = ""
        val button = holder.itemView.findViewById<Button>(R.id.button)
        button?.setOnClickListener {
            val uid = Firebase.auth.currentUser!!.uid
            val friendId =
                holder.itemView.findViewById<TextView>(R.id.nicknameTextView).text.toString()
            val friendState =
                holder.itemView.findViewById<TextView>(R.id.descriptionTextView).text.toString()
            val friendList = mutableMapOf<String, Any>()
            friendList["friendId"] = friendId
            friendList["friendState"] = friendState

            Firebase.database.reference.child(Key.DB_USERS).child(uid)
                .addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            otherUserEmailAddress =
                                snapshot.child("userName").getValue(String::class.java)!!
                            val sector = friendId.lastIndexOf(".")
                            val Sector = friendId.substring(0, sector)
                            // friendName 자리에 일반 "이런식으로" 들어가면 키값이 변경되는데 , 다른 식으로 넣으면 변경이 안된다.

                            emailAddress = friendId
                            descriptionMessage = friendState
                            Firebase.database.reference.child(Key.DB_USERS).orderByChild("userName")
                                .equalTo(friendId)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        for (snapshot in dataSnapshot.children) {
                                            otherUserFcmToken = snapshot.child("messageFcmToken")
                                                .getValue(String::class.java)!!
                                            val client = OkHttpClient()
                                            val root = JSONObject()
                                            val notification = JSONObject()
                                            notification.put("title", "친추가 왔습니다.")
                                            notification.put("body", otherUserEmailAddress)
                                            root.put("to", otherUserFcmToken)
                                            root.put("priority", "high")
                                            root.put("notification", notification)

                                            root.put("data", JSONObject().apply {
                                                put("data_type", "FriendingService")
                                                put("emailAddress", emailAddress)
                                                put("descriptionMessage", descriptionMessage)
                                                put("otherUserEmailAddress", otherUserEmailAddress)
                                            })
                                            Log.e("FCMActivity", emailAddress)

                                            val requestBody = root.toString()
                                                .toRequestBody("application/json; charset=UTF-8".toMediaType())
                                            val request = Request.Builder().post(requestBody)
                                                .url("https://fcm.googleapis.com/fcm/send")
                                                .header(
                                                    "Authorization",
                                                    "key=${Key.FCM_SERVER_KEY}"
                                                ).build()
                                            client.newCall(request).enqueue(object : Callback {
                                                override fun onFailure(
                                                    call: Call,
                                                    e: IOException
                                                ) {
                                                }

                                                override fun onResponse(
                                                    call: Call,
                                                    response: Response
                                                ) {
                                                }
                                            })
                                        }
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {}
                                })
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    }
                )
        }
    }


    companion object {
        val differ = object : DiffUtil.ItemCallback<FriendItem>() { // Interface니까 object로 구현.
            override fun areItemsTheSame(
                oldItem: FriendItem,
                newItem: FriendItem
            ): Boolean { // UserItem.kt에서 data class UserItem를 받아와서 사용 할 거야.
                return oldItem.friendId == newItem.friendId // UserId는 아마 Firebase의 UID가 될 것이야.
            }

            override fun areContentsTheSame(oldItem: FriendItem, newItem: FriendItem): Boolean {
                return oldItem == newItem
            }

        }
    }

    interface GoToFriendHomeListener {
        fun onItemDeleteClick(FriendItem: FriendItem)
    }
}