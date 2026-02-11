package com.hfut.schedule.logic.model

data class ChatRequest(
    val model : String,
    val messages : List<ChatMsg>,
    val temperature : Float
)



