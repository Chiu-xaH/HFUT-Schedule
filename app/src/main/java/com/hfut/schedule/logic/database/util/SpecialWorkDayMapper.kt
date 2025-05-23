package com.hfut.schedule.logic.database.util

import com.hfut.schedule.logic.database.dao.SpecialWorkDayDao
import com.hfut.schedule.logic.database.entity.SpecialWorkDayEntity

suspend fun SpecialWorkDayDao.insertSafely(originDate : String,targetDate : String) {
    // 符合YYYY-MM-DD格式
    val dateRegex = Regex("""\d{4}-\d{2}-\d{2}""")
    if (!dateRegex.matches(originDate)) {
        throw IllegalArgumentException("originDate 格式不合法，应为 YYYY-MM-DD")
    }
    this.insert(SpecialWorkDayEntity(originDate = originDate, targetDate = targetDate))
}