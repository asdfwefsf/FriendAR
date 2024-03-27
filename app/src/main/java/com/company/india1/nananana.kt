package com.company.india1

//Firebase.database.reference.child(Key.DB_CHAT_ROOMS).child(currentUser!!)
//.child(otherUserUid!!).get().addOnSuccessListener { snapshot ->
//    if (snapshot.exists()) {
//        val chatRooms = snapshot.getValue(ChatRoomsItem::class.java)
//        val otherUserEmail = chatRooms?.otherUserEmail
//        val lastMessage = chatRooms?.lastMessage
//        val nana = chatRooms?.nanSu
//        goToFriendChatListener.GoToFriendChat(otherUserEmail!! , otherUserUid!! , nana!! )
//    }
//    else {
//        searchUserUidByEmail(otherUserEmail) {
//            val chatRooms = mutableMapOf<String , Any>()
//            chatRooms["nanSu"] = nanSu
//            chatRooms["lastMessage"] = "최근 메시지"
//            chatRooms["otherUserEmail"] = otherUserEmail
//            chatRooms["otherUserUid"] = it
//            otherUserUid = it
//            Firebase.database.reference.child(Key.DB_CHAT_ROOMS).child(currentUser).child(otherUserUid.toString())
//                .updateChildren(chatRooms)
//            goToFriendChatListener.GoToFriendChat(otherUserEmail , otherUserUid!! , nanSu)
//        }
//    }
//}
