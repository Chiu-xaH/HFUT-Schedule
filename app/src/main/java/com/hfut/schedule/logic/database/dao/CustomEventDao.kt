package com.hfut.schedule.logic.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.TypeConverter
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
    suspend fun del(id : Int) : Int
    // 新建
    @Insert
    suspend fun insert(event : CustomEventEntity) : Long
}

