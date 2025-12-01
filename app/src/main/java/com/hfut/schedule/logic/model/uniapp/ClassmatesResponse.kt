package com.hfut.schedule.logic.model.uniapp

import com.google.gson.annotations.SerializedName

data class ClassmatesResponse(
    val data : List<ClassmatesBean>
)

data class ClassmatesBean(
    val code : String,
    val nameZh : String,
    @SerializedName("adminclass")
    val className : String,
    val gender : String,
    val telephone : String?,
)