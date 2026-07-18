package com.hfut.schedule.network.api.model.request.chat

import com.hfut.schedule.network.api.model.response.json.ai.ChatMsg

data class ChatRequest(
    val model : String,
    val messages : List<ChatMsg>,
    val temperature : Float
)