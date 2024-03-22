package com.company.Friend

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.company.chat.ChatActivity
import com.company.india1.Key
import com.company.india1.databinding.ActivityFriendBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FriendActivity : AppCompatActivity(), FriendAdapter.GoToFriendHomeListener,
    FriendAdapter.GoToFriendChatListener
//    FriendAdapter.FriendClickListener
{
    private lateinit var binding: ActivityFriendBinding
    private lateinit var friendAdapter: FriendAdapter
    private var currentUser = Firebase.auth.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val test = intent.getBooleanExtra("true" , true)
//        Log.d("FriendActivity" , test.toString())
//        if (test) {
//            val testIntent = Intent(this , ChatActivity::class.java)
//            startActivity(testIntent)
//        }

//        val intent = intent
//        if (intent.action == "GO_TO_FRIEND_CHAT") {
//            val FCMfriendId = intent.getStringExtra("bbb@bbb").toString()
//            val FCMotherUserUid = intent.getStringExtra("FCM_currentUserUid").toString()
//            val FCMnanSu = intent.getStringExtra("FCM_nanSu").toString()
//
//            // FriendActivity의 GoToFriendChat 함수를 호출
//            GoToFriendChat(FCMfriendId, FCMotherUserUid, FCMnanSu)
//        }

        binding.recyclerView.apply {
            friendAdapter = FriendAdapter(
//                onClick = { friendItem ->
//                    // 사용자가 친구를 클릭할 때 실행될 작업을 정의합니다.
//                    // friendItem을 이용하여 클릭한 친구에 대한 작업을 수행합니다.
//                    // 예: 친구의 정보 보기, 채팅 시작 등
//                },
                this@FriendActivity,
                this@FriendActivity,
//                this@FriendActivity
            )
            adapter = friendAdapter
            layoutManager =
                LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            val dividerItemDecoration =
                DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL)
            addItemDecoration(dividerItemDecoration)
        }

        Firebase.database.reference.child(Key.DB_FRIENDLIST)
            .child(currentUser!!).orderByChild("friendId")
            .addListenerForSingleValueEvent(object :
                ValueEventListener { // addListenerForSingleValueEvent : 데이터를 한 번 만 읽어와
                // addListenerForSingleValueEvent(@NonNull final ValueEventListener listener)는 Parameter로 ValueEventListener를 받는다.
                // ValueEventListener : This method will be called with a snapshot of the data at this location. It will also be called each time that data changes.
                // Data가 변경되면 콜백 함수를 실행한다.
                override fun onDataChange(snapshot: DataSnapshot) {
                    // DataSnapshot : Firebase Realtime Database에서 가져온 Data의 스냅샷을 나타내는 클래스이다.
                    // DataSnapshot 객체는 Database의 특정 위치에서 읽은 데이터를 포함하고 있으며, 이를 사용하여 해당 위치의 데이터를 가져오고 분석할 수 있다.

                    val friendItemList = mutableListOf<FriendItem>()

                    snapshot.children.forEach {
                        val friendId = it.child("friendId").getValue(String::class.java)
                        val friendState = it.child("friendState").getValue(String::class.java)
                        val findFriendItem = FriendItem(friendId , friendState)
                        friendItemList.add(findFriendItem)

                    }

                    friendAdapter.submitList(friendItemList) // Data를 보여주는 방법
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

    }

    override fun GoToFriendHome() {
        TODO("Not yet implemented")
    }

    override fun GoToFriendChat(friendId : String , otherUserUid : String , nanSu : String) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("name", friendId)
        intent.putExtra("userUid" , currentUser)
        intent.putExtra("otherUserUid" , otherUserUid)
        intent.putExtra("nanSu" , nanSu)
        Log.d("ChatActivity", "Received nanSu: $nanSu")

        startActivity(intent)

    }

}

