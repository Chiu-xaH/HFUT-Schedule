package com.hfut.schedule.logic.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hfut.schedule.logic.database.entity.ElectricBalanceRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ElectricBalanceRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: ElectricBalanceRecordEntity)

    @Query(
        """
        SELECT * FROM electric_balance_record
        WHERE meterKey = :meterKey
        ORDER BY sampledAt ASC
        """
    )
    fun observeByMeter(meterKey: String): Flow<List<ElectricBalanceRecordEntity>>

    @Query(
        """
        SELECT * FROM electric_balance_record
        WHERE meterKey = :meterKey
        ORDER BY sampledAt DESC
        LIMIT 1
        """
    )
    suspend fun getLatest(meterKey: String): ElectricBalanceRecordEntity?

    @Query(
        """
        DELETE FROM electric_balance_record
        WHERE meterKey = :meterKey
        """
    )
    suspend fun deleteByMeter(meterKey: String)

    @Query(
        """
        DELETE FROM electric_balance_record
        WHERE sampledAt < :before
        """
    )
    suspend fun deleteBefore(before: Long)

    @Query(
        """
        SELECT COUNT(*) FROM electric_balance_record
        WHERE meterKey = :meterKey
        """
    )
    suspend fun countByMeter(meterKey: String): Int
}
