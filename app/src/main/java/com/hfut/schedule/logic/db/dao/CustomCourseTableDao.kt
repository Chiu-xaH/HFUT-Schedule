package com.hfut.schedule.logic.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hfut.schedule.logic.db.entity.CustomCourseTableEntity
import com.hfut.schedule.logic.db.entity.CustomCourseTableSummary

// JSON较大 所以分开Query
@Dao
interface CustomCourseTableDao {
    // 获取部分
    @Query("SELECT id,title FROM course")
    suspend fun get() : List<CustomCourseTableSummary>
    // 根据id获取内容
    @Query("SELECT content_json FROM course WHERE id = :id")
    suspend fun getContent(id : Int) : String?
    // 查找全部 不推荐
    @Query("SELECT * FROM course")
    suspend fun getAll() : List<CustomCourseTableEntity>
    // 删除
    @Query("DELETE FROM course WHERE id = :id")
    suspend fun del(id : Int) : Int
    // 新建
    @Insert
    suspend fun insert(courseTable: CustomCourseTableEntity)
    // 清空
    @Query("DELETE FROM course")
    suspend fun clearAll()
    // 数量
    @Query("SELECT COUNT (*) FROM course")
    suspend fun count() : Int
}