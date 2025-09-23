package com.hfut.schedule.logic.model.library

data class LibraryStatusResponse(
    val data : List<LibraryStatusBean>
)

data class LibraryStatusBean(
    val code : String,
    val unit : String,
    val count : Int,
    val name : String
)

data class LibraryStatus(
    val bookShelfCount : Int = 0,
    val borrowCount : Int = 0,
    val reserveCount : Int = 0,
    val entrustCount : Int = 0,
    val followCount : Int = 0,
    val collectCount : Int = 0,
    val downloadCount : Int = 0,
    val sharingCount : Int = 0,
    val recommendCount : Int = 0
)
