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
        WHERE studentId = :studentId AND semester = :semester AND source = :source
        """
    )
    suspend fun deleteBySemesterAndSource(studentId: String, semester: Int, source: String)

    @Transaction
    suspend fun replaceSource(
        studentId: String,
        semester: Int,
        source: String,
        records: List<ExamRecordEntity>
    ) {
        deleteBySemesterAndSource(studentId, semester, source)
        if (records.isNotEmpty()) {
            insertAll(records)
        }
    }

    @Query(
        """
        SELECT * FROM exam_record
        WHERE studentId = :studentId AND semester = :semester
        ORDER BY dateTime ASC
        """
    )
    suspend fun getBySemester(studentId: String, semester: Int): List<ExamRecordEntity>

}
