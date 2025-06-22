package com.hfut.schedule.logic.util.network

import com.hfut.schedule.logic.database.entity.CustomEventType
import com.hfut.schedule.logic.model.SupabaseEventEntity
import com.hfut.schedule.logic.model.SupabaseEventForkEntity
import com.hfut.schedule.logic.model.SupabaseEventOutput
import com.hfut.schedule.logic.model.SupabaseEventsInput
import com.hfut.schedule.logic.util.sys.DateTime
import com.hfut.schedule.logic.util.sys.DateTimeBean
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.fromUTC
import com.hfut.schedule.logic.util.sys.toUTC
import com.hfut.schedule.ui.screen.home.focus.funiction.parseTimeItem
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.EventCampus

// 转换层
fun supabaseEventDtoToEntity(dto : SupabaseEventOutput) : SupabaseEventEntity = with(dto) {
    SupabaseEventEntity(
        name = name,
        description = description,
        timeDescription = timeDescription,
        type = type.name,
        startTime = dateTime.start.toUTC().toTimestamp(),
        endTime = dateTime.end.toUTC().toTimestamp(),
        applicableClasses = applicableClasses.joinToString(","),
        url = url,
        campus = campus.name
    )
}

fun supabaseEventEntityToDto(entity : SupabaseEventEntity) : SupabaseEventsInput? = with(entity) {
    toDateTimeBean(startTime)?.let { start ->
        toDateTimeBean(endTime)?.let { end ->
            email?.substringBefore("@")?.let { emailName -> if(isValidStudentId(emailName)) emailName else return null }?.let { studentId ->
                myClass?.let {
                    id?.let { it1 ->
                        createTime?.let { str ->
                            toDateTimeBean(str)?.let { create ->
                                SupabaseEventsInput(
                                    id = it1,
                                    name = name,
                                    description = description.let { desp -> if(desp == "NULL") null else desp },
                                    timeDescription = timeDescription,
                                    type = when (type) {
                                        CustomEventType.NET_COURSE.name -> CustomEventType.NET_COURSE
                                        CustomEventType.SCHEDULE.name -> CustomEventType.SCHEDULE
                                        // 错误
                                        else -> return null
                                    },
                                    url = url,
                                    applicableClasses = applicableClasses.split(","),
                                    dateTime = DateTime(start = start.fromUTC(), end = end.fromUTC()),
                                    contributorId = studentId,
                                    createTime = create.fromUTC(),
                                    contributorClass = it,
                                    campus = EventCampus.valueOf(campus)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun isValidStudentId(id : String) = id.length == 10

private fun DateTimeBean.toTimestamp(): String = with(this) {
    parseTimeItem(year) + "-" + parseTimeItem(month) + "-" + parseTimeItem(day) + "T" + parseTimeItem(hour) + ":" + parseTimeItem(minute) + ":00"
}
fun DateTimeBean.toTimestampWithOutT(): Long? = with(this) {
    (parseTimeItem(year) + parseTimeItem(month) + parseTimeItem(day)  + parseTimeItem(hour)  + parseTimeItem(minute)).toLongOrNull()
}

private fun toDateTimeBean(str : String): DateTimeBean? {
    try {
        val list1 = str.split("T")
        if(list1.size != 2) {
            return null
        }
        val date = list1[0]
        val time = list1[1]
        val dateList = date.split("-")
        if(dateList.size != 3) {
            return null
        }
        val year = dateList[0].toInt()
        val month = dateList[1].toInt()
        val day = dateList[2].toInt()
        val timeList = time.split(":")
        if(timeList.size != 3) {
            return null
        }
        val hour = timeList[0].toInt()
        val min = timeList[1].toInt()
        return DateTimeBean(year,month,day,hour,min)
    } catch (e : Exception) {
        return null
    }
}

private fun toDateTimeStr(str: String) : String? {
    try {
        val list1 = str.split("T")
        if(list1.size != 2) {
            return null
        }
        val date = list1[0]
        val time = list1[1]
//        val dateList = date.split("-")
//        if(dateList.size != 3) {
//            return null
//        }
        val timeList = time.split(":")
        if(timeList.size != 3) {
            return null
        }
        return date + " " + timeList[0] + ":" + timeList[1]
    } catch (e : Exception) {
        return null
    }
}

fun toDateTimeBeanForSupabase() : String = DateTimeBean(year = DateTimeManager.Date_yyyy.toInt(), month = DateTimeManager.Date_MM.toInt(), day = DateTimeManager.Date_dd.toInt(), hour = DateTimeManager.Time_Hour.toInt(), minute = DateTimeManager.Time_Minute.toInt()).toUTC().toTimestamp()

fun supabaseEventForkDtoToEntity(eventId : Int) : SupabaseEventForkEntity = SupabaseEventForkEntity(eventId = eventId)


fun DateTimeBean.toStr() : String = with(this) {
    return@with "$year-${parseTimeItem(month)}-${parseTimeItem(day)} ${parseTimeItem(hour)}:${parseTimeItem(minute)}"
}