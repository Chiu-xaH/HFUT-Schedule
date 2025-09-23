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
    val realReturnTime : String,
    val createdTime : String,
    val libraryDetail : LibraryDetail
)

data class LibraryDetail(
    val detail : LibraryDetailBean
)

data class LibraryDetailBean(
    val isbn : String,
    val title : String,
    val authors : String,
    val publishers : String,
    @SerializedName("cbrq")
    val year : String
)
