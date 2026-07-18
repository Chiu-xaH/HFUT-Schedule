package com.hfut.schedule.network.api.model.response.json.library

data class LibrarySearchResponse(
    val data : LibrarySearchData
)

data class LibrarySearchData(
    val rows : List<LibrarySearchRow>
)

data class LibrarySearchRow(
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
    val gc : List<LibrarySearchPosition>?
)

data class LibrarySearchOrigin(
    val tName : String,
)

data class LibrarySearchPosition(
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