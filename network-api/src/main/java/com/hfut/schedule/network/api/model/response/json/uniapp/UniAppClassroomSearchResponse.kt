package com.hfut.schedule.network.api.model.response.json.uniapp

import com.hfut.schedule.network.api.model.response.json.shared.MultiLanguageBaseData

data class UniAppClassroomSearchResponse(
    val data : UniAppClassroomSearchData
)

data class UniAppClassroomSearchData(
    val data : List<UniAppClassroomSearchResult>
)

data class UniAppClassroomSearchResult(
    val id : Int,
    val nameZh : String,
    val floor : Int,
    val seatsForLesson : Int,
    val roomType : MultiLanguageBaseData,
    val building : UniAppClassroomSearchResultBuilding
)

data class UniAppClassroomSearchResultBuilding(
    val nameZh : String,
    val campus : MultiLanguageBaseData
)