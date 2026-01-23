package com.hfut.schedule.logic.util.storage.file

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set

open class LargeStringDataStore(
    private val dirName : String,
    private val context : Context,
    private val maxCacheSize: Int = 100
) : FileStorage<String> {
//    private val memoryCache = ConcurrentHashMap<String, String>()
    private val memoryCache: LinkedHashMap<String, String> = object : LinkedHashMap<String, String>(16, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, String>?): Boolean {
            // 当缓存条目数超过最大限制时，移除最旧的条目
            return size > maxCacheSize
        }
    }
    private fun getCacheFile( key: String): File {
        // 防止非法文件名
        val safeKey = key.replace(Regex("[^A-Za-z0-9._-]"), "_")
        val dir = File(context.filesDir, dirName)
        if (!dir.exists()) dir.mkdirs()
        return File(dir, "$safeKey.cache")
    }

    /**
     * 同级之间迁移：改文件名就行
     */
    suspend fun move(oldKey: String, newKey: String): Boolean =
        withContext(Dispatchers.IO) {
            if (oldKey == newKey) return@withContext true

            val oldFile = getCacheFile(oldKey)
            if (!oldFile.exists()) return@withContext false

            val newFile = getCacheFile(newKey)

            // 如果目标已存在，先删除（覆盖语义）
            if (newFile.exists()) {
                newFile.delete()
            }

            val success = oldFile.renameTo(newFile)

            if (success) {
                // 同步更新内存缓存
                val value = memoryCache.remove(oldKey)
                if (value != null) {
                    memoryCache[newKey] = value
                }
            }

            success
        }


    override suspend fun save(key: String, content: String) {
        withContext(Dispatchers.IO) {
            val file = getCacheFile(key)
            file.writeText(content)
            // 更新缓存
            memoryCache[key] = content
        }
    }

    override suspend fun read(key: String): String? {
        // 优先读缓存
        memoryCache[key]?.let { return it }

        return withContext(Dispatchers.IO) {
            val file = getCacheFile(key)
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

    override suspend fun delete(key: String) : Boolean =
        withContext(Dispatchers.IO) {
            memoryCache.remove(key)
            val file = getCacheFile(key)
            if (file.exists()) {
                file.delete()
            } else {
                true
            }
        }
}
