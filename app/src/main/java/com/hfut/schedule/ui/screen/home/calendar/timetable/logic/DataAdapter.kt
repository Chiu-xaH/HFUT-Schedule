package com.hfut.schedule.ui.screen.home.calendar.timetable.logic

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.google.gson.Gson
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.database.DataBaseManager
import com.hfut.schedule.logic.database.entity.CustomEventType
import com.hfut.schedule.logic.database.util.CustomEventMapper.entityToDto
import com.hfut.schedule.logic.model.jxglstu.DatumResponse
import com.hfut.schedule.logic.model.uniapp.UniAppCoursesResponse
import com.hfut.schedule.logic.network.util.toStr
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager.ShowTeacherConfig
import com.hfut.schedule.ui.screen.home.calendar.common.dateToWeek
import com.hfut.schedule.ui.screen.home.calendar.common.examToCalendar
import com.hfut.schedule.ui.screen.home.calendar.common.simplifyPlace
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.distinctUnit
import com.hfut.schedule.ui.screen.home.focus.funiction.parseTimeItem
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getCoursesFromCommunity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.LocalDate
import kotlin.collections.plus

// 分配20周
suspend fun allToTimeTableDataUniApp(): List<List<TimeTableItem>> = withContext(Dispatchers.Default) {
    // 并发调用三个数据源
    val jxglstuDeferred = async { uniAppToTimeTableData() }
    val focusDeferred = async { focusToTimeTableData() }
    val examDeferred = async { examToTimeTableData() }

    val jxglstuList = jxglstuDeferred.await()
    val focusList = focusDeferred.await()
    val examList = examDeferred.await()

    // 合并：按周（index）叠加三个来源
    List(MyApplication.MAX_WEEK) { i ->
        jxglstuList[i] + focusList[i]+ examList[i]
    }
}

suspend fun allToTimeTableData(friendStudentId: String?): List<List<TimeTableItem>> = withContext(Dispatchers.Default) {
    // 并发调用三个数据源
    val jxglstuDeferred = async { communityToTimeTableData(friendStudentId) }
    if(friendStudentId != null) {
        return@withContext jxglstuDeferred.await()
    }
    val focusDeferred = async { focusToTimeTableData() }
    val examDeferred = async { examToTimeTableData() }

    val jxglstuList = jxglstuDeferred.await()
    val focusList = focusDeferred.await()
    val examList = examDeferred.await()

    // 合并：按周（index）叠加三个来源
    List(MyApplication.MAX_WEEK) { i ->
        jxglstuList[i] + focusList[i]+ examList[i]
    }
}

suspend fun allToTimeTableData(): List<List<TimeTableItem>> = withContext(Dispatchers.Default) {
    // 并发调用三个数据源
    val jxglstuDeferred = async { jxglstuToTimeTableData() }
    val focusDeferred = async { focusToTimeTableData() }
    val examDeferred = async { examToTimeTableData() }

    val jxglstuList = jxglstuDeferred.await()
    val focusList = focusDeferred.await()
    val examList = examDeferred.await()

    // 合并：按周（index）叠加三个来源
    List(MyApplication.MAX_WEEK) { i ->
        jxglstuList[i] + focusList[i]+ examList[i]
    }
}

private suspend fun uniAppToTimeTableData(): List<List<TimeTableItem>> {
    val json = LargeStringDataManager.read( LargeStringDataManager.UNI_APP_COURSES)
        ?: return List(MyApplication.MAX_WEEK) { emptyList<TimeTableItem>() }
    try {
        val result = List(MyApplication.MAX_WEEK) { mutableStateListOf<TimeTableItem>() }
        val list = Gson().fromJson(json, UniAppCoursesResponse::class.java).data
        val enableCalendarShowTeacher = DataStoreManager.enableCalendarShowTeacher.first()
        for(item in list) {
            val courseName = item.course.nameZh
            val multiTeacher = item.teacherAssignmentList.size > 1
            for(schedule in item.schedules) {
                val list = result[schedule.weekIndex-1]
                val teacher = when(enableCalendarShowTeacher) {
                    ShowTeacherConfig.ALL.code -> schedule.teacherName
                    ShowTeacherConfig.ONLY_MULTI.code -> {
                        if(multiTeacher) {
                            schedule.teacherName
                        } else {
                            null
                        }
                    }
                    else -> null
                }
                list.add(
                    TimeTableItem(
                        teacher = teacher,
                        type = TimeTableType.COURSE,
                        name = courseName,
                        dayOfWeek = schedule.weekday,
                        startTime = parseJxglstuIntTime(schedule.startTime),
                        endTime = parseJxglstuIntTime(schedule.endTime),
                        place = schedule.room?.nameZh?.simplifyPlace(),
                    )
                )
            }
        }
        // 去重
        distinctUnit(result)
        return result
    } catch (e : Exception) {
        e.printStackTrace()
        return List(MyApplication.MAX_WEEK) { emptyList<TimeTableItem>() }
    }
}

private suspend fun jxglstuToTimeTableData(): List<List<TimeTableItem>> {
    val json = LargeStringDataManager.read(LargeStringDataManager.DATUM)
        ?: return List(MyApplication.MAX_WEEK) { emptyList<TimeTableItem>() }
    try {
        val result = List(MyApplication.MAX_WEEK) { mutableStateListOf<TimeTableItem>() }

        val datumResponse = Gson().fromJson(json, DatumResponse::class.java)
        val scheduleList = datumResponse.result.scheduleList
        val lessonList = datumResponse.result.lessonList
        // 根据id得到课程名
        val courseNameMap = mutableMapOf<String, String>()
        val multiTeacherMap = mutableMapOf<String, Int>()
        for(item in lessonList) {
            courseNameMap[item.id] = item.courseName
            multiTeacherMap[item.id] = item.teacherAssignmentList.size
        }
        val enableCalendarShowTeacher = DataStoreManager.enableCalendarShowTeacher.first()

        for(item in scheduleList) {
            val list = result[item.weekIndex-1]
            val teacher = when(enableCalendarShowTeacher) {
                ShowTeacherConfig.ALL.code -> item.personName
                ShowTeacherConfig.ONLY_MULTI.code -> {
                    multiTeacherMap[item.lessonId.toString()]?.let { size ->
                        if(size > 1) {
                            item.personName
                        } else {
                            null
                        }
                    }
                }
                else -> null
            }
            list.add(
                TimeTableItem(
                    teacher = teacher,
                    type = TimeTableType.COURSE,
                    name = item.lessonId.toString().let { courseNameMap[it] ?: it },
                    dayOfWeek = item.weekday,
                    startTime = parseJxglstuIntTime(item.startTime),
                    endTime = parseJxglstuIntTime(item.endTime),
                    place = item.room?.nameZh?.simplifyPlace(),
                )
            )
        }
        // 去重
        distinctUnit(result)
        return result
    } catch (e : Exception) {
        e.printStackTrace()
        return List(MyApplication.MAX_WEEK) { emptyList<TimeTableItem>() }
    }
}

private suspend fun focusToTimeTableData(): List<List<TimeTableItem>> {
    try {
        val result = List(MyApplication.MAX_WEEK) { mutableStateListOf<TimeTableItem>() }
        val focusList = DataBaseManager.customEventDao.getAll(CustomEventType.SCHEDULE.name).map {
            entityToDto(it)
        }
        for(item in focusList) {
            val start = item.dateTime.start.toStr().split(" ")
            if(start.size != 2) {
                continue
            }
            val startDate = start[0]
            val startTime = start[1]
            val weekInfo = dateToWeek(startDate) ?: continue

            val end = item.dateTime.end.toStr().split(" ")
            if(end.size != 2) {
                continue
            }
            val endDate = end[0]
            val endTime = end[1]

            // 是同一周
            val list = result[weekInfo.first-1]
            val name = item.title
            val place = item.description?.simplifyPlace()

            // 跨天日程将其分裂
            if(endDate != startDate) {
                var currentDate = LocalDate.parse(startDate)
                val endLocalDate = LocalDate.parse(endDate)

                while (!currentDate.isAfter(endLocalDate)) {
                    val isFirstDay = currentDate == LocalDate.parse(startDate)
                    val isLastDay = currentDate == endLocalDate

                    val currentWeek = dateToWeek(currentDate.toString()) ?: continue
                    val list = result[currentWeek.first - 1]

                    val currentStartTime = when {
                        isFirstDay -> startTime
                        else -> "00:00"
                    }
                    val currentEndTime = when {
                        isLastDay -> endTime
                        else -> "24:00"
                    }

                    list.add(
                        TimeTableItem(
                            type = TimeTableType.FOCUS,
                            name = name,
                            dayOfWeek = currentWeek.second,
                            startTime = currentStartTime,
                            endTime = currentEndTime,
                            place = place,
                            id = item.id
                        )
                    )
                    currentDate = currentDate.plusDays(1)
                }
            } else {
                // 同一天
                list.add(
                    TimeTableItem(
                        type = TimeTableType.FOCUS,
                        name = name,
                        dayOfWeek = weekInfo.second,
                        startTime = startTime,
                        endTime = endTime,
                        place = place,
                        id = item.id
                    )
                )
            }
        }
        return result
    } catch (e : Exception) {
        e.printStackTrace()
        return List(MyApplication.MAX_WEEK) { emptyList<TimeTableItem>() }
    }
}

private suspend fun examToTimeTableData(): List<List<TimeTableItem>> {
    try {
        val result = List(MyApplication.MAX_WEEK) { mutableStateListOf<TimeTableItem>() }
        val examList = examToCalendar()
        for (item in examList) {
            val startTime = item.startTime ?: continue
            val endTime = item.endTime ?: continue
            val startDate = item.day ?: continue
            val weekInfo = dateToWeek(startDate) ?: continue

            val list = result[weekInfo.first-1]

            val name = item.course ?: continue
            val place = item.place?.simplifyPlace()
            list.add(
                TimeTableItem(
                    type = TimeTableType.EXAM,
                    name = name + (item.type?.let { "-$it" } ?: ""),
                    dayOfWeek = weekInfo.second,
                    startTime = startTime,
                    endTime = endTime,
                    place = place,
                )
            )
        }
        return result
    } catch (e : Exception) {
        e.printStackTrace()
        return List(MyApplication.MAX_WEEK) { emptyList<TimeTableItem>() }
    }
}

private suspend fun communityToTimeTableData(friendStudentId : String? = null) : List<List<TimeTableItem>> {
    val result = List(MyApplication.MAX_WEEK) { mutableStateListOf<TimeTableItem>() }
    try {
        val enableCalendarShowTeacher = DataStoreManager.enableCalendarShowTeacher.first()

        repeat(MyApplication.MAX_WEEK) { week ->
            val originalData = getCoursesFromCommunity(targetWeek = week+1, friendUserName = friendStudentId)
            val list = result[week]
            repeat(7) { weekday ->
                val data = originalData[weekday].flatMap { it }
                for(item in data) {
                    val time = item.classTime.split("-")
                    if(time.size != 2) {
                        continue
                    }
                    val teacher = when(enableCalendarShowTeacher) {
                        ShowTeacherConfig.ALL.code -> item.teacher
                        else -> null
                    }
                    list.add(
                        TimeTableItem(
                            teacher = teacher,
                            type = TimeTableType.COURSE,
                            name = item.name,
                            dayOfWeek = weekday + 1,
                            startTime = time[0],
                            endTime = time[1],
                            place = item.place?.simplifyPlace()
                        )
                    )
                }
            }
        }
    } catch (e : Exception) {
        e.printStackTrace()
    }
    for(t in result) {
        val uniqueItems = t.distinctBy {
            it.name + it.place + it.dayOfWeek + it.startTime + it.endTime
        }
        t.clear()
        t.addAll(uniqueItems)
    }
    return result
}

fun parseJxglstuIntTime(time : Int) : String {
    val hour = time / 100
    val minute = time % 100
    return "${parseTimeItem(hour)}:${parseTimeItem(minute)}"
}
