package com.hfut.schedule.logic.model.uniapp

import android.accessibilityservice.GestureDescription
import com.hfut.schedule.logic.enumeration.Campus

import com.hfut.schedule.network.util.Constant

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



enum class UniAppCampus(val code : Int,val campus : Campus) {
    XC(6, Campus.XC),TXL(2, Campus.TXL),FCH(3, Campus.FCH)
}

enum class ClassroomOccupiedCause(val activityType : String,val description: String) {
    BORROWED("RoomBorrow","借用"),IN_LESSON("Lesson","上课"),EXAM("Exam","考试")
}
