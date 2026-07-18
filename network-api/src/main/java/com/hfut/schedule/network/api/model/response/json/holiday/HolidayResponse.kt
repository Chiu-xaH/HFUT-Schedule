package com.hfut.schedule.network.api.model.response.json.holiday

data class HolidayResponse(
    val year : String,
    val days : List<Holiday>
)

data class Holiday(
    val name : String,
    val date : String,
    val isOffDay : Boolean
)