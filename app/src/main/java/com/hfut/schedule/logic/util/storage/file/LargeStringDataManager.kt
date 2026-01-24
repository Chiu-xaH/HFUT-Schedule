package com.hfut.schedule.logic.util.storage.file

import androidx.core.content.edit
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.parse.SemesterParser
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val CACHE_DIR_NAME = "data_cache"

object LargeStringDataManager : LargeStringDataStore(CACHE_DIR_NAME, MyApplication.context) {
    private const val DATUM_HEAD = "datum_"
    const val PROGRAM = "program"
    const val PROGRAM_PERFORMANCE = "program_performance"
    const val PHOTO = "jxglstu_photo"
    // 待迁移大文本 不是特别大 可慢慢迁移
    const val EXAM = "exam"//examJXGLSTU 大
    private const val COURSES_HEAD = "courses_"//courses 大
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
    // 新增
    const val GRADE = "grade"
    private const val BOOK_INFO_HEAD = "book_"
    const val XWX_USER_INFO = "xwx_user_info"
    private const val UNI_APP_COURSES_HEAD = "uni_app_courses_"
    const val UNI_APP_EXAMS = "uni_app_exams"

    fun getTotalCoursesKey(semester: Int) = "$COURSES_HEAD$semester"
    fun getBookKey(semester: Int) = "$BOOK_INFO_HEAD$semester"
    fun getJxglstuDatumKey(semester: Int) = "$DATUM_HEAD$semester"
    fun getUniAppCoursesKey(semester: Int) = "$UNI_APP_COURSES_HEAD$semester"

    // 迁移函数 从SharePrefs迁移到这里并删除
    private suspend fun moveFromPrefs(oldKey : String, newKey : String) {
        withContext(Dispatchers.IO) {
            val content = SharedPrefs.prefs.getString(oldKey, null) ?: return@withContext
            save(newKey, content)
            // 删除原有
            SharedPrefs.prefs.edit { remove(oldKey) }
        }
    }

    suspend fun moveLargeJson() {
        withContext(Dispatchers.IO) {
            launch {
                // 培养方案
                moveFromPrefs("program", PROGRAM)
            }
            launch {
                // 培养方案完成情况
                moveFromPrefs("PROGRAM_PERFORMANCE", PROGRAM_PERFORMANCE)
            }
            launch {
                // 学籍照
                moveFromPrefs("photo", PHOTO)
            }
            // 课程表
            launch {
                val key = getJxglstuDatumKey(SemesterParser.getSemester())
                // 课程表v1 迁移
                moveFromPrefs("json",key)
                // 课程表v2 支持多学期
                move("datum",key)
            }
            launch {
                // 新架构课程表 支持多学期
                move("uni_app_courses",getUniAppCoursesKey(SemesterParser.getSemester()))
            }
            launch {
                // 考试
                moveFromPrefs("examJXGLSTU",EXAM)
            }
            launch {
                // 课程汇总
                moveFromPrefs("courses",getTotalCoursesKey(SemesterParser.getSemester()))
            }
            // 移除下学期课程表功能
            launch {
                SharedPrefs.prefs.edit { remove("coursesNext") }
            }
            launch {
                SharedPrefs.prefs.edit { remove("jsonNext") }
            }
            launch moveBook@ {
                val content = DataStoreManager.courseBookJson.first()
                if(content.isEmpty() || content.isBlank()) {
                    return@moveBook
                }
                launch {
                    save(getBookKey(SemesterParser.getSemester()),content)
                }
                launch {
                    // 删除
                    DataStoreManager.saveCourseBook("")
                }
            }
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