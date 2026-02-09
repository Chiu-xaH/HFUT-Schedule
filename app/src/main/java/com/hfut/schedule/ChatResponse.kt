package com.hfut.schedule

import com.google.gson.annotations.SerializedName

data class ChatResponse(
    val usage : ChatUsageBean,
    val model : String,
    val choices : List<ChatChoice>
)

data class ChatUsageBean(
    @SerializedName("prompt_tokens")
    val promptTokens : Int,
    @SerializedName("completion_tokens")
    val completionTokens : Int,
    @SerializedName("total_tokens")
    val totalTokens : Int
)

data class ChatChoice(
    val message: ChatMsg,
    val index : Int,
    @SerializedName("finish_reason")
    val finishReason : String
)

data class ChatMsg(
    val role : String,
    val content : String
)

