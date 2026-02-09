package com.hfut.schedule.logic.enumeration

enum class ChatModel(val url : String) {
    DeepSeek("https://api.deepseek.com/chat/completions"),
    ChatAnywhere("https://api.chatanywhere.tech/v1/chat/completions"),
}
