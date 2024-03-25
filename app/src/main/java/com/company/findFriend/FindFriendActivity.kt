package com.company.findFriend

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.company.Friend.FriendItem
import com.company.india1.Key
import com.company.india1.databinding.ActivityFindFriendBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FindFriendActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFindFriendBinding
    private lateinit var findFriendAdapter: FindFriendAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageView.setOnClickListener {
            val wantedEmail = binding.editText.text.toString()
            searchUsersByEmail(wantedEmail) { userList ->
                // userList에 검색된 사용자 목록이 전달됨
                if (userList.isNotEmpty()) {
                    // 원하는 이메일 주소를 포함하는 사용자가 존재하는 경우
                    findFriendAdapter.submitList(userList) // RecyclerView에 사용자 목록을 전달
                }
            }
        }

        binding.recyclerView.apply {
            findFriendAdapter = FindFriendAdapter(
                onClick = { friendItem -> // 사용자가 친구를 클릭할 때 실행될 작업을 정의합니다.
                    // friendItem을 이용하여 클릭한 친구에 대한 작업을 수행합니다.
                    // 예: 친구의 정보 보기, 채팅 시작 등
                },
                goToFriendHomeListener = object : FindFriendAdapter.GoToFriendHomeListener {
                        override fun onItemDeleteClick(FriendItem: FriendItem) {
                        TODO("Not yet implemented")
                    }
                }
            )
            adapter = findFriendAdapter
            layoutManager = LinearLayoutManager(applicationContext , LinearLayoutManager.VERTICAL , false)
            val dividerItemDecoration = DividerItemDecoration(applicationContext , LinearLayoutManager.VERTICAL)
            addItemDecoration(dividerItemDecoration)
        }
    }

    private fun searchUsersByEmail(wantedEmail: String, callback: (List<FriendItem>) -> Unit) {
        Firebase.database.reference.child(Key.DB_USERS).orderByChild("userName").equalTo(wantedEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val userList = mutableListOf<FriendItem>()

                    for (snapshot in dataSnapshot.children) {
                        val userName = snapshot.child("userName").getValue(String::class.java)
                        val friendState = snapshot.child("userState").getValue(String::class.java)
                        val findFriendItem = FriendItem(userName , friendState)
                        userList.add(findFriendItem)
//                        userName?.let {
//                            val friendItem = FriendItem(it)
//                            userList.add(friendItem)
//                        }
                    }

                    callback(userList) // 검색된 사용자 목록을 콜백으로 반환 (FriendItem으로 변환)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    runOnUiThread {
                        Toast.makeText(this@FindFriendActivity , "없는 아이디입니다." , Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

}
