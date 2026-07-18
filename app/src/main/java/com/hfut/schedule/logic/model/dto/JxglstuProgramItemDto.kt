package com.hfut.schedule.logic.model.dto

data class JxglstuProgramItemDto(
    val term : Int?,
    val name : String,
    val credit : Double?,
    val depart :String,
    val code : String,
    val week : Int?,
    val courseType : String,
    val remark : String?,
    val isCompulsory : Boolean
)