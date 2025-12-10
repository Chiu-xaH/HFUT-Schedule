package com.hfut.schedule.logic.model.uniapp

import com.google.gson.annotations.SerializedName

data class UniAppClassmatesResponse(
    val data : List<UniAppClassmatesBean>?
)

data class UniAppClassmatesBean(
    val code : String,
    val nameZh : String,
    @SerializedName("adminclass")
    val className : String,
    val gender : String,
    val telephone : String?,
)