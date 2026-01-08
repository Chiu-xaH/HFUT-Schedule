package com.hfut.schedule.ui.screen.home.search.function.jxglstu.exam

import android.annotation.SuppressLint
import com.google.gson.Gson
import com.hfut.schedule.logic.model.community.ExamResponse
import com.hfut.schedule.logic.model.community.examArrangementList
import com.hfut.schedule.logic.model.uniapp.UniAppExamResponse
import com.hfut.schedule.logic.network.repo.hfut.JxglstuRepository.parseJxglstuExam
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.parseJxglstuIntTime
import com.xah.uicommon.util.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

// 废弃
fun getExam() : List<examArrangementList> {
    val json = prefs.getString("Exam","")
    try {
        val result = Gson().fromJson(json,ExamResponse::class.java)
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
//        val result = Gson().fromJson(json,ExamResponse::class.java)
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

data class JxglstuExam(
    val name : String,
    val dateTime : String,
    val place : String?,
    val type : String? = null
)


suspend fun getExamFromCache() : List<JxglstuExam> = withContext(Dispatchers.IO) {
    //考试解析
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
            val list = Gson().fromJson(json, UniAppExamResponse::class.java).data
            list.mapNotNull {
                // YYYY-MM-DD HH:MM~HH-MM
                val startTime = parseJxglstuIntTime(it.startTime)
                val endTime = parseJxglstuIntTime(it.endTime)
                val dateTime = "${it.examDate} ${startTime}~${endTime}"
                if(isValidDateTime(dateTime)) {
                    JxglstuExam(
                        name = it.courseNameZh,
                        dateTime = dateTime,
                        place = it.place.let { p ->
                            if(p?.contains(" ") == true) {
                                p.split(" ").last()
                            } else {
                                p
                            }
                        },
                        type = it.examType.nameZh
                    )
                } else {
                    null
                }
            }
        } catch (e : Exception) {
            LogUtil.error(e)
            emptyList()
        }
    }

    val jxglstuExams = jxglstuDeferred.await()
    val uniAppExams = uniAppDeferred.await()

    // 合并并去重 优先保留信息多的，
    try {
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
}


//[日期时间]的格式必须是 YYYY-MM-DD HH:MM~HH-MM
fun isValidDateTime(str : String) : Boolean {
    val regex = Regex("""\d{4}-\d{2}-\d{2} \d{2}:\d{2}~\d{2}:\d{2}""")
    if (!regex.matches(str)) {
        return false
    }

    return try {
        val parts = str.split(" ")
        val datePart = parts[0]
        val timeRange = parts[1].split("~")
        val timeStart = timeRange[0]
        val timeEnd = timeRange[1]

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        // 验证日期和时间部分是否有效
        dateFormat.parse(datePart)
        timeFormat.parse(timeStart)
        timeFormat.parse(timeEnd)
        true
    } catch (e: Exception) {
        false
    }
}