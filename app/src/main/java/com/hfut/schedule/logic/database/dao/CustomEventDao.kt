package com.hfut.schedule.logic.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.TypeConverter
import androidx.room.Update
import com.hfut.schedule.logic.database.entity.CustomCourseTableEntity
import com.hfut.schedule.logic.database.entity.CustomEventDTO
import com.hfut.schedule.logic.database.entity.CustomEventEntity
import com.hfut.schedule.logic.database.entity.CustomEventType
import com.hfut.schedule.logic.util.sys.DateTime
import com.hfut.schedule.logic.util.sys.DateTimeBean
import java.util.Calendar
import java.util.Date

@Dao
interface CustomEventDao {
    // 获取某类型
    @Query("SELECT * FROM event WHERE type = :type")
    suspend fun getAll(type: String) : List<CustomEventEntity>
    // 删除
    @Query("DELETE FROM event WHERE id = :id")
    suspend fun del(id : Int)
    // 新建
    @Insert
    suspend fun insert(event : CustomEventEntity)
    // 获取下载的日程
    @Query("SELECT * FROM event WHERE supabase_id IS NOT NULL AND type = :type")
    suspend fun getDownloaded(type: String) : List<CustomEventEntity>
    // 更新
    @Update
    suspend fun update(event : CustomEventEntity)
    // 传入supabase_id，检查整张表里是否存在
    @Query("SELECT EXISTS(SELECT 1 FROM event WHERE supabase_id = :supabaseId)")
    suspend fun isExistBySupabaseId(supabaseId: Int): Boolean
    // 传入supabase_id，删除
    @Query("DELETE FROM event WHERE supabase_id = :supabaseId")
    suspend fun delBySupabaseId(supabaseId: Int)
}

