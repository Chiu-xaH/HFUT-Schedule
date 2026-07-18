package com.hfut.schedule.network.api.model.response.json.jxglstu.select

data class JxglstuSelectCourseResponse(
    val id : Int,
    val name : String,
    val bulletin : String,
    val selectDateTimeText : String,
    val addRulesText : List<String>
)