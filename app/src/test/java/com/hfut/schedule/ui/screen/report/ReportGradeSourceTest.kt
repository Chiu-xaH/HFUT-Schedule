package com.hfut.schedule.ui.screen.report

import com.hfut.schedule.logic.model.community.GradeJxglstuDTO
import com.hfut.schedule.logic.model.community.GradeJxglstuResponse
import com.hfut.schedule.logic.model.jxglstu.NameZh
import com.hfut.schedule.logic.model.uniapp.UniAppGradeBean
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReportGradeSourceTest {

    @Test
    fun jxglstuGrades_areConvertedForReport() {
        val source = listOf(
            GradeJxglstuDTO(
                term = "2025~2026年第1学期",
                list = listOf(
                    GradeJxglstuResponse("高等数学", "4", "3.5", "90", "90", "MATH01"),
                    GradeJxglstuResponse("大学物理", "3", "0", "55", "55", "PHY01")
                )
            )
        )

        val grades = jxglstuReportGrades(source)

        assertEquals("高等数学", grades[0].courseName)
        assertEquals(4.0, grades[0].credits, 0.0)
        assertTrue(grades[0].passed)
        assertFalse(grades[1].passed)
        assertEquals("2025~2026年第1学期", grades[0].term)
    }

    @Test
    fun reportTerms_keepJxglstuTermAndPassFlags() {
        val terms = jxglstuReportGrades(
            listOf(
                GradeJxglstuDTO(
                    "2025~2026年第2学期",
                    listOf(GradeJxglstuResponse("英语", "2", "2.5", "75", "75", "ENG01"))
                )
            )
        ).toReportTerms()

        assertEquals("2025~2026年第2学期", terms.single().dto.term)
        assertEquals(listOf(true), terms.single().passFlags)
    }

    @Test
    fun uniAppPassedWithZeroGp_isExcludedFromReport() {
        val source = mapOf(
            "2025~2026年第1学期" to listOf(
                UniAppGradeBean(
                    courseNameZh = "通识选修",
                    lessonCode = "GEN01",
                    semester = NameZh("2025~2026年第1学期"),
                    passed = true,
                    finalGrade = "合格",
                    gradeDetail = "合格",
                    credits = 2.0,
                    gp = 0.0
                ),
                UniAppGradeBean(
                    courseNameZh = "高等数学",
                    lessonCode = "MATH01",
                    semester = NameZh("2025~2026年第1学期"),
                    passed = true,
                    finalGrade = "90",
                    gradeDetail = "90",
                    credits = 4.0,
                    gp = 3.8
                )
            )
        )

        val terms = uniAppReportGrades(source)
            .filter { it.finalGrade != null && !(it.passed && it.gp == 0.0) }
            .toReportTerms()

        val included = terms.flatMap { it.dto.list }
        assertEquals(1, included.size)
        assertEquals("高等数学", included[0].courseName)
    }

    @Test
    fun jxglstuPassFailGrade_withZeroGp_isStillPassed() {
        val source = listOf(
            GradeJxglstuDTO(
                term = "2025~2026年第1学期",
                list = listOf(
                    GradeJxglstuResponse(
                        "劳动教育",
                        "1",
                        "0",
                        "合格",
                        "合格",
                        "LABOR01"
                    )
                )
            )
        )

        assertTrue(jxglstuReportGrades(source).single().passed)
    }

    @Test
    fun emptyUniAppGrades_fallBackToJxglstuGrades() {
        val result = selectReportGrades(
            uniAppGrades = emptyMap(),
            jxglstuGrades = listOf(
                GradeJxglstuDTO(
                    "2025~2026年第1学期",
                    listOf(
                        GradeJxglstuResponse(
                            "高等数学", "4", "3.5",
                            "90", "90", "MATH01"
                        )
                    )
                )
            )
        )

        assertEquals("高等数学", result.single().courseName)
    }

    @Test
    fun uniAppSemesterWithEmptyGradeList_fallsBackToJxglstu() {
        val result = selectReportGrades(
            uniAppGrades = mapOf(
                "2025~2026年第1学期" to emptyList()
            ),
            jxglstuGrades = listOf(
                GradeJxglstuDTO(
                    "2025~2026年第1学期",
                    listOf(
                        GradeJxglstuResponse(
                            "高等数学", "4", "3.5",
                            "90", "90", "MATH01"
                        )
                    )
                )
            )
        )

        assertEquals("高等数学", result.single().courseName)
    }
}
