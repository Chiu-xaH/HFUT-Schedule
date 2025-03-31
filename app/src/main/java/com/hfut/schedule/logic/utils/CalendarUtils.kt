package com.hfut.schedule.logic.utils

import android.app.Activity
import android.content.ContentUris
import android.content.ContentValues
import android.provider.CalendarContract
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.beans.jxglstu.datumResponse
import com.hfut.schedule.logic.utils.PermissionManager.checkAndRequestCalendarPermission
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.ui.utils.components.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.YearMonth
import java.util.Calendar

// 是否存在日程
private fun isExistEvent(dateTime: DateTime, title: String): Long? {
    val resolver = MyApplication.context.contentResolver

    val projection = arrayOf(CalendarContract.Events._ID)
    val selection = """
        ${CalendarContract.Events.TITLE} = ? 
        AND ${CalendarContract.Events.DTSTART} BETWEEN ? AND ?
        AND ${CalendarContract.Events.CALENDAR_ID} = ?
    """.trimIndent()

    val beginTime = Calendar.getInstance().apply {
        set(dateTime.start.year, dateTime.start.month - 1, dateTime.start.day, dateTime.start.hour, dateTime.start.minute)
    }

    val selectionArgs = arrayOf(
        title,
        (beginTime.timeInMillis - 60000).toString(), // 放宽 1 分钟范围
        (beginTime.timeInMillis + 60000).toString(),
        "1" // 日历 ID
    )

    try {
        resolver.query(CalendarContract.Events.CONTENT_URI, projection, selection, selectionArgs, null).use { cursor ->
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getLong(0) // 返回事件 ID
            }
        }
    } catch (e: Exception) {
        return null
    }
    return null
}

private fun checkExistEvent(dateTime: DateTime, title: String,activity: Activity,showToast: Boolean = true): Long? {
    checkAndRequestCalendarPermission(activity)
    return try {
        val result = isExistEvent(dateTime, title)
        if(result != null) {
            if(showToast) showToast("已有相同日程，未添加")
            return result
        } else {
            return null
        }
    } catch (e : Exception) {
        if(showToast) showToast("未授予读取权限，未添加")
        null
    }
}

// 添加一次性日程
private fun addEvent(
    dateTime: DateTime,
    title : String,
    place : String? = null,
    remark : String? = null,
    timeZone: String = "Asia/Shanghai",
    reminderMinutes: Int? = null, // 提前多少分钟提,
    showToast : Boolean = true
) {
    try {
        val beginTime = Calendar.getInstance().apply {
            set(dateTime.start.year, dateTime.start.month - 1, dateTime.start.day, dateTime.start.hour, dateTime.start.minute)
        }
        val endTime = Calendar.getInstance().apply {
            set(dateTime.end.year, dateTime.end.month - 1, dateTime.end.day, dateTime.end.hour, dateTime.end.minute)
        }

        val values = ContentValues().apply {
            put(CalendarContract.Events.CALENDAR_ID, 1) // 日历的ID，可以通过查询日历提供者获取
            put(CalendarContract.Events.TITLE, title) // 标题
            remark?.let { put(CalendarContract.Events.DESCRIPTION, remark) } // 备注
            put(CalendarContract.Events.DTSTART, beginTime.timeInMillis)
            put(CalendarContract.Events.DTEND, endTime.timeInMillis)
            put(CalendarContract.Events.EVENT_TIMEZONE, timeZone) // 时区
            place?.let { put(CalendarContract.Events.EVENT_LOCATION, place) } // 地点
            put(CalendarContract.Events.HAS_ALARM, if(reminderMinutes != null) 1 else 0) // 允许提醒
        }

        val resolver = MyApplication.context.contentResolver
        val eventUri = resolver.insert(CalendarContract.Events.CONTENT_URI, values)
        // **获取事件ID**
        val eventId = eventUri?.lastPathSegment?.toLongOrNull()
        if (eventId != null && reminderMinutes != null) {
            // **插入提醒**
            val reminderValues = ContentValues().apply {
                put(CalendarContract.Reminders.EVENT_ID, eventId)
                put(CalendarContract.Reminders.MINUTES, reminderMinutes) // 提前提醒的时间（分钟）
                put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT) // 提醒方式：通知
            }
            resolver.insert(CalendarContract.Reminders.CONTENT_URI, reminderValues)
            if(showToast) showToast("添加成功(日程开始前的${reminderMinutes}分钟后提醒)")
        } else {
            if(showToast) showToast("添加成功(无提醒)")
        }
    } catch (e:Exception) {
        e.printStackTrace()
        if(showToast) showToast("添加失败(可能是权限问题)")
    }
}

fun addToCalendars(
    start : List<Int>,
    end : List<Int>,
    place : String? = null,
    title : String,
    remark : String? = null,
    activity : Activity
) {
    checkAndRequestCalendarPermission(activity)
    val bean = parseDateTimeBean(start, end)
    bean?.let {
        // **先检查是否已存在相同日程**
        if (checkExistEvent(bean, title,activity) != null) {
            return
        }
        addEvent(dateTime = bean, title = title,place = place, remark = remark)
    } ?: showToast("解析日期时间错误")
}

// 添加重复日程
enum class RepeatFrequency {
    DAILY, WEEKLY, MONTHLY, YEARLY
}


fun addRecurringEvent(
    dateTime: DateTime,
    title: String,
    place: String? = null,
    remark: String? = null,
    timeZone: String = "Asia/Shanghai",
    reminderMinutes: Int? = null, // 为空时不添加提醒
    repeatFrequency: RepeatFrequency, // 频率：DAILY, WEEKLY, MONTHLY, YEARLY
    interval: Int = 1, // 每 interval 天/周/月/年重复一次（默认为 1，即每次都重复）
    repeatUntil: DateTime? = null // 为空时无限重复
) = try {
    val beginTime = Calendar.getInstance().apply {
        set(dateTime.start.year, dateTime.start.month - 1, dateTime.start.day, dateTime.start.hour, dateTime.start.minute)
    }
    val endTime = Calendar.getInstance().apply {
        set(dateTime.end.year, dateTime.end.month - 1, dateTime.end.day, dateTime.end.hour, dateTime.end.minute)
    }

    val values = ContentValues().apply {
        put(CalendarContract.Events.CALENDAR_ID, 1) // 日历ID，需根据设备情况修改
        put(CalendarContract.Events.TITLE, title) // 标题
        remark?.let { put(CalendarContract.Events.DESCRIPTION, it) } // 备注
        put(CalendarContract.Events.DTSTART, beginTime.timeInMillis)
        put(CalendarContract.Events.DTEND, endTime.timeInMillis)
        put(CalendarContract.Events.EVENT_TIMEZONE, timeZone) // 时区
        place?.let { put(CalendarContract.Events.EVENT_LOCATION, it) } // 地点
        put(CalendarContract.Events.HAS_ALARM, if (reminderMinutes != null) 1 else 0) // 是否有提醒

        // **设置重复规则**
        val rrule = buildString {
            append("FREQ=${repeatFrequency.name};INTERVAL=$interval") // 例如 FREQ=WEEKLY;INTERVAL=2 表示每两周一次
            repeatUntil?.let {
                val until = String.format("%04d%02d%02dT235959Z", it.end.year, it.end.month, it.end.day)
                append(";UNTIL=$until") // 设置重复结束时间
            }
        }
        put(CalendarContract.Events.RRULE, rrule) // 规则示例：FREQ=WEEKLY;INTERVAL=1;UNTIL=20250630T235959Z
    }

    val resolver = MyApplication.context.contentResolver
    val eventUri = resolver.insert(CalendarContract.Events.CONTENT_URI, values)

    // **获取事件ID**
    val eventId = eventUri?.lastPathSegment?.toLongOrNull()
    if (eventId != null && reminderMinutes != null) {
        // **插入提醒**
        val reminderValues = ContentValues().apply {
            put(CalendarContract.Reminders.EVENT_ID, eventId)
            put(CalendarContract.Reminders.MINUTES, reminderMinutes) // 提前提醒的时间（分钟）
            put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT) // 提醒方式：通知
        }
        resolver.insert(CalendarContract.Reminders.CONTENT_URI, reminderValues)
        showToast("添加成功(日程开始前的${reminderMinutes}分钟后提醒)")
    } else {
        showToast("添加成功(无提醒)")
    }
} catch (e: Exception) {
    e.printStackTrace()
    showToast("添加失败(可能是权限问题)")
}

data class DateTime(val start : DateTimeBean,val end : DateTimeBean)

data class DateTimeBean(val year : Int,val month : Int,val day : Int,val hour : Int,val minute : Int = 0) {
    init {
        require(year > 1900) { "年份必须大于1900" }
        require(month in 1..12) { "月份必须在1到12之间" }
        require(hour in 0..23) { "小时必须在0到23之间" }
        require(minute in 0..59) { "分钟必须在0到59之间" }
        // 计算该年月的最大天数
        val maxDays = YearMonth.of(year, month).lengthOfMonth()
        require(day in 1..maxDays) { "日期必须在1到${maxDays}之间" }
    }
}

private fun parseDateTimeBean(start : List<Int>, end : List<Int>) : DateTime? =
    if(start.size == end.size && start.size == 5) {
        try {
            DateTime(
                DateTimeBean(start[0],start[1],start[2],start[3],start[4]),
                DateTimeBean(end[0],end[1],end[2],end[3],end[4])
            )
        } catch (e : Exception) { null }
    } else {
        null
    }

suspend fun addCourseToEvent(activity: Activity,time : Int)  = withContext(Dispatchers.IO) {
    async { checkAndRequestCalendarPermission(activity) }.await()
    launch {
        val json = prefs.getString("json", "")
        try {
            val datumResponse = Gson().fromJson(json, datumResponse::class.java)
            val scheduleList = datumResponse.result.scheduleList
            val lessonList = datumResponse.result.lessonList
            for (i in scheduleList.indices) {
                val item = scheduleList[i]
                var startTime = item.startTime.toString()
                startTime = startTime.substring(0, startTime.length - 2) + ":" + startTime.substring(startTime.length - 2)

                var endTime = item.endTime.toString()
                endTime = endTime.substring(0, endTime.length - 2) + ":" + endTime.substring(endTime.length - 2)
                val room = item.room.nameZh
                var courseId = item.lessonId.toString()

                for (j in lessonList.indices) {
                    if (courseId == lessonList[j].id) {
                        courseId = lessonList[j].courseName
                    }
                }

                val date = item.date.split("-")
                if(date.size != 3) {
                    break
                }
                val start = startTime.split(":")
                if(start.size != 2) {
                    break
                }
                val end = endTime.split(":")
                if(end.size != 2) {
                    break
                }

                val bean = DateTime(
                    DateTimeBean(date[0].toInt(),date[1].toInt(),date[2].toInt(),start[0].toInt(),start[1].toInt()),
                    DateTimeBean(date[0].toInt(),date[1].toInt(),date[2].toInt(),end[0].toInt(),end[1].toInt())
                )
                if (checkExistEvent(bean, courseId,activity,showToast = false) != null) {
                    break
                }
                addEvent(dateTime = bean, title = courseId,place = room, reminderMinutes = time, showToast = false)
            }
            showToast("执行完成 请检查日历")
        } catch (e : Exception) {
            showToast("失败 可能是代码错了或者权限问题")
        }
    }
}


suspend fun delCourseEvents(activity: Activity)  = withContext(Dispatchers.IO) {
    async { checkAndRequestCalendarPermission(activity) }.await()
    launch {
        val json = prefs.getString("json", "")
        try {
            val datumResponse = Gson().fromJson(json, datumResponse::class.java)
            val scheduleList = datumResponse.result.scheduleList
            val lessonList = datumResponse.result.lessonList
            for (i in scheduleList.indices) {
                val item = scheduleList[i]
                var startTime = item.startTime.toString()
                startTime = startTime.substring(0, startTime.length - 2) + ":" + startTime.substring(startTime.length - 2)

                var endTime = item.endTime.toString()
                endTime = endTime.substring(0, endTime.length - 2) + ":" + endTime.substring(endTime.length - 2)
                var courseId = item.lessonId.toString()

                for (j in lessonList.indices) {
                    if (courseId == lessonList[j].id) {
                        courseId = lessonList[j].courseName
                    }
                }

                val date = item.date.split("-")
                if(date.size != 3) {
                    break
                }
                val start = startTime.split(":")
                if(start.size != 2) {
                    break
                }
                val end = endTime.split(":")
                if(end.size != 2) {
                    break
                }

                val bean = DateTime(
                    DateTimeBean(date[0].toInt(),date[1].toInt(),date[2].toInt(),start[0].toInt(),start[1].toInt()),
                    DateTimeBean(date[0].toInt(),date[1].toInt(),date[2].toInt(),end[0].toInt(),end[1].toInt())
                )
                checkExistEvent(bean, courseId,activity,showToast = false)?.let {
                    delEvent(id = it, showToast = false)
                } ?: break
            }
            showToast("执行完成 请检查日历")
        } catch (e : Exception) {
            showToast("失败 可能是代码错了或者权限问题")
        }
    }
}


private fun delEvent(id : Long,showToast: Boolean = true) : Boolean {
    val resolver = MyApplication.context.contentResolver
    return try {
        val deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, id)
        val rows = resolver.delete(deleteUri, null, null)
        if (rows > 0) {
            if(showToast) showToast("日程删除成功")
            true
        } else {
            if(showToast) showToast("删除失败")
            false
        }
    } catch (e: Exception) {
        if(showToast) showToast("删除异常")
        false
    }
}


