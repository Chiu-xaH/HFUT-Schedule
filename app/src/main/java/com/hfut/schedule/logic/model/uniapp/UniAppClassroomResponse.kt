package com.hfut.schedule.logic.model.uniapp

import com.hfut.schedule.logic.enumeration.Campus
import com.hfut.schedule.logic.util.network.getPageSize

data class UniAppClassroomResponse(
    val data : UniAppClassroomData
)

data class UniAppClassroomData(
    val data : List<UniAppClassroomBean>
)

data class UniAppClassroomBean(
    val nameZh : String,
    val campusNameZh : String,
    val roomOccupationInfoVms : List<UniAppClassroomLesson>?
)

data class UniAppClassroomLesson(
    val date : String,
    // HH-MM
    val startTimeString : String,
    val endTimeString : String,
    val activityType : String,
    val activityName : String,
    val teacherName : String
)

data class UniAppClassroomRequest(
    val currentPage : Int,
    // 从date向后7天，YYYY-MM-DD
    val date : String,
    // 校区ID
    val campusAssoc : Int?,
    // 建筑ID
    val buildingIds : List<Int>?,
    // 楼层
    val floors : List<Int>?,
    val pageSize : Int = getPageSize(),
)

enum class UniAppCampus(val code : Int,val campus : Campus) {
    XC(6, Campus.XC),TXL(2, Campus.TXL),FCH(3, Campus.FCH)
}

enum class ClassroomOccupiedCause(val activityType : String) {
    BORROWED("RoomBorrow"),IN_LESSON("Lesson")
}
