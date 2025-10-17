package com.hfut.schedule.logic.util.storage.file

import android.content.Context

// 持久化存储大文本 JSON/HTML/XML等
interface FileStorage<T> {
    // 保存/更新
    suspend fun save(context: Context, key: String, content: T)
    // 读取
    suspend fun read(context: Context, key: String): T?
    // 删除
    suspend fun delete(context: Context, key: String): Boolean
}