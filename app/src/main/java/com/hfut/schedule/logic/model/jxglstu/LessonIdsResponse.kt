package com.hfut.schedule.logic.model.jxglstu

data class lessonResponse(
    val lessonIds : List<Int>,
    val lessons : List<lessons>,
    val timeTableLayoutId : Int,
    val weekIndices : List<Int>,
    val currentWeek : Int
)
data class lessons(
    val id : Int,
    val nameZh : String?,
    val remark : String?,
    val scheduleText : scheduleText,
    val stdCount : Int?,
    val course : course,
    val courseType : NameZh,
    val openDepartment : NameZh,
    val examMode : NameZh,
    val scheduleWeeksInfo : String?,
    val planExamWeek : Int?,
    val teacherAssignmentList : List<teacherAssignmentList2>?,
    val semester : semester,
    val code : String)
data class scheduleText(val dateTimePlacePersonText : dateTimePlacePersonText )
data class dateTimePlacePersonText(val textZh : String?)

data class course(val id : Long,val nameZh : String,val credits : Double?,val code : String,val periodInfo : PeriodInfo,val courseType : NameZh)
data class NameZh(val nameZh: String)

data class teacherAssignmentList2(
    val teacher : teacher?,
    val age : Int?,
    val person : NameZh
)

data class teacher(val person : NameZh?,
                   val title : NameZh?,
                   val type : NameZh?)


data class semester(val id : Int,
                    val nameZh : String,
                    val startDate : String,
                    val endDate : String)

data class PeriodInfo(val weeks : Int?)