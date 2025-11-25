package com.hfut.schedule.logic.model.xwx

import com.google.gson.annotations.SerializedName

data class XwxFunctionsResponseBody(
    @SerializedName("errcode")
    val code : String,
    val result : XwxFunctionsBean
)

data class XwxFunctionsBean(
    val data : List<XwxFunction>
)

data class XwxFunction(
    @SerializedName("vcPrintTypeId")
    val fileProperty : String,
    @SerializedName("printType")
    val name : String,
    @SerializedName("fileProerty")
    val filePropertyType : String,
)