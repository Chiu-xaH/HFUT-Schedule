package com.hfut.schedule.logic.model.anr

data class AnrReport(
    val occurredAt: Long,
    val processName: String,
    val description: String?,
    val traceText: String?
)