package com.hfut.schedule.network.api.model.response.json.jxglstu.lesson

data class JxglstuTextbookResponse(
    val textbookAssignMap : Map<String,JxglstuTextbook>
)

data class JxglstuTextbook(
    val textbook : String,
    val specialTextbook : String
)