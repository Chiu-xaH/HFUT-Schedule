package com.hfut.schedule.logic.model.dto

import com.hfut.schedule.network.api.model.response.json.zhijian.ZhiJianCourseTable
import com.hfut.schedule.network.api.model.response.json.zhijian.ZhiJianCourseTableDto
import com.hfut.schedule.ui.component.icon.filterDepartmentName
import com.hfut.schedule.ui.screen.home.calendar.common.parseSingleChineseDigit
import com.hfut.schedule.ui.screen.home.calendar.common.simplifyPlace
import com.xah.common.logic.util.LogUtil

fun ZhiJianCourseTable.toDto() : ZhiJianCourseTableDto? =
    try {
        val start = startPeriod.toInt()
        val end = start + period.toInt() - 1
        ZhiJianCourseTableDto(
            courseName = courseName,
            startPeriod = start,
            endPeriod = end,
            place = if(place == "暂无数据") null else place?.substringAfter(",")?.simplifyPlace(),
            teacher = teacher,
            department = department.filterDepartmentName(),
            classes = classes,
            date = date,
            code = code,
            type = type,
            weekday = parseSingleChineseDigit(weekday[1]),
        )
    } catch (e : Exception) {
        LogUtil.error(e)
        null
    }