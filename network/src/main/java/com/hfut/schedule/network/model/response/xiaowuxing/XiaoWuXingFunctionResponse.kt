package com.hfut.schedule.network.model.response.xiaowuxing

import com.google.gson.annotations.SerializedName

data class XiaoWuXingFunctionResponse(
    @SerializedName("errcode")
    val code : String,
    val result : XiaoWuXingFunctionResult
)

data class XiaoWuXingFunctionResult(
    val data : List<XiaoWuXingFunction>
)

data class XiaoWuXingFunction(
    @SerializedName("vcPrintTypeId")
    val fileProperty : String,
    @SerializedName("printType")
    val name : String,
    @SerializedName("fileProerty")
    val filePropertyType : String,
)