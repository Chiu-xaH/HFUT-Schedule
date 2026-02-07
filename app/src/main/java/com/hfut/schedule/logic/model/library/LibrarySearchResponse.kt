package com.hfut.schedule.logic.model.library

data class LibrarySearchResponse(
    val data : LibrarySearchRows
)

data class LibrarySearchRows(
    val rows : List<LibrarySearchBean>
)

data class LibrarySearchBean(
    val categoryPath : List<String>,
    val publishers : String?,
    val year : Int,
    val title : String,
    val abstract : String?,
    /**
     * 点击量
     */
    val click : Int,
    val isbn : String,
    val author : List<String>,
    /**
     * 来源
     */
    val ds : List<LibrarySearchOrigin>?,
    /**
     * 位置
     */
    val gc : List<LibrarySearchPositionBean>?
)

data class LibrarySearchOrigin(
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
    /**
     * 状态
     */
    val js : String?
)