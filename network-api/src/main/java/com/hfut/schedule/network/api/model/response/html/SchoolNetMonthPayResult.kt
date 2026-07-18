package com.hfut.schedule.network.api.model.response.html

data class SchoolNetMonthPayResult(
    val year: Int?,
    val summary: SchoolNetMonthPaySummary,
    val records: List<SchoolNetMonthPayRecord>
)

data class SchoolNetMonthPaySummary(
    val baseFee: Double,
    val usageFee: Double,
    val durationMinutes: Int,
    val flowMb: Double
)

data class SchoolNetMonthPayRecord(
    val startDate: String,
    val endDate: String,
    val packageName: String,
    val baseFee: Double,
    val usageFee: Double,
    val durationMinutes: Int,
    val flowMb: Double,
    val billTime: String
)

data class SchoolNetSemesterUsageResult(
    val semester: Int,
    val startYearMonth: String,
    val endYearMonth: String,
    val totalDurationMinutes: Int,
    val totalFlowMb: Double,
    val records: List<SchoolNetMonthPayRecord>
)
