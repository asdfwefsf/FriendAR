package com.company.chat

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.company.india1.Key
import com.company.india1.UserItem
import com.company.india1.databinding.ActivityChatBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
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

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    private var isKeyboardOpened = false

    private val chatItemList = mutableListOf<ChatItem>()

    ////////////////////////////////////
    private var myUserName: String = ""
    private var otherUserUid: String = ""
    private var otherUserFcmToken = ""
    private var otherUserName = ""

    ////////////////////////////////////
    private var FCMmyUserName: String = ""
    private var FCMotherUserFcmToken: String = ""
    private var FCMotherUserName: String = ""

    ////////////////////////////////////
    private var currentUser = Firebase.auth.uid.toString()
    //    private var myUserId: String = ""

    private val onGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        val rect = Rect()
        binding.root.getWindowVisibleDisplayFrame(rect)
        val screenHeight = binding.root.height
        val keypadHeight = screenHeight - rect.bottom

        // 키보드가 표시되었는지 여부를 판단합니다 (키보드 높이를 기준으로)
        if (keypadHeight > screenHeight * 0.15) { // 키보드가 표시됨
            if (!isKeyboardOpened) {
                isKeyboardOpened = true
                // 키보드가 표시될 때 스크롤을 조정합니다
                binding.chatRecyclerView.post {
                    binding.chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
                }
            }
        } else {
            isKeyboardOpened = false
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener)


        val currentUserUid = Firebase.auth.uid!!.toString()
        val otherUserUid = intent.getStringExtra("otherUserUid").toString()
        val nanSu = intent.getStringExtra("nanSu").toString()
        chatAdapter = ChatAdapter()
        linearLayoutManager = LinearLayoutManager(applicationContext).apply {
//            stackFromEnd = true
        }

        Firebase.database.reference.child(Key.DB_USERS).child(
            currentUserUid
        ).get()
            .addOnSuccessListener {
                val myUserItem = it.getValue(UserItem::class.java)
                myUserName = myUserItem?.userName ?: ""
                Log.e("ChatActivity1", "myUserName : $myUserName")
                Firebase.database.reference.child(Key.DB_USERS).child(otherUserUid).get()
                    .addOnSuccessListener {
                        Log.e("ChatActivity1", "otherUserUid : $otherUserUid")

                        val otherUserItem = it.getValue(UserItem::class.java)
                        otherUserFcmToken = otherUserItem?.messageFcmToken.toString()
                        otherUserName = otherUserItem?.userName.toString()
                        chatAdapter.otherUserItem = otherUserItem

                        Firebase.database.reference.child("Chats").child(nanSu)
                            .addChildEventListener(object : ChildEventListener {
                                // addChildEventListener() : Firebase Realtime Database의 데이터에 대한 변경 사항을 감지하고 이벤트를 수신하는데 사용되는 리스너.
                                // -> 이 리스너는 Database의 특정 위치에 대한 항목(child)의 추가, 변경, 삭제, 이동, 작업의 취소 이 5개에 대한 인터페이스가 주어진다.
                                override fun onChildAdded(
                                    snapshot: DataSnapshot,
                                    previousChildName: String?
                                ) {
                                    val chatItem = snapshot.getValue(ChatItem::class.java)
                                    Log.d("ChatActivity", "3: $chatItem")
                                    Log.d("ChatActivity", "4: $nanSu")
                                    chatItem ?: return
                                    chatItemList.add(chatItem)
                                    chatAdapter.submitList(chatItemList.toMutableList())
                                }

                                override fun onChildChanged(
                                    snapshot: DataSnapshot,
                                    previousChildName: String?
                                ) {
                                }

                                override fun onChildRemoved(snapshot: DataSnapshot) {}

                                override fun onChildMoved(
                                    snapshot: DataSnapshot,
                                    previousChildName: String?
                                ) {
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.d("ChatActivity", "4: 44")

                                }

                            })
                    }
                Log.d("ChatActivity", "1: $nanSu")
            }
// RecyclerView에 layoutManager와 adapter를 설정하여
        binding.chatRecyclerView.apply {
            layoutManager = linearLayoutManager
            adapter = chatAdapter


            if (chatAdapter.itemCount > 0) {
                addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                    if (bottom < oldBottom) {
                        postDelayed({
                            smoothScrollToPosition(chatAdapter.itemCount - 1)
                        }, 1)
                    }
                }

            }

        }
        chatAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
//                linearLayoutManager.scrollToPositionWithOffset(0, 0)

                linearLayoutManager.smoothScrollToPosition(
                    binding.chatRecyclerView,
                    null,
                    chatAdapter.itemCount,
                )
            }
        })

        // 메시지 전송 버튼 이벤트
        binding.sendButton.setOnClickListener {
//            binding.chatRecyclerView.scrollToPosition(0)

            val message = binding.messageEditText.text.toString()
            if (message.isEmpty()) {
                Toast.makeText(this@ChatActivity, "빈 메시지를 전송할 수는 없습니다.", Toast.LENGTH_SHORT).show()
            }
            val newChatItem = ChatItem(
                message = message,
                myId = currentUser
            )
            Firebase.database.reference.child(Key.DB_CHATS).child(nanSu).push().apply {
                newChatItem.friendId = key
                setValue(newChatItem)
            }

            val updates: MutableMap<String, Any> = hashMapOf(
                "${Key.DB_CHAT_ROOMS}/$currentUser/$otherUserUid/lastMessage" to message,
                "${Key.DB_CHAT_ROOMS}/$otherUserUid/$currentUser/lastMessage" to message,
                "${Key.DB_CHAT_ROOMS}/$otherUserUid/$currentUser/nanSu" to nanSu,
                "${Key.DB_CHAT_ROOMS}/$otherUserUid/$currentUser/otherUserEmail" to myUserName,
                "${Key.DB_CHAT_ROOMS}/$otherUserUid/$currentUser/otherUserId" to currentUser,
            )
            Firebase.database.reference.updateChildren(updates)

            val client = OkHttpClient()

            val root = JSONObject()
            val notification = JSONObject()

            notification.put("title", "놀꾸야")
            notification.put("body", message)
            root.put("to", otherUserFcmToken)
            root.put("priority", "high")
            root.put("notification", notification)

            root.put("data", JSONObject().apply {
                put("myUserName", myUserName)
                put("FCM_otherUserUid", currentUserUid)
                put("FCM_currentUserUid", otherUserUid)
                put("FCM_nanSu", nanSu)
                put("data_type", "MessagingService")
            })

            Log.e("ChatActivity1", "myUserName : $myUserName")
            Log.e("ChatActivity1", "currentUserUid : $currentUserUid")
            Log.e("ChatActivity1", "otherUserUid : $otherUserUid")
            Log.e("ChatActivity1", "nanSu : $nanSu")


            val requestBody =
                root.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val request =
                Request.Builder().post(requestBody).url("https://fcm.googleapis.com/fcm/send")
                    .header("Authorization", "key=${Key.FCM_SERVER_KEY}").build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {}
                override fun onResponse(call: Call, response: Response) {}
            })
            binding.messageEditText.text.clear()

        }

    }

}
