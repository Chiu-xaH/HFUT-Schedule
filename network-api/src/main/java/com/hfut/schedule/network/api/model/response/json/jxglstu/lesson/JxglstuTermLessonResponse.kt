package com.hfut.schedule.network.api.model.response.json.jxglstu.lesson

import com.hfut.schedule.network.api.model.response.json.shared.MultiLanguageBaseData

data class JxglstuTermLessonResponse(
    val lessonIds : List<Int>,
    val lessons : List<JxglstuLesson>,
    val timeTableLayoutId : Int,
    val weekIndices : List<Int>,
    val currentWeek : Int
)

data class JxglstuLesson(
    val id : Int,
    val nameZh : String?,
    val remark : String?,
    val scheduleText : JxglstuTermLessonScheduleText,
    val stdCount : Int?,
    val course : JxglstuLessonCourseInfo,
    val courseType : MultiLanguageBaseData,
    val openDepartment : MultiLanguageBaseData,
    val examMode : MultiLanguageBaseData,
    val scheduleWeeksInfo : String?,
    val planExamWeek : Int?,
    val teacherAssignmentList : List<JxglstuLessonTeacherAssignment>?,
    val semester : JxglstuLessonSemester,
    val code : String
)

data class JxglstuTermLessonScheduleText(
    val dateTimePlacePersonText : JxglstuTermLessonScheduleDateTimePlacePersonText
)

data class JxglstuTermLessonScheduleDateTimePlacePersonText(
    val textZh : String?
)

data class JxglstuLessonCourseInfo(
    val id : Long,
    val nameZh : String,
    val credits : Double?,
    val code : String,
    val periodInfo : JxglstuLessonCoursePeriodInfo,
    val courseType : MultiLanguageBaseData
)

data class JxglstuLessonTeacherAssignment(
    val teacher : JxglstuLessonTeacher?,
    val age : Int?,
    val person : MultiLanguageBaseData
)

data class JxglstuLessonTeacher(
    val person : MultiLanguageBaseData?,
    val title : MultiLanguageBaseData?,
    val type : MultiLanguageBaseData?
)

data class JxglstuLessonSemester(
    val id : Int,
    val nameZh : String,
    val startDate : String,
    val endDate : String
)

data class JxglstuLessonCoursePeriodInfo(
    val weeks : Int?
)