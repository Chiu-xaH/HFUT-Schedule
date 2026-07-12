package com.hfut.schedule.logic.model

import com.hfut.schedule.logic.util.parse.SemesterParser
import com.hfut.schedule.logic.util.parse.groupExamsBySemester
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExamTest {

    @Test
    fun semesterForExam_mapsBothTermsAndJanuaryCorrectly() {
        assertEquals(274, SemesterParser.reverseGetSemester("2025-01-10"))
        assertEquals(294, SemesterParser.reverseGetSemester("2025-06-20"))
        assertEquals(314, SemesterParser.reverseGetSemester("2025-12-20"))
    }

    @Test
    fun semesterForExam_rejectsInvalidInput() {
        assertNull(SemesterParser.reverseGetSemester("2025-13-01"))
    }

    @Test
    fun groupExamsBySemester_keepsHistoricalExamsSeparate() {
        val oldExam = JxglstuExam("旧学期考试", "2025-01-10 09:00~11:00", null)
        val currentExam = JxglstuExam("新学期考试", "2025-06-20 09:00~11:00", null)

        val grouped = groupExamsBySemester(listOf(oldExam, currentExam))

        assertEquals(listOf(oldExam), grouped[274])
        assertEquals(listOf(currentExam), grouped[294])
    }

    @Test
    fun examDateTimeValidation_isStrict() {
        assertTrue(isValidExamDateTime("2025-06-20 09:00~11:00"))
        assertFalse(isValidExamDateTime("2025-02-30 09:00~11:00"))
        assertFalse(isValidExamDateTime("2025-06-20 25:00~26:00"))
    }
}
