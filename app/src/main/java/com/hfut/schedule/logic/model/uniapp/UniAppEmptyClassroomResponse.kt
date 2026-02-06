package com.hfut.schedule.logic.model.uniapp

import android.accessibilityservice.GestureDescription
import com.hfut.schedule.logic.enumeration.Campus
import com.hfut.schedule.logic.util.network.getPageSize

data class UniAppEmptyClassroomResponse(
    val data : UniAppEmptyClassroomData
)

data class UniAppEmptyClassroomData(
    val data : List<UniAppEmptyClassroomBean>
)

data class UniAppEmptyClassroomBean(
    val id : Int,
    val nameZh : String,
    val campusNameZh : String,
    val roomOccupationInfoVms : List<UniAppEmptyClassroomLesson>?
)

data class UniAppEmptyClassroomLesson(
    val date : String,
    // HH-MM
    val startTimeString : String,
    val endTimeString : String,
    val activityType : String,
    val activityName : String,
    val teacherName : String?
)

data class UniAppEmptyClassroomRequest(
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

enum class ClassroomOccupiedCause(val activityType : String,val description: String) {
    BORROWED("RoomBorrow","借用"),IN_LESSON("Lesson","上课"),EXAM("Exam","考试")
}
