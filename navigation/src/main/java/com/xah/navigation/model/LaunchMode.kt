package com.xah.navigation.model

enum class LaunchMode {
    STANDARD,   // 永远压入新的
    SINGLE_TOP, // 栈顶存在则复用
    SINGLE_TASK, // 栈内存在则复用并清除其顶部项
    SINGLE_INSTANCE, // 栈内存在则复用并清空其余项
    CLEAR_STACK, // 清空栈并压入
}