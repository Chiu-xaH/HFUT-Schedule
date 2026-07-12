package com.hfut.schedule.logic.model

import java.text.SimpleDateFormat

data class JxglstuExam(
    val name: String,
    val dateTime: String,
    val place: String?,
    val type: String? = null
)

// 日期时间格式必须是 YYYY-MM-DD HH:MM~HH:MM
fun isValidExamDateTime(value: String): Boolean {
    val regex = Regex("""\d{4}-\d{2}-\d{2} \d{2}:\d{2}~\d{2}:\d{2}""")
    if (!regex.matches(value)) return false

    return try {
        val (datePart, rangePart) = value.split(" ")
        val (startTime, endTime) = rangePart.split("~")
        SimpleDateFormat("yyyy-MM-dd").apply { isLenient = false }.parse(datePart)
        SimpleDateFormat("HH:mm").apply { isLenient = false }.run {
            parse(startTime)
            parse(endTime)
        }
        true
    } catch (_: Exception) {
        false
    }
}
