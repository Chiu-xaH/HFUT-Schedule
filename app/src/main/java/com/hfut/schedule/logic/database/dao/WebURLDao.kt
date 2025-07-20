package com.hfut.schedule.logic.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hfut.schedule.logic.database.entity.WebURLEntity

@Dao
interface WebURLDao {
    // 删除
    @Query("DELETE FROM web_url WHERE id = :id")
    suspend fun del(id : Int) : Int
    // 添加
    @Insert
    suspend fun insert(entity: WebURLEntity)
    // 获取
    @Query("SELECT * FROM web_url")
    suspend fun get() : List<WebURLEntity>
    // 根据URL查找是否存在
    @Query("SELECT EXISTS(SELECT 1 FROM web_url WHERE url = :url)")
    suspend fun isExist(url : String) : Boolean
    // 根据URL删除
    @Query("DELETE FROM web_url WHERE url = :url")
    suspend fun delFromUrl(url : String) : Int
}