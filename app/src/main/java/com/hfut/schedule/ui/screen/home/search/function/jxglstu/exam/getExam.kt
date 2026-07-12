package com.hfut.schedule.ui.screen.home.search.function.jxglstu.exam

import com.hfut.schedule.logic.database.repository.ExamHistoryRepository
import com.hfut.schedule.logic.model.JxglstuExam
import com.hfut.schedule.logic.model.community.ExamResponse
import com.hfut.schedule.logic.model.community.examArrangementList
import com.hfut.schedule.logic.util.parse.SemesterParser
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.network.util.GsonInstance
import com.xah.common.logic.util.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
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
        LogUtil.error(e, "从Room读取考试记录失败")
    }

    // 旧文件缓存没有用户维度，不能安全归属给当前账号。
    emptyList()
}
