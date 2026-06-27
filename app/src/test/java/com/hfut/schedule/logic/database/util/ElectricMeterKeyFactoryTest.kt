package com.hfut.schedule.logic.database.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ElectricMeterKeyFactoryTest {

    @Test
    fun `hefei key format`() {
        val key = ElectricMeterKeyFactory.hefei("1", "101")
        assertEquals("HEFEI:1:101", key)
    }

    @Test
    fun `hefei key with different rooms`() {
        val key1 = ElectricMeterKeyFactory.hefei("1", "101")
        val key2 = ElectricMeterKeyFactory.hefei("1", "102")
        assertNotEquals(key1, key2)
    }

    @Test
    fun `hefei key with different buildings`() {
        val key1 = ElectricMeterKeyFactory.hefei("1", "101")
        val key2 = ElectricMeterKeyFactory.hefei("2", "101")
        assertNotEquals(key1, key2)
    }

    @Test
    fun `xuancheng key format`() {
        val key = ElectricMeterKeyFactory.xuancheng("3001011111")
        assertEquals("XUANCHENG:3001011111", key)
    }

    @Test
    fun `xuancheng key with different inputs`() {
        val key1 = ElectricMeterKeyFactory.xuancheng("3001011111")
        val key2 = ElectricMeterKeyFactory.xuancheng("3001011112")
        assertNotEquals(key1, key2)
    }

    @Test
    fun `xuancheng key with air conditioning suffix`() {
        val key1 = ElectricMeterKeyFactory.xuancheng("3001011112")
        val key2 = ElectricMeterKeyFactory.xuancheng("3001011122")
        assertNotEquals(key1, key2)
    }

    @Test
    fun `hefei and xuancheng keys are different`() {
        val hefeiKey = ElectricMeterKeyFactory.hefei("1", "101")
        val xuanchengKey = ElectricMeterKeyFactory.xuancheng("1:101")
        assertNotEquals(hefeiKey, xuanchengKey)
    }

    @Test
    fun `hefei key trims whitespace`() {
        val key = ElectricMeterKeyFactory.hefei(" 1 ", " 101 ")
        assertEquals("HEFEI:1:101", key)
    }

    @Test
    fun `xuancheng key trims whitespace`() {
        val key = ElectricMeterKeyFactory.xuancheng(" 3001011111 ")
        assertEquals("XUANCHENG:3001011111", key)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `hefei key rejects empty buildingNumber`() {
        ElectricMeterKeyFactory.hefei("", "101")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `hefei key rejects empty roomNumber`() {
        ElectricMeterKeyFactory.hefei("1", "")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `hefei key rejects both empty`() {
        ElectricMeterKeyFactory.hefei("", "")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `hefei key rejects blank buildingNumber`() {
        ElectricMeterKeyFactory.hefei("  ", "101")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `xuancheng key rejects empty input`() {
        ElectricMeterKeyFactory.xuancheng("")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `xuancheng key rejects blank input`() {
        ElectricMeterKeyFactory.xuancheng("   ")
    }

    // isValid tests

    @Test
    fun `isValid returns true for valid hefei key`() {
        assertTrue(ElectricMeterKeyFactory.isValid("HEFEI:1:101"))
    }

    @Test
    fun `isValid returns true for valid xuancheng key`() {
        assertTrue(ElectricMeterKeyFactory.isValid("XUANCHENG:3001011111"))
    }

    @Test
    fun `isValid returns false for HEFEI with empty room`() {
        assertFalse(ElectricMeterKeyFactory.isValid("HEFEI:1:"))
    }

    @Test
    fun `isValid returns false for HEFEI with empty building`() {
        assertFalse(ElectricMeterKeyFactory.isValid("HEFEI::101"))
    }

    @Test
    fun `isValid returns false for HEFEI with both empty`() {
        assertFalse(ElectricMeterKeyFactory.isValid("HEFEI::"))
    }

    @Test
    fun `isValid returns false for XUANCHENG with empty input`() {
        assertFalse(ElectricMeterKeyFactory.isValid("XUANCHENG:"))
    }

    @Test
    fun `isValid returns false for unknown prefix`() {
        assertFalse(ElectricMeterKeyFactory.isValid("OTHER:1:101"))
    }

    @Test
    fun `isValid returns false for empty string`() {
        assertFalse(ElectricMeterKeyFactory.isValid(""))
    }
}
