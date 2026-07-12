package com.hfut.schedule.ui.screen.report

import com.hfut.schedule.logic.model.community.GradeJxglstuDTO
import com.hfut.schedule.logic.model.community.GradeJxglstuResponse
import com.hfut.schedule.logic.model.uniapp.UniAppGradeBean

internal data class ReportGradeItem(
    val courseName: String,
    val lessonCode: String,
    val term: String,
    val passed: Boolean,
    val finalGrade: String?,
    val gradeDetail: String,
    val credits: Double,
    val gp: Double
)

internal data class ReportTermGrade(
    val dto: GradeJxglstuDTO,
    val passFlags: List<Boolean>
)

internal fun hasUniAppGradeData(
    grades: Map<String, List<UniAppGradeBean>>?
): Boolean = grades?.values?.any { it.isNotEmpty() } == true

internal fun hasJxglstuGradeData(
    grades: List<GradeJxglstuDTO>?
): Boolean = grades?.any { it.list.isNotEmpty() } == true

internal fun selectReportGrades(
    uniAppGrades: Map<String, List<UniAppGradeBean>>?,
    jxglstuGrades: List<GradeJxglstuDTO>?
): List<ReportGradeItem> {
    return when {
        hasUniAppGradeData(uniAppGrades) ->
            uniAppReportGrades(uniAppGrades!!)
        hasJxglstuGradeData(jxglstuGrades) ->
            jxglstuReportGrades(jxglstuGrades!!)
        else -> emptyList()
    }
}

internal fun uniAppReportGrades(gradeMap: Map<String, List<UniAppGradeBean>>): List<ReportGradeItem> {
    return gradeMap.flatMap { (term, grades) ->
        grades.map { grade ->
            ReportGradeItem(
                courseName = grade.courseNameZh,
                lessonCode = grade.lessonCode,
                term = term,
                passed = grade.passed,
                finalGrade = grade.finalGrade,
                gradeDetail = grade.gradeDetail,
                credits = grade.credits,
                gp = grade.gp
            )
        }
    }
}

internal fun jxglstuReportGrades(terms: List<GradeJxglstuDTO>): List<ReportGradeItem> {
    return terms.flatMap { term ->
        term.list.map { grade ->
            ReportGradeItem(
                courseName = grade.courseName,
                lessonCode = grade.lessonCode,
                term = term.term,
                passed = isJxglstuGradePassed(grade.detail, grade.gpa.toDoubleOrNull() ?: 0.0),
                finalGrade = grade.detail,
                gradeDetail = grade.score,
                credits = grade.credits.toDoubleOrNull() ?: 0.0,
                gp = grade.gpa.toDoubleOrNull() ?: 0.0
            )
        }
    }
}

private fun isJxglstuGradePassed(finalGrade: String, gp: Double): Boolean {
    finalGrade.toDoubleOrNull()?.let {
        return it >= 60.0
    }

    return when (finalGrade.trim()) {
        "优秀", "优",
        "良好", "良",
        "中等", "中",
        "及格",
        "合格",
        "通过" -> true

        "不及格",
        "不合格",
        "未通过" -> false

        else -> gp > 0.0
    }
}

internal fun List<ReportGradeItem>.toReportTerms(): List<ReportTermGrade> {
    return groupBy { it.term }.map { (term, grades) ->
        ReportTermGrade(
            dto = GradeJxglstuDTO(
                term,
                grades.map {
                    GradeJxglstuResponse(
                        it.courseName,
                        it.credits.toString(),
                        it.gp.toString(),
                        it.gradeDetail,
                        it.finalGrade.orEmpty(),
                        it.lessonCode
                    )
                }
            ),
            passFlags = grades.map { it.passed }
        )
    }.sortedByDescending { it.dto.term }
}
