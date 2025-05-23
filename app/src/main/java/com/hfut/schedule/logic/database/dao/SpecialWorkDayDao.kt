package com.hfut.schedule.logic.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hfut.schedule.logic.database.entity.SpecialWorkDayEntity
import com.hfut.schedule.logic.util.sys.DateTimeUtils


@Dao
interface SpecialWorkDayDao {
    @Query("SELECT * FROM special_work_day")
    suspend fun getAll(): List<SpecialWorkDayEntity>

    @Query("DELETE FROM special_work_day WHERE origin_date = :originDate")
    suspend fun delete(originDate: String): Int

    // 自动更新或插入
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SpecialWorkDayEntity)

    @Query("SELECT * FROM special_work_day WHERE origin_date = :originDate")
    suspend fun search(originDate: String): SpecialWorkDayEntity?

    @Query("SELECT target_date FROM special_work_day WHERE origin_date = :originDate")
    suspend fun searchToday(originDate: String = DateTimeUtils.Date_yyyy_MM_dd): String?

    @Query("SELECT target_date FROM special_work_day WHERE origin_date = :originDate")
    suspend fun searchTomorrow(originDate: String = DateTimeUtils.tomorrow_YYYY_MM_DD): String?
}
