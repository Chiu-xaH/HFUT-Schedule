package com.hfut.schedule.logic.model.uniapp

import com.hfut.schedule.logic.model.jxglstu.NameZh

data class UniAppSearchClassroomsResponse(
    val data : UniAppSearchClassroomsData
)

data class UniAppSearchClassroomsData(
    val data : List<UniAppSearchClassroomBean>
)

data class UniAppSearchClassroomBean(
    val id : Int,
    val nameZh : String,
    val floor : Int,
    val seatsForLesson : Int,
    val roomType : NameZh,
    val building : UniAppSearchClassroomBuilding
)

data class UniAppSearchClassroomBuilding(
    val nameZh : String,
    val campus : NameZh
)