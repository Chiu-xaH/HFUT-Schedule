package com.hfut.schedule.logic.database.impl

import com.hfut.schedule.logic.database.dao.FriendDao
import com.hfut.schedule.logic.database.entity.FriendEntity

class FriendDaoImplSafely : FriendDao {
    override suspend fun getAll(): List<FriendEntity> = emptyList()
    override suspend fun del(id: Int): Int = -1
    override suspend fun insert(bean: FriendEntity): Long = -1L
}