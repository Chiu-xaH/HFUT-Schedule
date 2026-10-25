package com.hfut.schedule.logic.database.repository

import com.hfut.schedule.logic.database.core.DataBaseManager
import com.hfut.schedule.logic.database.entity.CustomEventEntity
import com.hfut.schedule.logic.database.entity.CustomEventType

object CustomEventRepository {
    private val dao by lazy { DataBaseManager.customEventDao }

    suspend fun getGroupById(eventId: Int): List<CustomEventEntity> {
        val entity = dao.getById(eventId) ?: return emptyList()
        // DDL类型没有聚簇日程
        if (entity.type == CustomEventType.NET_COURSE.name) {
            return listOf(entity)
        }
        if(entity.groupId == null) {
            return listOf(entity)
        }
        return dao.getByGroupId(entity.groupId)
    }
}