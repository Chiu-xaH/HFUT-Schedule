package com.hfut.schedule.logic.model.enumeration

enum class ChatModel(val url : String) {
    DeepSeek("https://api.deepseek.com/"),
    ChatAnywhere("https://api.chatanywhere.tech/v1/"),
}


enum class DeepSeekModel(val modelName : String) {
    FLASH("deepseek-flash"),
    PRO("deepseek-v4-pro")
}

enum class ChatRole {
    ASSISTANT,USER,SYSTEM
}