package com.hfut.schedule.logic.database.impl

import com.hfut.schedule.logic.database.dao.SpecialWorkDayDao
import com.hfut.schedule.logic.database.entity.SpecialWorkDayEntity

class SpecialWorkDayDaoImplSafely : SpecialWorkDayDao {
    override suspend fun getAll(): List<SpecialWorkDayEntity> = emptyList()
    override suspend fun delete(originDate: String): Int = -1
    override suspend fun insert(entity: SpecialWorkDayEntity) = Unit
    override suspend fun search(originDate: String): SpecialWorkDayEntity? = null
    override suspend fun searchToday(originDate: String): String? = null
    override suspend fun searchTomorrow(originDate: String): String? = null
}