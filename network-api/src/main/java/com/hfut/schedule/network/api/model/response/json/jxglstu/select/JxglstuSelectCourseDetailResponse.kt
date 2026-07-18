package com.hfut.schedule.network.api.model.response.json.jxglstu.select

import com.hfut.schedule.network.api.model.response.json.shared.MultiLanguageBaseData
import com.hfut.schedule.network.api.model.response.json.jxglstu.lesson.JxglstuLessonCourseInfo
import com.hfut.schedule.network.api.model.response.json.jxglstu.lesson.JxglstuTermLessonScheduleDateTimePlacePersonText

data class JxglstuSelectCourseDetailResponse(
    val id : Int,
    val code : String,
    val nameZh : String,
    val teachers : List<MultiLanguageBaseData>,
    val course: JxglstuLessonCourseInfo,
    val courseType : MultiLanguageBaseData,
    val examMode : MultiLanguageBaseData,
    val limitCount : Int,
    val remark : String?,
    val dateTimePlace : JxglstuTermLessonScheduleDateTimePlacePersonText
)