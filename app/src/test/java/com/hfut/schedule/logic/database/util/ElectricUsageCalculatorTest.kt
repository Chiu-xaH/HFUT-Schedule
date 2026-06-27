package com.hfut.schedule.logic.database.util

import com.hfut.schedule.logic.database.entity.ElectricBalanceRecordEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class ElectricUsageCalculatorTest {

    private fun createRecord(
        id: Long = 0,
        meterKey: String = "HEFEI:1:101",
        campusRegion: String = "合肥",
        roomName: String = "1号楼101寝室",
        remainingBalance: Double,
        sampledAt: Long
    ) = ElectricBalanceRecordEntity(
        id = id,
        meterKey = meterKey,
        campusRegion = campusRegion,
        roomName = roomName,
        remainingBalance = remainingBalance,
        sampledAt = sampledAt
    )

    private fun timeAt(
        year: Int = 2026,
        month: Int = Calendar.JUNE,
        day: Int,
        hour: Int,
        minute: Int = 0
    ): Long = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, day)
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    @Test
    fun `empty records`() {
        val summary = ElectricUsageCalculator.calculate(emptyList())
        assertNull(summary.latestBalance)
        assertNull(summary.firstSampleAt)
        assertNull(summary.latestSampleAt)
        assertEquals(0.0, summary.totalConsumed, 0.001)
        assertNull(summary.averageDailyConsumption)
        assertNull(summary.estimatedRemainingDays)
        assertEquals(0, summary.validIntervalCount)
        assertTrue(summary.increases.isEmpty())
        assertTrue(summary.intervals.isEmpty())
    }

    @Test
    fun `single record`() {
        val records = listOf(
            createRecord(remainingBalance = 100.0, sampledAt = 1000L)
        )
        val summary = ElectricUsageCalculator.calculate(records)
        assertEquals(100.0, summary.latestBalance!!, 0.001)
        assertEquals(1000L, summary.firstSampleAt)
        assertEquals(1000L, summary.latestSampleAt)
        assertEquals(0.0, summary.totalConsumed, 0.001)
        assertEquals(0, summary.validIntervalCount)
    }

    @Test
    fun `two records with balance decrease`() {
        val now = System.currentTimeMillis()
        val dayMs = 24 * 60 * 60 * 1000L
        val records = listOf(
            createRecord(remainingBalance = 100.0, sampledAt = now - dayMs),
            createRecord(remainingBalance = 90.0, sampledAt = now)
        )
        val summary = ElectricUsageCalculator.calculate(records)
        assertEquals(90.0, summary.latestBalance!!, 0.001)
        assertEquals(10.0, summary.totalConsumed, 0.001)
        assertEquals(1, summary.validIntervalCount)
        assertNotNull(summary.averageDailyConsumption)
        assertEquals(10.0, summary.averageDailyConsumption!!, 0.001)
    }

    @Test
    fun `multiple consecutive decreases`() {
        val now = System.currentTimeMillis()
        val dayMs = 24 * 60 * 60 * 1000L
        val records = listOf(
            createRecord(remainingBalance = 100.0, sampledAt = now - 3 * dayMs),
            createRecord(remainingBalance = 90.0, sampledAt = now - 2 * dayMs),
            createRecord(remainingBalance = 80.0, sampledAt = now - dayMs),
            createRecord(remainingBalance = 70.0, sampledAt = now)
        )
        val summary = ElectricUsageCalculator.calculate(records)
        assertEquals(70.0, summary.latestBalance!!, 0.001)
        assertEquals(30.0, summary.totalConsumed, 0.001)
        assertEquals(3, summary.validIntervalCount)
        assertEquals(10.0, summary.averageDailyConsumption!!, 0.001)
    }

    @Test
    fun `balance unchanged`() {
        val now = System.currentTimeMillis()
        val dayMs = 24 * 60 * 60 * 1000L
        val records = listOf(
            createRecord(remainingBalance = 100.0, sampledAt = now - dayMs),
            createRecord(remainingBalance = 100.0, sampledAt = now)
        )
        val summary = ElectricUsageCalculator.calculate(records)
        assertEquals(100.0, summary.latestBalance!!, 0.001)
        assertEquals(0.0, summary.totalConsumed, 0.001)
        assertEquals(0, summary.validIntervalCount)
        assertNull(summary.averageDailyConsumption)
    }

    @Test
    fun `balance increase (recharge)`() {
        val now = System.currentTimeMillis()
        val dayMs = 24 * 60 * 60 * 1000L
        val records = listOf(
            createRecord(remainingBalance = 50.0, sampledAt = now - dayMs),
            createRecord(remainingBalance = 150.0, sampledAt = now)
        )
        val summary = ElectricUsageCalculator.calculate(records)
        assertEquals(150.0, summary.latestBalance!!, 0.001)
        assertEquals(0.0, summary.totalConsumed, 0.001)
        assertEquals(0, summary.validIntervalCount)
        assertEquals(1, summary.increases.size)
        assertEquals(100.0, summary.increases[0].amount, 0.001)
    }

    @Test
    fun `decrease then increase then decrease`() {
        val now = System.currentTimeMillis()
        val dayMs = 24 * 60 * 60 * 1000L
        val records = listOf(
            createRecord(remainingBalance = 100.0, sampledAt = now - 3 * dayMs),
            createRecord(remainingBalance = 80.0, sampledAt = now - 2 * dayMs),
            createRecord(remainingBalance = 200.0, sampledAt = now - dayMs),
            createRecord(remainingBalance = 190.0, sampledAt = now)
        )
        val summary = ElectricUsageCalculator.calculate(records)
        assertEquals(190.0, summary.latestBalance!!, 0.001)
        assertEquals(30.0, summary.totalConsumed, 0.001)
        assertEquals(2, summary.validIntervalCount)
        assertEquals(1, summary.increases.size)
        assertEquals(120.0, summary.increases[0].amount, 0.001)
    }

    @Test
    fun `records out of order`() {
        val now = System.currentTimeMillis()
        val dayMs = 24 * 60 * 60 * 1000L
        val records = listOf(
            createRecord(remainingBalance = 90.0, sampledAt = now),
            createRecord(remainingBalance = 100.0, sampledAt = now - dayMs)
        )
        val summary = ElectricUsageCalculator.calculate(records)
        assertEquals(90.0, summary.latestBalance!!, 0.001)
        assertEquals(10.0, summary.totalConsumed, 0.001)
    }

    @Test
    fun `records with same timestamp`() {
        val now = System.currentTimeMillis()
        val records = listOf(
            createRecord(remainingBalance = 100.0, sampledAt = now),
            createRecord(remainingBalance = 90.0, sampledAt = now)
        )
        val summary = ElectricUsageCalculator.calculate(records)
        assertEquals(0.0, summary.totalConsumed, 0.001)
        assertEquals(0, summary.validIntervalCount)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `different meterKeys should not mix`() {
        val now = System.currentTimeMillis()
        val dayMs = 24 * 60 * 60 * 1000L
        val records = listOf(
            createRecord(meterKey = "HEFEI:1:101", remainingBalance = 100.0, sampledAt = now - dayMs),
            createRecord(meterKey = "HEFEI:1:102", remainingBalance = 50.0, sampledAt = now)
        )
        ElectricUsageCalculator.calculate(records)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `different meterKeys should not mix in recent 7 days`() {
        val now = System.currentTimeMillis()
        val dayMs = 24 * 60 * 60 * 1000L
        val records = listOf(
            createRecord(meterKey = "HEFEI:1:101", remainingBalance = 100.0, sampledAt = now - dayMs),
            createRecord(meterKey = "HEFEI:1:102", remainingBalance = 50.0, sampledAt = now)
        )
        ElectricUsageCalculator.calculateRecent7DaysConsumption(records, now)
    }

    @Test
    fun `average daily consumption calculation`() {
        val now = System.currentTimeMillis()
        val dayMs = 24 * 60 * 60 * 1000L
        val records = listOf(
            createRecord(remainingBalance = 100.0, sampledAt = now - 2 * dayMs),
            createRecord(remainingBalance = 80.0, sampledAt = now - dayMs),
            createRecord(remainingBalance = 70.0, sampledAt = now)
        )
        val summary = ElectricUsageCalculator.calculate(records)
        assertEquals(30.0, summary.totalConsumed, 0.001)
        assertEquals(15.0, summary.averageDailyConsumption!!, 0.001)
    }

    @Test
    fun `estimated remaining days with positive balance`() {
        val now = System.currentTimeMillis()
        val dayMs = 24 * 60 * 60 * 1000L
        val records = listOf(
            createRecord(remainingBalance = 100.0, sampledAt = now - dayMs),
            createRecord(remainingBalance = 90.0, sampledAt = now)
        )
        val summary = ElectricUsageCalculator.calculate(records)
        assertNotNull(summary.estimatedRemainingDays)
        assertEquals(9.0, summary.estimatedRemainingDays!!, 0.1)
    }

    @Test
    fun `estimated remaining days null when no consumption`() {
        val now = System.currentTimeMillis()
        val dayMs = 24 * 60 * 60 * 1000L
        val records = listOf(
            createRecord(remainingBalance = 100.0, sampledAt = now - dayMs),
            createRecord(remainingBalance = 100.0, sampledAt = now)
        )
        val summary = ElectricUsageCalculator.calculate(records)
        assertNull(summary.estimatedRemainingDays)
    }

    @Test
    fun `estimated remaining days null when balance is negative`() {
        val now = System.currentTimeMillis()
        val dayMs = 24 * 60 * 60 * 1000L
        val records = listOf(
            createRecord(remainingBalance = 10.0, sampledAt = now - dayMs),
            createRecord(remainingBalance = -5.0, sampledAt = now)
        )
        val summary = ElectricUsageCalculator.calculate(records)
        assertNull(summary.estimatedRemainingDays)
    }

    @Test
    fun `estimated remaining days null when balance is zero`() {
        val now = System.currentTimeMillis()
        val dayMs = 24 * 60 * 60 * 1000L
        val records = listOf(
            createRecord(remainingBalance = 10.0, sampledAt = now - dayMs),
            createRecord(remainingBalance = 0.0, sampledAt = now)
        )
        val summary = ElectricUsageCalculator.calculate(records)
        assertNull(summary.estimatedRemainingDays)
    }

    @Test
    fun `recent 7 days consumption`() {
        val now = System.currentTimeMillis()
        val dayMs = 24 * 60 * 60 * 1000L
        val records = listOf(
            createRecord(remainingBalance = 100.0, sampledAt = now - 10 * dayMs),
            createRecord(remainingBalance = 90.0, sampledAt = now - 5 * dayMs),
            createRecord(remainingBalance = 80.0, sampledAt = now)
        )
        val recent7 = ElectricUsageCalculator.calculateRecent7DaysConsumption(records, now)
        assertTrue(recent7 > 0)
        assertTrue(recent7 <= 20.0)
    }

    @Test
    fun `recent 7 days consumption with no recent data`() {
        val now = System.currentTimeMillis()
        val dayMs = 24 * 60 * 60 * 1000L
        val records = listOf(
            createRecord(remainingBalance = 100.0, sampledAt = now - 30 * dayMs),
            createRecord(remainingBalance = 90.0, sampledAt = now - 20 * dayMs)
        )
        val recent7 = ElectricUsageCalculator.calculateRecent7DaysConsumption(records, now)
        assertEquals(0.0, recent7, 0.001)
    }

    @Test
    fun `float epsilon threshold`() {
        val now = System.currentTimeMillis()
        val dayMs = 24 * 60 * 60 * 1000L
        val records = listOf(
            createRecord(remainingBalance = 100.0, sampledAt = now - dayMs),
            createRecord(remainingBalance = 99.998, sampledAt = now)
        )
        val summary = ElectricUsageCalculator.calculate(records)
        assertEquals(0, summary.validIntervalCount)
    }

    @Test
    fun `very short time interval`() {
        val now = System.currentTimeMillis()
        val records = listOf(
            createRecord(remainingBalance = 100.0, sampledAt = now - 1000),
            createRecord(remainingBalance = 90.0, sampledAt = now)
        )
        val summary = ElectricUsageCalculator.calculate(records)
        assertEquals(1, summary.validIntervalCount)
        assertTrue(summary.averageDailyConsumption!! > 0)
    }

    @Test
    fun `daily consumption keeps short interval as daily total`() {
        val records = listOf(
            createRecord(remainingBalance = 100.0, sampledAt = timeAt(day = 25, hour = 10)),
            createRecord(remainingBalance = 99.0, sampledAt = timeAt(day = 25, hour = 10, minute = 30))
        )
        val summary = ElectricUsageCalculator.calculate(records)
        assertEquals(1, summary.dailyConsumptions.size)
        assertEquals(1.0, summary.dailyConsumptions.first().consumed, 0.001)
    }

    @Test
    fun `daily consumption distributes cross-day interval by duration`() {
        val records = listOf(
            createRecord(remainingBalance = 100.0, sampledAt = timeAt(day = 25, hour = 12)),
            createRecord(remainingBalance = 88.0, sampledAt = timeAt(day = 26, hour = 12))
        )
        val summary = ElectricUsageCalculator.calculate(records)
        assertEquals(2, summary.dailyConsumptions.size)
        assertEquals(6.0, summary.dailyConsumptions[0].consumed, 0.001)
        assertEquals(6.0, summary.dailyConsumptions[1].consumed, 0.001)
    }

    @Test
    fun `long time no change`() {
        val now = System.currentTimeMillis()
        val dayMs = 24 * 60 * 60 * 1000L
        val records = listOf(
            createRecord(remainingBalance = 100.0, sampledAt = now - 365 * dayMs),
            createRecord(remainingBalance = 100.0, sampledAt = now)
        )
        val summary = ElectricUsageCalculator.calculate(records)
        assertEquals(0.0, summary.totalConsumed, 0.001)
        assertNull(summary.averageDailyConsumption)
    }

    @Test
    fun `same meterKey normal statistics`() {
        val now = System.currentTimeMillis()
        val dayMs = 24 * 60 * 60 * 1000L
        val records = listOf(
            createRecord(meterKey = "HEFEI:1:101", remainingBalance = 100.0, sampledAt = now - 2 * dayMs),
            createRecord(meterKey = "HEFEI:1:101", remainingBalance = 80.0, sampledAt = now - dayMs),
            createRecord(meterKey = "HEFEI:1:101", remainingBalance = 60.0, sampledAt = now)
        )
        val summary = ElectricUsageCalculator.calculate(records)
        assertEquals(40.0, summary.totalConsumed, 0.001)
        assertEquals(2, summary.validIntervalCount)
    }

    @Test
    fun `recharge interval does not produce negative consumption`() {
        val now = System.currentTimeMillis()
        val dayMs = 24 * 60 * 60 * 1000L
        val records = listOf(
            createRecord(remainingBalance = 10.0, sampledAt = now - 2 * dayMs),
            createRecord(remainingBalance = 100.0, sampledAt = now - dayMs),
            createRecord(remainingBalance = 90.0, sampledAt = now)
        )
        val summary = ElectricUsageCalculator.calculate(records)
        assertEquals(10.0, summary.totalConsumed, 0.001)
        assertEquals(1, summary.validIntervalCount)
        assertEquals(1, summary.increases.size)
        assertEquals(90.0, summary.increases[0].amount, 0.001)
    }
}
