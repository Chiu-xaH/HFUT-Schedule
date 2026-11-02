package com.hfut.schedule.logic.model.anr

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class AnrDiagnosticException(
    val report: AnrReport
) : Exception(
    buildString {
        append("进程=")
        append(report.processName)
        append("，时间=")
        append(
            Instant.ofEpochMilli(report.occurredAt)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        )
        if (!report.description.isNullOrBlank()) {
            append("，说明=")
            append(report.description)
        }
    }
)