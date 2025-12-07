package com.hfut.schedule.logic.model.library

import com.google.gson.annotations.SerializedName

data class LibraryBorrowedResponse(
    val data : LibraryBorrowed
)

data class LibraryBorrowed(
    val list : List<LibraryBorrowedBean>
)

data class LibraryBorrowedBean(
    val callNo : String,
    val location : String,
    val status : String,
    val realReturnTime : String? = null,
    val returnTime : String? = null,
    val createdTime : String,
    val libraryDetail : LibraryDetail
)

data class LibraryDetail(
    val detail : LibraryDetailBean,
)

data class LibraryDetailBean(
    val isbn : String,
    val title : String,
    val authors : String,
    val publishers : String,
    @SerializedName("cbrq")
    val year : String,
    val digest : String,
    val keywords : String
)

enum class BorrowedStatus(val status : String,val description: String) {
    RETURNED("0","已还"),
    BORROWING("2","借阅中"),
    OVERDUE("02","逾期待还")
}
