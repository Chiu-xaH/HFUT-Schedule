package com.hfut.schedule.network.api.model.request.ai

import com.hfut.schedule.network.api.model.response.json.ai.AiChatMsg

data class AiChatRequest(
    val model : String,
    val messages : List<AiChatMsg>,
    val temperature : Float
)