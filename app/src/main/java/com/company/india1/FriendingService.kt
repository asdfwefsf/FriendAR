package com.company.india1

import com.google.firebase.messaging.FirebaseMessagingService

class FriendingService : FirebaseMessagingService() {
//    override fun onMessageReceived(message: RemoteMessage) {
//        super.onMessageReceived(message)
//        Log.d("FCMMessage", "Message received: ${message.data}")
//
//        val name = "친추 알림"
//        val descriptionText = "친추 알림입니다."
//        val importance = NotificationManager.IMPORTANCE_DEFAULT
//        val mChannel = NotificationChannel(getString(R.string.friend_notification_channel_id), name , importance)
//
//        mChannel.description = descriptionText
//        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.createNotificationChannel(mChannel)
//
//        val body = message.notification?.body ?: ""
//        val data = message.data
//        val emailAddress = data["emailAddress"].toString()
//        val descriptionMessage = data["descriptionMessage"].toString()
//        Log.e("FriendingService" , "emailAddress : $emailAddress")
//        Log.e("FriendingService" , "descriptionMessage : $descriptionMessage")
//        val intent = Intent(this , FCMAcceptFindFriendActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        intent.putExtra("emailAddress" , emailAddress)
//        intent.putExtra("descriptionMessage" , descriptionMessage)
//        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
//        val pendingIntent = PendingIntent.getActivity(
//            this@FriendingService , 1 , intent , flags
//        )
//        val notificationBuilder = NotificationCompat.Builder(applicationContext,getString(R.string.friend_notification_channel_id))
//            .setSmallIcon(R.drawable.addfriend)
//            .setContentTitle("$emailAddress 가 친구 추가 요청을 하였습니다.")
//            .setContentText(body)
//            .setAutoCancel(true)
//            .setContentIntent(pendingIntent)
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.POST_NOTIFICATIONS
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return
//        }
//        NotificationManagerCompat.from(applicationContext).notify(1 , notificationBuilder.build())
//    }
//
//    override fun onNewToken(token: String) {
//        super.onNewToken(token)
//    }
}


