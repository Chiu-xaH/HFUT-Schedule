package com.hfut.schedule.logic.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.hfut.schedule.logic.database.entity.ShowerLabelEntity

@Dao
interface ShowerLabelDao {
    @Query("SELECT * FROM shower_label")
    suspend fun getAll() : List<ShowerLabelEntity>
    // 删除
    @Query("DELETE FROM shower_label WHERE id = :id")
    suspend fun del(id: Int): Int // 返回删除的行数
    // 新建
    @Insert
    suspend fun insert(showerLabel : ShowerLabelEntity)
}