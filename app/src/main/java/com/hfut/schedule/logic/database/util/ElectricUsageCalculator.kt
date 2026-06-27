package com.hfut.schedule.logic.database.util

import com.hfut.schedule.logic.database.entity.ElectricBalanceRecordEntity
import com.hfut.schedule.logic.database.model.ElectricConsumptionInterval
import com.hfut.schedule.logic.database.model.ElectricDailyConsumption
import com.hfut.schedule.logic.database.model.ElectricIncreaseEvent
import com.hfut.schedule.logic.database.model.ElectricUsageSummary
import java.util.Calendar

object ElectricUsageCalculator {

    private const val EPSILON = 0.005
    private const val MILLIS_PER_DAY = 86_400_000L

    fun calculate(records: List<ElectricBalanceRecordEntity>): ElectricUsageSummary {
        if (records.isEmpty()) {
            return emptySummary()
        }

        val meterKeys = records.map { it.meterKey }.distinct()
        require(meterKeys.size <= 1) {
            "ElectricUsageCalculator only accepts records from one meterKey, but got: $meterKeys"
        }

        val sorted = records.sortedBy { it.sampledAt }
        val intervals = mutableListOf<ElectricConsumptionInterval>()
        val increases = mutableListOf<ElectricIncreaseEvent>()
        val dailyConsumptionMap = linkedMapOf<Long, Double>()
        var totalConsumed = 0.0
        var totalDurationDays = 0.0

        for (i in 1 until sorted.size) {
            val prev = sorted[i - 1]
            val curr = sorted[i]

            if (curr.sampledAt <= prev.sampledAt) continue

            val diff = prev.remainingBalance - curr.remainingBalance
            val durationMs = curr.sampledAt - prev.sampledAt
            val durationDays = durationMs.toDouble() / MILLIS_PER_DAY

            when {
                diff > EPSILON -> {
                    val consumed = diff
                    val dailyRate = if (durationDays > 0) consumed / durationDays else 0.0
                    intervals.add(
                        ElectricConsumptionInterval(
                            startAt = prev.sampledAt,
                            endAt = curr.sampledAt,
                            startBalance = prev.remainingBalance,
                            endBalance = curr.remainingBalance,
                            consumed = consumed,
                            durationDays = durationDays,
                            dailyRate = dailyRate
                        )
                    )
                    totalConsumed += consumed
                    if (durationDays > 0) {
                        totalDurationDays += durationDays
                    }
                    distributeConsumptionByDay(
                        dailyConsumptionMap = dailyConsumptionMap,
                        startAt = prev.sampledAt,
                        endAt = curr.sampledAt,
                        consumed = consumed
                    )
                }
                diff < -EPSILON -> {
                    val increaseAmount = curr.remainingBalance - prev.remainingBalance
                    increases.add(
                        ElectricIncreaseEvent(
                            sampledAt = curr.sampledAt,
                            amount = increaseAmount
                        )
                    )
                }
                else -> {
                    // 余额基本不变
                }
            }
        }

        val latestBalance = sorted.lastOrNull()?.remainingBalance
        val firstSampleAt = sorted.firstOrNull()?.sampledAt
        val latestSampleAt = sorted.lastOrNull()?.sampledAt

        val averageDailyConsumption = if (totalDurationDays > 0) {
            totalConsumed / totalDurationDays
        } else {
            null
        }

        val estimatedRemainingDays = if (latestBalance != null &&
            latestBalance > 0 &&
            averageDailyConsumption != null &&
            averageDailyConsumption > 0 &&
            intervals.isNotEmpty()
        ) {
            latestBalance / averageDailyConsumption
        } else {
            null
        }

        return ElectricUsageSummary(
            latestBalance = latestBalance,
            firstSampleAt = firstSampleAt,
            latestSampleAt = latestSampleAt,
            totalConsumed = totalConsumed,
            averageDailyConsumption = averageDailyConsumption,
            estimatedRemainingDays = estimatedRemainingDays,
            validIntervalCount = intervals.size,
            increases = increases,
            intervals = intervals,
            dailyConsumptions = dailyConsumptionMap
                .map { (dayStartAt, consumed) ->
                    ElectricDailyConsumption(
                        dayStartAt = dayStartAt,
                        consumed = consumed
                    )
                }
                .filter { it.consumed > EPSILON }
        )
    }

    fun calculateRecent7DaysConsumption(
        records: List<ElectricBalanceRecordEntity>,
        now: Long = System.currentTimeMillis()
    ): Double {
        if (records.isEmpty()) return 0.0

        val meterKeys = records.map { it.meterKey }.distinct()
        require(meterKeys.size <= 1) {
            "ElectricUsageCalculator only accepts records from one meterKey, but got: $meterKeys"
        }

        val sevenDaysAgo = now - 7 * MILLIS_PER_DAY
        var consumption = 0.0

        val sorted = records.sortedBy { it.sampledAt }
        for (i in 1 until sorted.size) {
            val prev = sorted[i - 1]
            val curr = sorted[i]

            if (curr.sampledAt <= prev.sampledAt) continue

            val diff = prev.remainingBalance - curr.remainingBalance
            if (diff <= EPSILON) continue

            val intervalStart = prev.sampledAt
            val intervalEnd = curr.sampledAt

            if (intervalEnd <= sevenDaysAgo) continue
            if (intervalStart >= now) continue

            val effectiveStart = maxOf(intervalStart, sevenDaysAgo)
            val effectiveEnd = minOf(intervalEnd, now)
            val totalDuration = (intervalEnd - intervalStart).toDouble()

            if (totalDuration > 0) {
                val overlapRatio = (effectiveEnd - effectiveStart).toDouble() / totalDuration
                consumption += diff * overlapRatio
            }
        }

        return consumption
    }

    private fun distributeConsumptionByDay(
        dailyConsumptionMap: MutableMap<Long, Double>,
        startAt: Long,
        endAt: Long,
        consumed: Double
    ) {
        val totalDuration = endAt - startAt
        if (totalDuration <= 0L || consumed <= EPSILON) return

        var cursor = startAt
        while (cursor < endAt) {
            val dayStart = startOfDay(cursor)
            val nextDayStart = nextDayStart(cursor)
            val segmentEnd = minOf(endAt, nextDayStart)
            val segmentDuration = segmentEnd - cursor
            if (segmentDuration > 0) {
                val segmentConsumed = consumed * segmentDuration.toDouble() / totalDuration.toDouble()
                dailyConsumptionMap[dayStart] = (dailyConsumptionMap[dayStart] ?: 0.0) + segmentConsumed
            }
            cursor = segmentEnd
        }
    }

    private fun startOfDay(timeMillis: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timeMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun nextDayStart(timeMillis: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = startOfDay(timeMillis)
            add(Calendar.DAY_OF_MONTH, 1)
        }.timeInMillis
    }

    private fun emptySummary() = ElectricUsageSummary(
        latestBalance = null,
        firstSampleAt = null,
        latestSampleAt = null,
        totalConsumed = 0.0,
        averageDailyConsumption = null,
        estimatedRemainingDays = null,
        validIntervalCount = 0,
        increases = emptyList(),
        intervals = emptyList(),
        dailyConsumptions = emptyList()
    )
}
