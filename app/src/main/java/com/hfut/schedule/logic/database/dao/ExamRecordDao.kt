package com.hfut.schedule.logic.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.hfut.schedule.logic.database.entity.ExamRecordEntity

@Dao
interface ExamRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<ExamRecordEntity>)

    @Query(
        """
        DELETE FROM exam_record
        WHERE semester = :semester AND source = :source
        """
    )
    suspend fun deleteBySemesterAndSource(semester: Int, source: String)

    @Transaction
    suspend fun replaceSource(
        semester: Int,
        source: String,
        records: List<ExamRecordEntity>
    ) {
        deleteBySemesterAndSource(semester, source)
        if (records.isNotEmpty()) {
            insertAll(records)
        }
    }

    @Query(
        """
        SELECT * FROM exam_record
        WHERE semester = :semester
        ORDER BY dateTime ASC
        """
    )
    suspend fun getBySemester(semester: Int): List<ExamRecordEntity>

}
