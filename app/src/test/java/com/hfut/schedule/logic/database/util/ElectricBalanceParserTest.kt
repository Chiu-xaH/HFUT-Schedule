package com.hfut.schedule.logic.database.util

import com.hfut.schedule.logic.enumeration.CampusRegion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ElectricBalanceParserTest {

    @Test
    fun `parseHefeiBalance normal amount`() {
        val result = ElectricBalanceParser.parseHefeiBalance("123.45")
        assertEquals(123.45, result!!, 0.001)
    }

    @Test
    fun `parseHefeiBalance with RMB symbol`() {
        val result = ElectricBalanceParser.parseHefeiBalance("￥123.45")
        assertEquals(123.45, result!!, 0.001)
    }

    @Test
    fun `parseHefeiBalance with Yen symbol`() {
        val result = ElectricBalanceParser.parseHefeiBalance("¥50.00")
        assertEquals(50.0, result!!, 0.001)
    }

    @Test
    fun `parseHefeiBalance empty string`() {
        val result = ElectricBalanceParser.parseHefeiBalance("")
        assertNull(result)
    }

    @Test
    fun `parseHefeiBalance placeholder XX`() {
        val result = ElectricBalanceParser.parseHefeiBalance("XX.XX")
        assertNull(result)
    }

    @Test
    fun `parseHefeiBalance placeholder dashes`() {
        val result = ElectricBalanceParser.parseHefeiBalance("--")
        assertNull(result)
    }

    @Test
    fun `parseHefeiBalance illegal string`() {
        val result = ElectricBalanceParser.parseHefeiBalance("abc")
        assertNull(result)
    }

    @Test
    fun `parseHefeiBalance with spaces`() {
        val result = ElectricBalanceParser.parseHefeiBalance("  123.45  ")
        assertEquals(123.45, result!!, 0.001)
    }

    @Test
    fun `parseXuanchengBalance with half-width colon`() {
        val result = ElectricBalanceParser.parseXuanchengBalance("剩余金额:12.34")
        assertEquals(12.34, result!!, 0.001)
    }

    @Test
    fun `parseXuanchengBalance with full-width colon`() {
        val result = ElectricBalanceParser.parseXuanchengBalance("剩余金额：12.34")
        assertEquals(12.34, result!!, 0.001)
    }

    @Test
    fun `parseXuanchengBalance with room info`() {
        val result = ElectricBalanceParser.parseXuanchengBalance("房间号 3001011111 剩余金额:45.67")
        assertEquals(45.67, result!!, 0.001)
    }

    @Test
    fun `parseXuanchengBalance without remaining amount`() {
        val result = ElectricBalanceParser.parseXuanchengBalance("无法获取房间信息")
        assertNull(result)
    }

    @Test
    fun `parseXuanchengBalance with space after colon`() {
        val result = ElectricBalanceParser.parseXuanchengBalance("剩余金额: 99.99")
        assertEquals(99.99, result!!, 0.001)
    }

    @Test
    fun `parseXuanchengBalance with spaces around keyword`() {
        val result = ElectricBalanceParser.parseXuanchengBalance("剩余金额 50.00")
        assertEquals(50.0, result!!, 0.001)
    }

    @Test
    fun `parse with null value`() {
        val result = ElectricBalanceParser.parse(null, CampusRegion.HEFEI)
        assertNull(result)
    }

    @Test
    fun `parse with blank value`() {
        val result = ElectricBalanceParser.parse("   ", CampusRegion.HEFEI)
        assertNull(result)
    }

    @Test
    fun `parse NaN value`() {
        val result = ElectricBalanceParser.parse("NaN", CampusRegion.HEFEI)
        assertNull(result)
    }

    @Test
    fun `parse Infinity value`() {
        val result = ElectricBalanceParser.parse("Infinity", CampusRegion.HEFEI)
        assertNull(result)
    }

    @Test
    fun `parse negative value`() {
        val result = ElectricBalanceParser.parse("-50.00", CampusRegion.HEFEI)
        assertEquals(-50.0, result!!, 0.001)
    }

    @Test
    fun `extremely negative balance is rejected by threshold`() {
        // -2000.00 is parsed but rejected by validateBalance (threshold < -1000.0)
        assertNull(ElectricBalanceParser.parseHefeiBalance("-2000.00"))
    }

    @Test
    fun `parseXuanchengBalance accepts reasonable negative balance`() {
        val result = ElectricBalanceParser.parseXuanchengBalance("剩余金额:-5.20")
        assertEquals(-5.20, result!!, 0.001)
    }

    @Test
    fun `parseXuanchengBalance accepts negative balance with full width colon`() {
        val result = ElectricBalanceParser.parseXuanchengBalance("剩余金额： -5.20")
        assertEquals(-5.20, result!!, 0.001)
    }

    @Test
    fun `parseXuanchengBalance rejects extremely negative balance by threshold`() {
        // -2000.00 is parsed by regex but rejected by validateBalance (threshold < -1000.0)
        assertNull(ElectricBalanceParser.parseXuanchengBalance("剩余金额:-2000.00"))
    }

    @Test
    fun `parseXuanchengBalance negative with room info`() {
        val result = ElectricBalanceParser.parseXuanchengBalance("房间号 3001011111 剩余金额:-3.50")
        assertEquals(-3.50, result!!, 0.001)
    }

    @Test
    fun `parseHefeiBalance negative value`() {
        val result = ElectricBalanceParser.parseHefeiBalance("-5.20")
        assertEquals(-5.20, result!!, 0.001)
    }
}
