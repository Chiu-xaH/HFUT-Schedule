package com.hfut.schedule.logic.util.storage.file

import android.content.Context
import androidx.core.content.edit
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val CACHE_DIR_NAME = "data_cache"

object LargeStringDataManager : LargeStringDataStore(CACHE_DIR_NAME) {
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
    private suspend fun move(context: Context, oldKey : String, newKey : String) {
        withContext(Dispatchers.IO) {
            val content = SharedPrefs.prefs.getString(oldKey, null) ?: return@withContext
            save(context, newKey, content)
            // 删除原有
            SharedPrefs.prefs.edit { remove(oldKey) }
        }
    }

    suspend fun moveLargeJson(
        context: Context
    ) {
        withContext(Dispatchers.IO) {
            launch {
                // 培养方案
                move(context, "program", PROGRAM)
            }
            launch {
                // 培养方案完成情况
                move(context, "PROGRAM_PERFORMANCE", PROGRAM_PERFORMANCE)
            }
            launch {
                // 学籍照
                move(context, "photo", PHOTO)
            }
            launch {
                // 课程表
                move(context, "json", DATUM)
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