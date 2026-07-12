package com.hfut.schedule.ui.screen.home.search.function.jxglstu.exam

import com.hfut.schedule.logic.database.repository.ExamHistoryRepository
import com.hfut.schedule.logic.model.JxglstuExam
import com.hfut.schedule.logic.model.community.ExamResponse
import com.hfut.schedule.logic.model.community.examArrangementList
import com.hfut.schedule.logic.network.repo.JxglstuRepository.parseJxglstuExam
import com.hfut.schedule.logic.network.repo.UniAppRepository.parseExams
import com.hfut.schedule.logic.util.parse.SemesterParser
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.network.util.GsonInstance
import com.xah.common.logic.util.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

// 废弃
fun getExam() : List<examArrangementList> {
    val json = prefs.getString("Exam","")
    try {
        val result = GsonInstance.fromJson(json,ExamResponse::class.java)
        val list = result.result.examArrangementList
        return list
    } catch (e:Exception) {
        LogUtil.error(e)
        return emptyList()
    }
}

//@SuppressLint("SuspiciousIndentation")
//fun getNewExam() : MutableList<examArrangementList> {
//    val json = prefs.getString("Exam","")
//    val AddExam = mutableListOf<examArrangementList>()
//    try {
//        val result = GsonInstance.fromJson(json,ExamResponse::class.java)
//        val list = result.result.examArrangementList
//        val date = DateTimeManager.Date_yyyy_MM_dd
//        val todaydate = date.substring(0, 4) + date.substring(5, 7) + date.substring(8, 10)
//
//
//        for (i in list.indices) {
//            val name = list[i].courseName
//            val place = list[i].place
//            val st = list[i].formatStartTime
//            val get = list[i].formatEndTime
//            //判断考完试不显示信息
//            val examdate = (get?.substring(0,4)+ get?.substring(5, 7) ) + get?.substring(8, 10)
//            if(examdate.toInt() >= todaydate.toInt())
//                AddExam.add(examArrangementList(name,place,st,get))
//        }
//        return AddExam
//    } catch (e : Exception) {
//        return AddExam
//    }
//}

suspend fun getExamFromCache(
    semester: Int = SemesterParser.getLatestSemester()
) : List<JxglstuExam> = withContext(Dispatchers.IO) {
    // 优先从Room数据库读取
    try {
        val roomExams = ExamHistoryRepository.getExams(semester)
        if (roomExams.isNotEmpty()) {
            return@withContext roomExams
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        LogUtil.error(e, "从Room读取考试记录失败，回退到缓存")
    }

    // 旧文件缓存没有学期维度，只能作为当前学期首次迁移时的回退数据。
    if (semester != SemesterParser.getLatestSemester()) {
        return@withContext emptyList()
    }

    // 如果Room没有数据，从缓存读取并解析
    val jxglstuDeferred = async {
        val html = LargeStringDataManager.read(LargeStringDataManager.EXAM) ?: return@async emptyList()
        try {
            parseJxglstuExam(html)
        } catch (e : Exception) {
            LogUtil.error(e)
            emptyList()
        }
    }
    val uniAppDeferred = async {
        val json = LargeStringDataManager.read(LargeStringDataManager.UNI_APP_EXAMS) ?: return@async emptyList()
        try {
            parseExams(json)
        } catch (e : Exception) {
            LogUtil.error(e)
            emptyList()
        }
    }

    val jxglstuExams = jxglstuDeferred.await()
    val uniAppExams = uniAppDeferred.await()

    try {
        ExamHistoryRepository.saveExamSnapshot(uniAppExams, "uniapp", semester)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        LogUtil.error(e, "保存 UniApp 考试记录到Room失败")
    }

    // 合并并去重 优先保留信息多的，
    val mergedExams = try {
        (jxglstuExams + uniAppExams).groupBy { it.name to it.dateTime }
            .map { entry ->
                // 对每组数据按优先级（优先保留有place和type的）进行合并
                entry.value.maxByOrNull {
                    // 根据 `place` 和 `type` 字段的存在性来决定优先级
                    // 优先保留有更多非空字段的项
                    val placeCount = if (it.place != null) 1 else 0
                    val typeCount = if (it.type != null) 1 else 0
                    placeCount + typeCount
                } ?: entry.value.first() // 如果没有更多信息的项，默认保留第一个
            }
    } catch (e : Exception) {
        LogUtil.error(e)
        jxglstuExams
    }

    mergedExams
}
