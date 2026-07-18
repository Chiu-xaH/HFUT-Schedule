package com.hfut.schedule.network.model.response.xiaowuxing

import com.google.gson.annotations.SerializedName

data class XiaoWuXingLoginResponse(
    @SerializedName("errcode")
    val code : String,
    val result : XiaoWuXingLoginResult
)

data class XiaoWuXingLoginResult(
    val data : List<XiaoWuXingUserInfo>,
    val token : String,
)

data class XiaoWuXingUserInfo(
    val grade : String,
    val name : String,
    val schoolCode : Long,
    val userId : String
)