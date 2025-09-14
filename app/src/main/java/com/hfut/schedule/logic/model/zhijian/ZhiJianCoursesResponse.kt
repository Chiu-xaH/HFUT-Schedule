package com.hfut.schedule.logic.model.zhijian

import com.google.gson.annotations.SerializedName
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.next.parseSingleChineseDigit

data class ZhiJianCoursesResponse(
    val data : ZhiJianCoursesBean
)

data class ZhiJianCoursesBean(
    @SerializedName("kbdata")
    val courseJsonString : String,
    @SerializedName("rawdata")
    val rawJsonString : String
)


data class ZhiJianCourseItem(
    @SerializedName("kcmc")
    val courseName : String,
    @SerializedName("skjc")
    val startPeriod : String,
    @SerializedName("jxdd")
    val place : String?,
    @SerializedName("jsxm")
    val teacher : String,
    @SerializedName("skbm")
    val department : String,
    @SerializedName("jxbdm")
    val classes : String,
    @SerializedName("skrq")
    val date : String,
    @SerializedName("kxh")
    val code : String,
    @SerializedName("kclx")
    val type : String,
    @SerializedName("dayofweek")
    val weekday : String,
    @SerializedName("cxjc")
    val period : String
) {
    fun toDto() : ZhiJianCourseItemDto? =
        try {
            val start = startPeriod.toInt()
            val end = start + period.toInt() - 1
            ZhiJianCourseItemDto(
                courseName = courseName,
                startPeriod = start,
                endPeriod = end,
                place = place?.substringAfter(",")?.replace("学堂","") ?: "",
                teacher = teacher,
                department = department.substringBefore("（"),
                classes = classes,
                date = date,
                code = code,
                type = type,
                weekday = parseSingleChineseDigit(weekday[1]),
            )
        } catch (e : Exception) {
            e.printStackTrace()
            null
        }
}

data class ZhiJianCourseItemDto(
    val courseName : String,
    val startPeriod : Int,
    val endPeriod : Int,
    val place : String,
    val teacher : String,
    val department : String,
    val classes : String,
    val date : String,
    val code : String,
    val type : String,
    val weekday : Int,
)