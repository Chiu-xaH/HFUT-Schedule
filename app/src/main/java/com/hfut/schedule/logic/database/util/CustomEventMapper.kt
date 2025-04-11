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
    fun dtoToEntity(dto: CustomEventDTO): CustomEventEntity {
        return CustomEventEntity(
            title = dto.title,
            startTime = dto.dateTime.start.toDate(),
            endTime = dto.dateTime.end.toDate(),
            type = dto.type.name, // 用字符串保存枚举
            description = dto.description,
            remark = dto.remark
        )
    }
    @JvmStatic
    fun entityToDto(entity: CustomEventEntity): CustomEventDTO {
        return CustomEventDTO(
            id = entity.id,
            title = entity.title,
            dateTime = DateTime(
                start = entity.startTime.toDateTimeBean(),
                end = entity.endTime.toDateTimeBean()
            ),
            type = CustomEventType.valueOf(entity.type),
            description = entity.description,
            remark = entity.remark
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
