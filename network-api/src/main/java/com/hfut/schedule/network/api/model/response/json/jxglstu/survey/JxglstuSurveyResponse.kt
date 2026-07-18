package com.hfut.schedule.network.api.model.response.json.jxglstu.survey

import com.google.gson.annotations.SerializedName
import com.hfut.schedule.network.api.model.response.json.shared.MultiLanguageBaseData
import com.hfut.schedule.network.api.model.response.json.jxglstu.lesson.JxglstuLessonTeacher

//获取教师
data class JxglstuSurveyResponse(
    @SerializedName("forStdLessonSurveySearchVms")
    val list : List<JxglstuSurveyLesson>
)

data class JxglstuSurveyLesson(
    val code : String,
    val openEndTimeContent : String?,
    val course : MultiLanguageBaseData,
    val openDepartment : MultiLanguageBaseData,
    val lessonSurveyTasks : List<JxglstuSurveyTeacher>
)

data class JxglstuSurveyTeacher(
    val id : Int,
    val submitted : Boolean,
    val teacher : JxglstuLessonTeacher
)