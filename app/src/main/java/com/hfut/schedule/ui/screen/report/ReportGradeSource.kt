package com.hfut.schedule.ui.screen.report

import com.hfut.schedule.network.api.model.response.html.JxglstuTermGrade
import com.hfut.schedule.network.api.model.response.html.JxglstuGrade
import com.hfut.schedule.network.api.model.response.json.uniapp.UniAppGrade

data class ReportGradeItem(
    val courseName: String,
    val lessonCode: String,
    val term: String,
    val passed: Boolean,
    val finalGrade: String?,
    val gradeDetail: String,
    val credits: Double,
    val gp: Double
)

data class ReportTermGrade(
    val dto: JxglstuTermGrade,
    val passFlags: List<Boolean>
)

fun hasUniAppGradeData(
    grades: Map<String, List<UniAppGrade>>?
): Boolean = grades?.values?.any { it.isNotEmpty() } == true

fun hasJxglstuGradeData(
    grades: List<JxglstuTermGrade>?
): Boolean = grades?.any { it.list.isNotEmpty() } == true

fun selectReportGrades(
    uniAppGrades: Map<String, List<UniAppGrade>>?,
    jxglstuGrades: List<JxglstuTermGrade>?
): List<ReportGradeItem> {
    return when {
        hasUniAppGradeData(uniAppGrades) ->
            uniAppReportGrades(uniAppGrades!!)
        hasJxglstuGradeData(jxglstuGrades) ->
            jxglstuReportGrades(jxglstuGrades!!)
        else -> emptyList()
    }
}

fun uniAppReportGrades(gradeMap: Map<String, List<UniAppGrade>>): List<ReportGradeItem> {
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

fun jxglstuReportGrades(terms: List<JxglstuTermGrade>): List<ReportGradeItem> {
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

fun List<ReportGradeItem>.toReportTerms(): List<ReportTermGrade> {
    return groupBy { it.term }.map { (term, grades) ->
        ReportTermGrade(
            dto = JxglstuTermGrade(
                term,
                grades.map {
                    JxglstuGrade(
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
