package com.hfut.schedule.logic.util.storage.file

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set

open class LargeStringDataStore(private val dirName : String) : FileStorage<String> {
    private val memoryCache = ConcurrentHashMap<String, String>()

    private fun getCacheFile(context: Context, key: String): File {
        // 防止非法文件名
        val safeKey = key.replace(Regex("[^A-Za-z0-9._-]"), "_")
        val dir = File(context.filesDir, dirName)
        if (!dir.exists()) dir.mkdirs()
        return File(dir, "$safeKey.cache")
    }

    override suspend fun save(context: Context, key: String, content: String) {
        withContext(Dispatchers.IO) {
            val file = getCacheFile(context, key)
            file.writeText(content)
            // 更新缓存
            memoryCache[key] = content
        }
    }

    override suspend fun read(context: Context, key: String): String? {
        // 优先读缓存
        memoryCache[key]?.let { return it }

        return withContext(Dispatchers.IO) {
            val file = getCacheFile(context, key)
            if (file.exists()) {
                val text = file.readText()
                // 放置缓存
                memoryCache[key] = text
                text
            } else {
                null
            }
        }
    }

    override suspend fun delete(context: Context, key: String) : Boolean =
        withContext(Dispatchers.IO) {
            memoryCache.remove(key)
            val file = getCacheFile(context, key)
            if (file.exists()) {
                file.delete()
            } else {
                true
            }
        }
}
