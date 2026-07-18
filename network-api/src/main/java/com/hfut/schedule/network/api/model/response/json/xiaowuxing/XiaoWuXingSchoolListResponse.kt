package com.hfut.schedule.network.api.model.response.json.xiaowuxing

import com.google.gson.annotations.SerializedName

data class XiaoWuXingSchoolListResponse(
    @SerializedName("errcode")
    val code : String,
    val result : XiaoWuXingSchoolList
)

data class XiaoWuXingSchoolList(
    val data : List<XiaoWuXingSchoolKey>
)

data class XiaoWuXingSchoolKey(
    val list : List<XiaoWuXingSchool>
)

data class XiaoWuXingSchool(
    val schoolCode : Long,
    val schoolName : String,
    val iconUrl : String,
)