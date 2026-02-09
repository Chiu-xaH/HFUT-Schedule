package com.hfut.schedule.logic.model

import com.hfut.schedule.ChatMsg

data class ChatRequest(
    val model : String,
    val messages : List<ChatMsg>,
    val temperature : Float
)



