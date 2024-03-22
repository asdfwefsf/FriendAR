package com.company.Friend

data class FriendItem( // Firebase의 DB에 들어갈 것 하고 거의 연관이 된다.
    var friendId : String? =null,
//    var friendName: String? = null, 담에 꼭 해주기
    val friendState: String? = null, // 상태 메시지

)