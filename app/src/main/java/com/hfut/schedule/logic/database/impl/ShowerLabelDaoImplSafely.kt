package com.hfut.schedule.logic.database.impl

import com.hfut.schedule.logic.database.dao.ShowerLabelDao
import com.hfut.schedule.logic.database.entity.ShowerLabelEntity

class ShowerLabelDaoImplSafely : ShowerLabelDao {
    override suspend fun getAll(): List<ShowerLabelEntity> = emptyList()
    override suspend fun del(id: Int): Int = -1
    override suspend fun insert(showerLabel: ShowerLabelEntity) = Unit
}