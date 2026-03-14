package com.hfut.schedule.network.model

data class ChatRequest(
    val model : String,
    val messages : List<ChatMsg>,
    val temperature : Float
)