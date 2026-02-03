package com.hfut.schedule.logic.model.library

data class LibrarySearchResponse(
    val categoryPath : List<String>,
    val publishers : String,
    val year : Int,
    val title : String,
    val abstract : String,
//    val language : String,
    /**
     * 点击量
     */
    val click : Int,
    val isbn : String,
    val author : List<String>,
    /**
     * 来源
     */
    val ds : List<LibrarySearchBean>,
    /**
     * 机构
     */
    val unit : List<String>,
    /**
     * 位置
     */
    val gc : List<LibrarySearchPositionBean>
)

data class LibrarySearchBean(
    val tName : String,
)

data class LibrarySearchPositionBean(
    /**
     * 位置
     */
    val cp : String,
    /**
     * 索书号
     */
    val `in` : String,

)