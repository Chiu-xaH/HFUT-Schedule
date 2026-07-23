package com.hfut.schedule.network.api.model.response.html

data class JxglstuExam(
    val name : String,
    val dateTime : String,
    val place : String?,
    val type : String? = null
)