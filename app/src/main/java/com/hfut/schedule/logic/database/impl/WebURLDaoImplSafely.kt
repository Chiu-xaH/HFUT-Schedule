package com.hfut.schedule.logic.database.impl

import com.hfut.schedule.logic.database.dao.WebURLDao
import com.hfut.schedule.logic.database.entity.WebURLEntity

class WebURLDaoImplSafely : WebURLDao {
    override suspend fun del(id: Int): Int = -1
    override suspend fun insert(entity: WebURLEntity) = Unit
    override suspend fun get(): List<WebURLEntity> = emptyList()
    override suspend fun isExist(url: String): Boolean = false
    override suspend fun delFromUrl(url: String): Int = -1
}