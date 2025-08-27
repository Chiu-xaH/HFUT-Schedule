package com.hfut.schedule.logic.util.sys

import android.app.Activity
import android.content.ContentUris
import android.content.ContentValues
import android.provider.CalendarContract
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.model.jxglstu.DatumResponse
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.sys.PermissionSet.checkAndRequestCalendarPermission
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.viewmodel.ui.UIViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneOffset
import java.util.Calendar

// 是否存在日程
private suspend fun isExistEvent(dateTime: DateTime, title: String): Long? = withContext(Dispatchers.IO) {
    try {
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
        val id = DataStoreManager.defaultCalendarAccountId.first()
        val selectionArgs = arrayOf(
            title,
            (beginTime.timeInMillis - 60000).toString(), // 放宽 1 分钟范围
            (beginTime.timeInMillis + 60000).toString(),
            "$id"
        )
        resolver.query(CalendarContract.Events.CONTENT_URI, projection, selection, selectionArgs, null).use { cursor ->
            if (cursor != null && cursor.moveToFirst()) {
                return@withContext cursor.getLong(0) // 返回事件 ID
            }
        }
    } catch (e: Exception) {
        return@withContext null
    }
    return@withContext null
}

private suspend fun checkExistEvent(dateTime: DateTime, title: String, activity: Activity, showToast: Boolean = true): Long? {
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
private suspend fun addEvent(
    dateTime: DateTime,
    title : String,
    place : String? = null,
    remark : String? = null,
    timeZone: String = "Asia/Shanghai",
    reminderMinutes: Int? = null, // 提前多少分钟提,
    showToast : Boolean = true
) = withContext(Dispatchers.IO) {
    try {
        val beginTime = Calendar.getInstance().apply {
            set(dateTime.start.year, dateTime.start.month - 1, dateTime.start.day, dateTime.start.hour, dateTime.start.minute)
        }
        val endTime = Calendar.getInstance().apply {
            set(dateTime.end.year, dateTime.end.month - 1, dateTime.end.day, dateTime.end.hour, dateTime.end.minute)
        }

        val values = ContentValues().apply {
            val id = DataStoreManager.defaultCalendarAccountId.first()
            put(CalendarContract.Events.CALENDAR_ID,id ) // 日历的ID，可以通过查询日历提供者获取
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
// 使用规范的时间bean 推荐
suspend fun addToCalendars(
    bean: DateTime,
    place : String? = null,
    title : String,
    remark : String? = null,
    activity : Activity,
    remind : Boolean = false
) {
    checkAndRequestCalendarPermission(activity)
    bean.let {
        // **先检查是否已存在相同日程**
        if (checkExistEvent(it, title,activity) != null) {
            return
        }
        addEvent(dateTime = it, title = title,place = place, remark = remark, reminderMinutes = if(remind) 15 else null)
    }
}
// 传入标准格式化的YYYY-MM-DD与HH:SS
suspend fun addToCalendars(
    startDate : String,
    startTime : String,
    endDate : String,
    endTime : String,
    place : String? = null,
    title : String,
    remark : String? = null,
    activity : Activity
) {
    checkAndRequestCalendarPermission(activity)
    // 解析
    val bean = parseToDateTime(startDate, startTime, endDate, endTime)
    bean?.let {
        // **先检查是否已存在相同日程**
        if (checkExistEvent(it, title,activity) != null) {
            return
        }
        addEvent(dateTime = it, title = title,place = place, remark = remark)
    }
}
// 旧方法改造
suspend fun addToCalendars(
    start : List<Int>,
    end : List<Int>,
    place : String? = null,
    title : String,
    remark : String? = null,
    activity : Activity,
    remind : Boolean = false
) {
    checkAndRequestCalendarPermission(activity)
    val bean = parseDateTimeBean(start, end)
    bean?.let {
        // **先检查是否已存在相同日程**
        if (checkExistEvent(it, title,activity) != null) {
            return
        }
        addEvent(dateTime = it, title = title,place = place, remark = remark, reminderMinutes = if(remind) 15 else null)
    } ?: showToast("解析日期时间错误")
}

// 添加重复日程
enum class RepeatFrequency {
    DAILY, WEEKLY, MONTHLY, YEARLY
}

data class DateTime(val start : DateTimeBean, val end : DateTimeBean)

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

fun parseToDateTime(startDate: String,startTime: String,endDate: String,endTime: String) : DateTime? {
    val startBean = parseStrToDateTimeBean(startDate,startTime) ?: return null
    val endBean = parseStrToDateTimeBean(endDate,endTime) ?: return null
    return DateTime(startBean,endBean)
}

// 将时间转换为标准时间 例如东八区->0时区
fun DateTimeBean.toUTC(offsetHours: Int = 8): DateTimeBean {
    // 构造本地时区的 LocalDateTime
    val localDateTime = LocalDateTime.of(year, month, day, hour, minute)

    // 假设当前时间是东X区（默认东八区）
    val zoneOffset = ZoneOffset.ofHours(offsetHours)
    val zonedDateTime = localDateTime.atOffset(zoneOffset)

    // 转换为 UTC
    val utcDateTime = zonedDateTime.withOffsetSameInstant(ZoneOffset.UTC)

    // 构造并返回转换后的 DateTimeBean
    return DateTimeBean(
        year = utcDateTime.year,
        month = utcDateTime.monthValue,
        day = utcDateTime.dayOfMonth,
        hour = utcDateTime.hour,
        minute = utcDateTime.minute
    )
}

fun DateTimeBean.fromUTC(offsetHours: Int = 8): DateTimeBean = toUTC(-offsetHours)

fun parseStrToDateTimeBean(date: String, time: String) : DateTimeBean? {
    val dates = date.split("-")
    if(dates.size != 3) return null
    val year = dates[0].toIntOrNull() ?: return null
    val month = dates[1].toIntOrNull() ?: return null
    val day = dates[2].toIntOrNull() ?: return null
    val times = time.split(":")
    if(times.size != 2) return null
    val hour = times[0].toIntOrNull() ?: return null
    val minute = times[1].toIntOrNull() ?: return null
    return DateTimeBean(year,month,day,hour,minute)
}

data class JxglstuCourseSchedule(val time : DateTime, val place : String?, val courseName : String)

fun getJxglstuCourseSchedule(jstr : String? = null) : List<JxglstuCourseSchedule>  {
    val json = jstr ?: prefs.getString("json", "")
    val list = mutableListOf<JxglstuCourseSchedule>()
    try {
        val datumResponse = Gson().fromJson(json, DatumResponse::class.java)
        val scheduleList = datumResponse.result.scheduleList
        val lessonList = datumResponse.result.lessonList
        for (i in scheduleList.indices) {
            val item = scheduleList[i]
            var startTime = item.startTime.toString()
            startTime = startTime.substring(0, startTime.length - 2) + ":" + startTime.substring(startTime.length - 2)

            var endTime = item.endTime.toString()
            endTime = endTime.substring(0, endTime.length - 2) + ":" + endTime.substring(endTime.length - 2)
            val room = item.room?.nameZh
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
            list.add(JxglstuCourseSchedule(bean,room,courseId))
        }
    } catch (_ : Exception) { }
    return list
}

suspend fun addCourseToEvent(vmUI : UIViewModel,activity: Activity,time : Int)  = withContext(Dispatchers.IO) {
    async { checkAndRequestCalendarPermission(activity) }.await()
    launch {
        try {
            val list = vmUI.jxglstuCourseScheduleList
            for(item in list) {
                val itemTime = item.time
                val itemName = item.courseName
                if (checkExistEvent(itemTime, itemName,activity,showToast = false) != null) {
                    break
                }
                addEvent(dateTime = itemTime, title = itemName,place = item.place, reminderMinutes = time, showToast = false)
            }
            showToast("执行完成 请检查日历")
        } catch (e : Exception) {
            showToast("失败 可能是代码错了或者权限问题")
        }
    }
}


suspend fun delCourseEvents(vmUI: UIViewModel,activity: Activity)  = withContext(Dispatchers.IO) {
    async { checkAndRequestCalendarPermission(activity) }.await()
    launch {
        try {
            val list = vmUI.jxglstuCourseScheduleList
            for(item in list) {
                val itemTime = item.time
                val itemName = item.courseName
                checkExistEvent(itemTime, itemName,activity,showToast = false)?.let {
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

fun queryCalendars(): List<Pair<Long, String>> {
    val calendarList = mutableListOf<Pair<Long, String>>()
    try {
        val uri = CalendarContract.Calendars.CONTENT_URI
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
        )
        val selection = "${CalendarContract.Calendars.VISIBLE} = 1"

        val cursor = MyApplication.context.contentResolver.query(
            uri,
            projection,
            selection,
            null,
            null
        )

        cursor?.use {
            val idIndex = it.getColumnIndex(CalendarContract.Calendars._ID)
            val nameIndex = it.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME)

            while (it.moveToNext()) {
                val id = it.getLong(idIndex)
                var name = it.getString(nameIndex)
                if(id == 1L) {
                    name = "本地默认"
                }
                calendarList.add(id to name)
            }
        }

        return calendarList
    } catch (e : Exception) {
        return calendarList
    }
}



