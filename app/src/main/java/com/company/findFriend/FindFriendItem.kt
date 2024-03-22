package com.company.findFriend

data class FindFriendItem( // Firebase의 DB에 들어갈 것 하고 거의 연관이 된다.
    val userId: String? = null,
    val username: String? = null,
    val description: String? = null, // 상태 메시지
    val fcmToken: String? = null,
    val friendName : String? = null
)