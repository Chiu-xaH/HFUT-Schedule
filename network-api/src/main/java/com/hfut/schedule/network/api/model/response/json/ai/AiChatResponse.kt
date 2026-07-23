package com.hfut.schedule.network.api.model.response.json.ai

import com.google.gson.annotations.SerializedName

data class AiChatResponse(
    val usage : AiChatUsageBean,
    val model : String,
    val choices : List<AiChatChoice>
)

data class AiChatUsageBean(
    @SerializedName("prompt_tokens")
    val promptTokens : Int,
    @SerializedName("completion_tokens")
    val completionTokens : Int,
    @SerializedName("total_tokens")
    val totalTokens : Int
)

data class AiChatChoice(
    val message: AiChatMsg,
    val index : Int,
    @SerializedName("finish_reason")
    val finishReason : String
)

data class AiChatMsg(
    val role : String,
    val content : String
)
