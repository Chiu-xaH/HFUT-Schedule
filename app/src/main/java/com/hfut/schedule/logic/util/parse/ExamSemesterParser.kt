package com.hfut.schedule.logic.util.parse

import com.hfut.schedule.network.api.model.response.html.JxglstuExam

fun groupExamsBySemester(exams: List<JxglstuExam>): Map<Int, List<JxglstuExam>> {
    return exams.mapNotNull { exam ->
        SemesterParser.reverseGetSemester(exam.dateTime.substringBefore(" "))
            ?.let { semester -> semester to exam }
    }.groupBy({ it.first }, { it.second })
}
