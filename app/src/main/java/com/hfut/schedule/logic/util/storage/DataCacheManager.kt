package com.hfut.schedule.logic.util.storage

import android.content.Context
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import androidx.core.content.edit
import com.hfut.schedule.logic.util.storage.FileDataManager.CACHE_DIR_NAME
import kotlinx.coroutines.launch

// 持久化存储大文本 JSON/HTML/XML等
interface DataCache {
    // 保存/更新
    suspend fun save(context: Context, key: String, content: String)
    // 读取
    suspend fun read(context: Context, key: String): String?
    // 删除
    suspend fun delete(context: Context, key: String): Boolean
}


open class FileDataCache(private val dirName : String) : DataCache {
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


object FileDataManager : FileDataCache(CACHE_DIR_NAME) {
    private const val CACHE_DIR_NAME = "data_cache"
    const val DATUM = "datum"
    const val PROGRAM = "program"
    const val PROGRAM_PERFORMANCE = "program_performance"
    const val PHOTO = "jxglstu_photo"
    // 待迁移大文本 不是特别大 可慢慢迁移
    const val EXAM = "exam"//examJXGLSTU 大
    const val COURSES = "courses"//courses 大
    const val PERSON_INFO = "jxglstu_info"//info 大
    const val PERSON_PROFILE = "jxglstu_profile"//profile 大
    // 远期规划迁移 非常小的JSON 影响不大
    const val MY = "my" //my
    const val HOLIDAY = "holiday"//HOLIDAY
    const val WX_PERSON_INFO = "wx_person_info" //WX_PERSON_INFO
    const val PROGRAM_COMPETITION = "program_competition"//PROGRAM_COMPETITION
    const val GUA_GUA_PERSON_INFO = "guagua_person_info"//GuaguaPersonInfo
    const val FRIENDS = "community_friends" //friends
    const val COMMUNITY_COURSES = "community_courses"//Course
    const val HUI_XIN_INFO = "hui_xin_info"//card_yue

    // 迁移函数 从SharePrefs迁移到这里并删除
    private suspend fun move(context: Context,oldKey : String,newKey : String) {
        withContext(Dispatchers.IO) {
            val content = prefs.getString(oldKey,null) ?: return@withContext
            save(context,newKey,content)
            // 删除原有
            prefs.edit { remove(oldKey) }
        }
    }

    suspend fun moveLargeJson(
        context: Context
    ) {
        withContext(Dispatchers.IO) {
            launch {
                // 培养方案
                move(context,"program",PROGRAM)
            }
            launch {
                // 培养方案完成情况
                move(context,"PROGRAM_PERFORMANCE",PROGRAM_PERFORMANCE)
            }
            launch {
                // 学籍照
                move(context,"photo",PHOTO)
            }
            launch {
                // 课程表
                move(context,"json",DATUM)
            }
//            launch {
                // 考试
//                move(context,"examJXGLSTU",EXAM)
//            }
//            launch {
                // 课程汇总
//                move(context,"courses",COURSES)
//            }
//            launch {
                // 个人信息
//                move(context,"info",PERSON_INFO)
//            }
//            launch {
                // 个人信息2
//                move(context,"profile",PERSON_PROFILE)
//            }
        }
    }
}
