package com.hfut.schedule.logic.database.util

import com.hfut.schedule.logic.database.entity.CustomEventDTO
import com.hfut.schedule.logic.database.entity.CustomEventEntity
import com.hfut.schedule.logic.database.entity.CustomEventType
import com.hfut.schedule.logic.util.sys.DateTime
import com.hfut.schedule.logic.util.sys.DateTimeBean
import java.util.Calendar
import java.util.Date

object CustomEventMapper {
    @JvmStatic
    fun dtoToEntity(dto: CustomEventDTO) = with(dto) {
        CustomEventEntity(
            title = title,
            startTime = dateTime.start.toDate(),
            endTime = dateTime.end.toDate(),
            type = type.name,
            description = description,
            remark = remark,
//            createTime = createTime.toDate(),
            supabaseId = supabaseId
        )
    }
    @JvmStatic
    fun entityToDto(entity: CustomEventEntity) = with(entity) {
        CustomEventDTO(
            id = id,
            title = title,
            dateTime = DateTime(
                start = startTime.toDateTimeBean(),
                end = endTime.toDateTimeBean()
            ),
            type = CustomEventType.valueOf(type),
            description = description,
            remark = remark,
//            createTime = createTime.toDateTimeBean(),
            supabaseId = supabaseId
        )
    }
    // DateTimeBean -> Date
    @JvmStatic
    private fun DateTimeBean.toDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, day, hour, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }
    // Date -> DateTimeBean
    @JvmStatic
    private fun Date.toDateTimeBean(): DateTimeBean {
        val calendar = Calendar.getInstance()
        calendar.time = this
        return DateTimeBean(
            year = calendar.get(Calendar.YEAR),
            month = calendar.get(Calendar.MONTH) + 1,
            day = calendar.get(Calendar.DAY_OF_MONTH),
            hour = calendar.get(Calendar.HOUR_OF_DAY),
            minute = calendar.get(Calendar.MINUTE)
        )
    }
}

