package com.hfut.schedule.logic.datamodel.Jxglstu

data class lessonResponse(val lessonIds : List<Int>, val lessons : List<lessons>)
data class lessons(val nameZh : String?,
                   val remark : String?,
                   val scheduleText : scheduleText,
                   val stdCount : Int?,
                   val course : course,
                   val courseType : courseType,
                   val openDepartment : courseType,
                   val examMode : courseType,
                   val scheduleWeeksInfo : String?,
                   val teacherAssignmentList : List<teacherAssignmentList2>)
data class scheduleText(val dateTimePlacePersonText : dateTimePlacePersonText )
data class dateTimePlacePersonText(val textZh : String?)

data class course(val nameZh : String,val credits : Double?)
data class courseType(val nameZh: String,)

data class teacherAssignmentList2(val teacher : teacher,val age : Int?)
data class teacher(val person : courseType,
                   val title : courseType,
                   val type : courseType?)