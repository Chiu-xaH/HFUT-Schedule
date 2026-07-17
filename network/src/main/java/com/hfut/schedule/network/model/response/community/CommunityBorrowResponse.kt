package com.hfut.schedule.network.model.response.community

data class CommunityBorrowResponse(
    val result : CommunityBorrow
)

data class CommunityBorrow(
    val records : List<CommunityBorrowRecord>
)

data class CommunityBorrowRecord(
    val bookName : String,
    val author : String,
    val outTime : String,
    val returnTime : String?,
    val callNumber : String
)
