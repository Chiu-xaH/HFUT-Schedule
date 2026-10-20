package com.hfut.schedule.logic.database.impl

import com.hfut.schedule.logic.database.dao.ExamRecordDao
import com.hfut.schedule.logic.database.entity.ExamRecordEntity

class ExamRecordDaoImplSafely : ExamRecordDao {
    override suspend fun insertAll(records: List<ExamRecordEntity>) = Unit
    override suspend fun deleteBySemesterAndSource(semester: Int, source: String) = Unit
    override suspend fun getBySemester(semester: Int): List<ExamRecordEntity> = emptyList()
}