package com.hfut.schedule.ui.screen.report

data class TermInfo(val startYear: Int, val endYear: Int, val termNum: Int) {
    val displayName = "$startYear-$endYear $termNum"
    val dormitoryName = "${startYear}-${endYear}学年第${if (termNum == 1) "一" else "二"}学期"
    val dateRangeStart = if (termNum == 1) "$startYear-09" else "$endYear-02"
    val dateRangeEnd = if (termNum == 1) "$endYear-02" else "$endYear-09"
}

fun parseSemesterInt(semester: Int): TermInfo? {
    return try {
        val codes = (semester - 4) / 10
        val startYear = 2018 + (codes + 1) / 4
        val endYear = startYear + 1
        val termNum = if (codes % 4 == 1) 2 else if (codes % 4 == 3) 1 else 0
        if (termNum == 0) null
        else TermInfo(startYear, endYear, termNum)
    } catch (_: Exception) { null }
}

fun termStringToSemesterInt(term: String): Int? {
    return try {
        val years = Regex("(\\d{4})[^\\d]+(\\d{4})").find(term) ?: return null
        val y1 = years.groupValues[1].toInt()
        val hasTwo = Regex("[2二]").containsMatchIn(term.substringAfter(years.value))
        val termNum = if (hasTwo) 2 else 1
        val codes = (y1 - 2019) * 4 + (if (termNum == 2) 5 else 3)
        codes * 10 + 4
    } catch (_: Exception) { null }
}

fun latestSemesterFromTerms(terms: List<String>): Int? {
    return terms.mapNotNull { termStringToSemesterInt(it) }.maxOrNull()
}
