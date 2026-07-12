package com.hfut.schedule.logic.database.repository

import com.hfut.schedule.logic.database.DataBaseManager
import com.hfut.schedule.logic.database.entity.ExamRecordEntity
import com.hfut.schedule.logic.model.JxglstuExam
import com.hfut.schedule.logic.util.parse.groupExamsBySemester
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.xah.common.logic.util.LogUtil
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

object ExamHistoryRepository {

    private val dao get() = DataBaseManager.examRecordDao

    private val saveMutex = Mutex()

    suspend fun getExams(semester: Int): List<JxglstuExam> = withContext(Dispatchers.IO) {
        try {
            val studentId = currentStudentId() ?: return@withContext emptyList()
            dao.getBySemester(studentId, semester).mergeSources()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            LogUtil.error(e, "从Room读取考试记录失败")
            emptyList()
        }
    }

    suspend fun saveExamSnapshot(
        studentId: String,
        exams: List<JxglstuExam>,
        source: String,
        fallbackSemester: Int
    ): Boolean = saveMutex.withLock {
        withContext(Dispatchers.IO) {
            try {
                val ownerStudentId = studentId.trim().takeIf { it.isNotEmpty() }
                    ?: return@withContext false
                val groupedExams = groupExamsBySemester(exams)
                val snapshots = buildMap {
                    put(fallbackSemester, emptyList())
                    putAll(groupedExams)
                }
                val now = System.currentTimeMillis()

                snapshots.forEach { (semester, semesterExams) ->
                    val entities = semesterExams.map { exam ->
                        ExamRecordEntity(
                            name = exam.name,
                            dateTime = exam.dateTime,
                            place = exam.place,
                            type = exam.type,
                            source = source,
                            studentId = ownerStudentId,
                            semester = semester,
                            fetchedAt = now
                        )
                    }
                    dao.replaceSource(ownerStudentId, semester, source, entities)
                    LogUtil.info("考试记录快照已按用户和日期归档: semester=$semester, count=${entities.size}, source=$source")
                }
                true
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                LogUtil.error(e, "按日期归档考试记录失败")
                false
            }
        }
    }

    internal fun currentStudentId(): String? = SharedPrefs.prefs
        .getString("Username", null)
        ?.trim()
        ?.takeIf { it.isNotEmpty() }

    private fun ExamRecordEntity.toJxglstuExam(): JxglstuExam {
        return JxglstuExam(
            name = name,
            dateTime = dateTime,
            place = place,
            type = type
        )
    }

    private fun List<ExamRecordEntity>.mergeSources(): List<JxglstuExam> {
        return groupBy { it.name to it.dateTime }
            .map { (_, records) ->
                val first = records.first()
                JxglstuExam(
                    name = first.name,
                    dateTime = first.dateTime,
                    place = records.firstNotNullOfOrNull { it.place?.takeIf(String::isNotBlank) },
                    type = records.firstNotNullOfOrNull { it.type?.takeIf(String::isNotBlank) }
                )
            }
            .sortedBy { it.dateTime }
    }
}
