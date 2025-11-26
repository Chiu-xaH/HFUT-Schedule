package com.hfut.schedule.logic.model.xwx

import com.google.gson.annotations.SerializedName

data class XwxLoginResponseBody(
    @SerializedName("errcode")
    val code : String,
    val result : XwxLoginBean
)

data class XwxLoginBean(
    val data : List<XwxUserInfo>,
    val token : String,
)

data class XwxUserInfo(
    val grade : String,
    val name : String,
    val schoolCode : Long,
    val userId : String
)

data class XwxLoginInfo(
    val data : XwxUserInfo,
    val token : String,
)
