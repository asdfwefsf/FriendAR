package com.company.fcm

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.company.Key
import com.company.india1.databinding.ActivityFcmfindFriendBinding
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

class FCMAcceptFindFriendActivity : AppCompatActivity() {
    private lateinit var binding : ActivityFcmfindFriendBinding
    private var sector = 0
    private var Sector = ""
    private var currentUserUid = Firebase.auth.uid!!.toString()
    private val friendList = mutableMapOf<String , Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFcmfindFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val notificationId = 0
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)

        val otherUserEmailAddress = intent.getStringExtra("otherUserEmailAddress").toString()
        val emailAddress = intent.getStringExtra("emailAddress").toString()
        val descriptionMessage = intent.getStringExtra("descriptionMessage").toString()
        binding.friendEmailAddress.text = otherUserEmailAddress


        if (intent.action == "ACCEPT_ACTION") {
//             친구추가 요청한 친구의 Firebase의 RealtimeDatabase의 친구 리스트에 추가.
            var otherUserUid = ""
            var otherUserFcmToken = ""
            Firebase.database.reference.child(Key.DB_USERS).orderByChild("userName")
                .equalTo("$otherUserEmailAddress")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (data in snapshot.children) {
                            otherUserUid = data.child("userUid").getValue(String::class.java).toString()
                            otherUserFcmToken = data.child("messageFcmToken").getValue(String::class.java).toString()
                            binding.chinchooButton.setOnClickListener {

                                val client = OkHttpClient()
                                val root = JSONObject()
                                val notification = JSONObject()
                                notification.put("title", "친추가 왔습니다.")
                                notification.put("body", emailAddress)
                                root.put("to", "$otherUserFcmToken")
                                root.put("priority", "high")
                                root.put("notification", notification)

                                root.put("data", JSONObject().apply {
                                    put("data_type", "otherFriendingService")
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
                            friendList["friendId"] = emailAddress
                            friendList["friendState"] = descriptionMessage
                            sector = emailAddress.lastIndexOf(".")
                            Sector = emailAddress.substring(0, sector)
                            Log.e("otherUserUid" , "$otherUserUid")
                            Log.e("Sector" , "$Sector")
                            Firebase.database.reference.child(Key.DB_FRIENDLIST).child(otherUserUid).child(Sector)
                                .updateChildren(friendList)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
Log.e("hahaha" ,otherUserEmailAddress )
Log.e("hahaha" , emailAddress)
Log.e("hahaha" , descriptionMessage)
Log.e("hahaha" , otherUserUid)
//            binding.chinchooButton.setOnClickListener {
//
//                val client = OkHttpClient()
//                val root = JSONObject()
//                val notification = JSONObject()
//                notification.put("title", "친추가 왔습니다.")
//                notification.put("body", emailAddress)
//                root.put("to", "dRIyyYblTiyDWRZYfRK8bv:APA91bFN90vHmyRzCrKz_UGHPijKPkWwjB7TRpIZcfLEgRf2tPlFJQbDn6WMphmwO9UG7ocpsKWuWYSvwwtg14Fhbu7BILEKjm_oQXUtafEPk4RP8cB_mjlVfN3OceExJ9nb-2QPmCxV")
//                root.put("priority", "high")
//                root.put("notification", notification)
//
//                root.put("data", JSONObject().apply {
//                    put("data_type", "otherFriendingService")
//                    put("emailAddress", emailAddress)
//                    put("descriptionMessage", descriptionMessage)
//                    put("otherUserEmailAddress", otherUserEmailAddress)
//                })
//                Log.e("FCMActivity", emailAddress)
//
//                val requestBody = root.toString()
//                    .toRequestBody("application/json; charset=UTF-8".toMediaType())
//                val request = Request.Builder().post(requestBody)
//                    .url("https://fcm.googleapis.com/fcm/send")
//                    .header(
//                        "Authorization",
//                        "key=${Key.FCM_SERVER_KEY}"
//                    ).build()
//                client.newCall(request).enqueue(object : Callback {
//                    override fun onFailure(
//                        call: Call,
//                        e: IOException
//                    ) {
//                    }
//
//                    override fun onResponse(
//                        call: Call,
//                        response: Response
//                    ) {
//                    }
//                })
//
//            }














        }



























        else{
            finish()
        }


    }



}