package com.hfut.schedule.logic.database.impl

import com.hfut.schedule.logic.database.dao.CustomEventDao
import com.hfut.schedule.logic.database.entity.CustomEventEntity

class CustomEventDaoImplSafely : CustomEventDao {
    override suspend fun getById(id: Int): CustomEventEntity? = null
    override suspend fun getAll(type: String): List<CustomEventEntity> = emptyList()
    override suspend fun del(id: Int) = Unit
    override suspend fun insert(event: CustomEventEntity): Long = -1L
    override suspend fun getDownloaded(type: String): List<CustomEventEntity> = emptyList()
    override suspend fun update(event: CustomEventEntity): Int = -1
    override suspend fun isExistBySupabaseId(supabaseId: Int): Boolean = false
    override suspend fun delBySupabaseId(supabaseId: Int) = Unit
    override suspend fun getAllSortedByTime(): List<CustomEventEntity> = emptyList()
    override suspend fun getDownloadedByTime(): List<CustomEventEntity> = emptyList()
}