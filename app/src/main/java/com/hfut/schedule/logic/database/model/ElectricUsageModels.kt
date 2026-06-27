package com.hfut.schedule.logic.database.model

data class ElectricConsumptionInterval(
    val startAt: Long,
    val endAt: Long,
    val startBalance: Double,
    val endBalance: Double,
    val consumed: Double,
    val durationDays: Double,
    val dailyRate: Double
)

data class ElectricDailyConsumption(
    val dayStartAt: Long,
    val consumed: Double
)

data class ElectricIncreaseEvent(
    val sampledAt: Long,
    val amount: Double
)

data class ElectricUsageSummary(
    val latestBalance: Double?,
    val firstSampleAt: Long?,
    val latestSampleAt: Long?,
    val totalConsumed: Double,
    val averageDailyConsumption: Double?,
    val estimatedRemainingDays: Double?,
    val validIntervalCount: Int,
    val increases: List<ElectricIncreaseEvent>,
    val intervals: List<ElectricConsumptionInterval>,
    val dailyConsumptions: List<ElectricDailyConsumption>
)
