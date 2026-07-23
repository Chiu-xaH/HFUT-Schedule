package com.hfut.schedule.network.api.model.response.json.zhijian

import com.google.gson.annotations.SerializedName

data class ZhiJianCourseTableResponse(
    val data : ZhiJianCourseTableData
)

data class ZhiJianCourseTableData(
    @SerializedName("rawdata")
    val rawJsonString : String
)

// 解析 ZhiJianCourseTable#rawJsonString
data class ZhiJianCourseTable(
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
)

data class ZhiJianCourseTableDto(
    val courseName : String,
    val startPeriod : Int,
    val endPeriod : Int,
    val place : String?,
    val teacher : String,
    val department : String,
    val classes : String,
    val date : String,
    val code : String,
    val type : String,
    val weekday : Int,
)