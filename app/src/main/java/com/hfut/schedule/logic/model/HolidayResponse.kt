package com.hfut.schedule.logic.model

data class HolidayResponse(
    val year : String,
    val days : List<HolidayBean>
)

data class HolidayBean(
    val name : String,
    val date : String,
    val isOffDay : Boolean
)