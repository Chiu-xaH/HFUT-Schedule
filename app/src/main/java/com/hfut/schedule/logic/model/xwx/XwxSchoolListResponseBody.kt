package com.hfut.schedule.logic.model.xwx

import com.google.gson.annotations.SerializedName

data class XwxSchoolListResponseBody(
    @SerializedName("errcode")
    val code : String,
    val result : XwxSchoolListBean
)

data class XwxSchoolListBean(
    val data : List<XwxSchoolKeyBean>
)

data class XwxSchoolKeyBean(
    val list : List<XwxSchoolBean>
)

data class XwxSchoolBean(
    val schoolCode : Long,
    val schoolName : String,
    val iconUrl : String,
)

fun isXwxRequestSuccessful(code: String) : Boolean = code == "0"