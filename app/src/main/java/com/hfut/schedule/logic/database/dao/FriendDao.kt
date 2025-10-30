package com.hfut.schedule.logic.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hfut.schedule.logic.database.entity.FriendEntity

@Dao
interface FriendDao {
    @Query("SELECT * FROM friend")
    suspend fun getAll() : List<FriendEntity>
    // 删除
    @Query("DELETE FROM friend WHERE id = :id")
    suspend fun del(id: Int): Int
    // 新建
    @Insert
    suspend fun insert(bean : FriendEntity): Long
}