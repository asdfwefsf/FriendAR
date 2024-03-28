package com.company.india1

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.company.fcm.FCMAcceptFindFriendActivity
import com.company.fcm.FCMAcceptFindFriendActivity1
import com.company.fcm.FCMChatActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MessagingService: FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val dataType = message.data["data_type"].toString()
        Log.e("dataType" , dataType)
        if (dataType == "MessagingService") {
            val name = "채팅 알림"
            val descriptionText = "채팅 알림입니다."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(getString(R.string.default_notification_channel_id), name, importance)

            mChannel.description = descriptionText
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)

            val body = message.notification?.body ?: "" // body에 알릴 내용을 작성하면 된다. (알림에 보여줄 실질적인 Text)
            val data = message.data
            val myUserName = data["myUserName"]

            val FCM_nanSu = data["FCM_nanSu"].toString()
            val FCM_currentUserUid = data["FCM_currentUserUid"].toString()
            val FCM_otherUserUid = data["FCM_otherUserUid"].toString()

            val intent = Intent(this , FCMChatActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("FCM_currentUserUid" , FCM_currentUserUid)
            intent.putExtra("FCM_otherUserUid" , FCM_otherUserUid)
            intent.putExtra("FCM_nanSu" , FCM_nanSu)

            val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            val pendingIntent = PendingIntent.getActivity(
                this@MessagingService , 0 , intent , flags
            )

            val notificationBuilder = NotificationCompat.Builder(applicationContext, getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.drawable.addfriend)
                .setContentTitle("$myUserName")
                .setContentText(body)
                .setAutoCancel(true)
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)


            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permiss
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            NotificationManagerCompat.from(applicationContext).notify(0, notificationBuilder.build())
        } else if(dataType == "FriendingService") {
            val name = "친추 알림"
            val descriptionText = "친추 알림입니다."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(getString(R.string.default_notification_channel_id), name , importance)

            mChannel.description = descriptionText
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)

            val body = message.notification?.body ?: ""
            val data = message.data

            val otherUserEmailAddress = data["otherUserEmailAddress"].toString()
            val emailAddress = data["emailAddress"].toString()
            val descriptionMessage = data["descriptionMessage"].toString()

            val intent = Intent(this , FCMAcceptFindFriendActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("emailAddress" , emailAddress)
            intent.putExtra("descriptionMessage" , descriptionMessage)
            val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            val pendingIntent = PendingIntent.getActivity(
                this@MessagingService , 0 , intent , flags
            )

            val fcmFriendIntent = Intent(this@MessagingService, FCMAcceptFindFriendActivity::class.java)
            fcmFriendIntent.putExtra("otherUserEmailAddress" , otherUserEmailAddress)
            fcmFriendIntent.putExtra("emailAddress" , emailAddress)
            fcmFriendIntent.putExtra("descriptionMessage" , descriptionMessage)
            fcmFriendIntent.action = "ACCEPT_ACTION"  // 수락 작업을 식별하기 위한 액션
            val fcmFriendAcceptPendingIntent = PendingIntent.getActivity(this, 0, fcmFriendIntent, flags)
            Log.e("otherUserEmailAddress1" , otherUserEmailAddress)

            val rejectIntent = Intent(this@MessagingService, FCMAcceptFindFriendActivity::class.java)
            rejectIntent.action = "Reject_ACTION"  // 거절 작업을 식별하기 위한 액션
            val fcmFriendRejectPendingIntent = PendingIntent.getActivity(this, 0, rejectIntent, flags)

            val notificationBuilder = NotificationCompat.Builder(applicationContext,getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.drawable.addfriend)
                .setContentTitle("친추가 왔습니다.")
                .setContentText(body)
//                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.addfriend, "수락", fcmFriendAcceptPendingIntent)  // 수락 버튼
                .addAction(R.drawable.addfriend, "거절", fcmFriendRejectPendingIntent)  // 거절 버튼


            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            NotificationManagerCompat.from(applicationContext).notify(0 , notificationBuilder.build())

        } else if(dataType == "otherFriendingService") {
            val name = "친추 알림"
            val descriptionText = "친추 알림입니다."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(getString(R.string.default_notification_channel_id), name , importance)

            mChannel.description = descriptionText
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)

            val body = message.notification?.body ?: ""
            val data = message.data
            val otherUserEmailAddress = data["otherUserEmailAddress"].toString()
            val emailAddress = data["emailAddress"].toString()
            val descriptionMessage = data["descriptionMessage"].toString()
            Log.e("FriendingService" , "emailAddress : $emailAddress")
            Log.e("FriendingService" , "descriptionMessage : $descriptionMessage")
            val intent = Intent(this , FCMAcceptFindFriendActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("emailAddress" , emailAddress)
            intent.putExtra("descriptionMessage" , descriptionMessage)
            val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            val pendingIntent = PendingIntent.getActivity(
                this@MessagingService , 0 , intent , flags
            )

            val fcmFriendIntent = Intent(this@MessagingService, FCMAcceptFindFriendActivity1::class.java)
            fcmFriendIntent.putExtra("otherUserEmailAddress" , otherUserEmailAddress)
            fcmFriendIntent.putExtra("emailAddress" , emailAddress)
            fcmFriendIntent.putExtra("descriptionMessage" , descriptionMessage)
            fcmFriendIntent.action = "ACCEPT_ACTION"  // 수락 작업을 식별하기 위한 액션
            val fcmFriendAcceptPendingIntent = PendingIntent.getActivity(this, 0, fcmFriendIntent, flags)
            Log.e("otherUserEmailAddress1" , otherUserEmailAddress)

            val rejectIntent = Intent(this@MessagingService, FCMAcceptFindFriendActivity1::class.java)
            rejectIntent.action = "Reject_ACTION"  // 거절 작업을 식별하기 위한 액션
            val fcmFriendRejectPendingIntent = PendingIntent.getActivity(this, 0, rejectIntent, flags)

            val notificationBuilder = NotificationCompat.Builder(applicationContext,getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.drawable.addfriend)
                .setContentTitle("친추가 왔습니다.")
                .setContentText(body)
//                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.addfriend, "수락", fcmFriendAcceptPendingIntent)  // 수락 버튼
                .addAction(R.drawable.addfriend, "거절", fcmFriendRejectPendingIntent)  // 거절 버튼


            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            NotificationManagerCompat.from(applicationContext).notify(0 , notificationBuilder.build())

        }


    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }


}