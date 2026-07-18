package com.hfut.schedule.network.api.model.response.json.library

import com.google.gson.annotations.SerializedName

data class LibraryBorrowResponse(
    val data : LibraryBorrowData
)

data class LibraryBorrowData(
    val list : List<LibraryBorrowRecord>
)

data class LibraryBorrowRecord(
    val callNo : String,
    val location : String,
    val status : String,
    val realReturnTime : String? = null,
    val returnTime : String? = null,
    val createdTime : String,
    val libraryDetail : LibraryDetailData
)

data class LibraryDetailData(
    val detail : LibraryDetail,
)

data class LibraryDetail(
    val isbn : String,
    val title : String,
    val authors : String,
    val publishers : String,
    @SerializedName("cbrq")
    val year : String,
    val digest : String,
    val keywords : String
)

enum class BorrowedStatus(
    val status : String,
    val description: String
) {
    RETURNED("0","已还"),
    BORROWING("2","借阅中"),
    OVERDUE("02","逾期待还")
}
