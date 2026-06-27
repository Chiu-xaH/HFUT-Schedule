package com.hfut.schedule.logic.database.util

import com.hfut.schedule.logic.enumeration.CampusRegion
import com.xah.common.logic.util.LogUtil

object ElectricBalanceParser {

    private val AMOUNT_REGEX = Regex("""剩余金额[：:\s]*(-?[0-9]+(?:\.[0-9]+)?)""")

    fun parse(rawValue: String?, campusRegion: CampusRegion): Double? {
        if (rawValue.isNullOrBlank()) return null
        return when (campusRegion) {
            CampusRegion.HEFEI -> parseHefeiBalance(rawValue)
            CampusRegion.XUANCHENG -> parseXuanchengBalance(rawValue)
        }
    }

    fun parseHefeiBalance(rawValue: String): Double? {
        return try {
            val cleaned = rawValue.trim()
                .replace("￥", "")
                .replace("¥", "")
                .replace(" ", "")
                .trim()
            if (cleaned.isEmpty() || cleaned == "XX.XX" || cleaned == "--") {
                return null
            }
            val value = cleaned.toDoubleOrNull()
            validateBalance(value)
        } catch (e: Exception) {
            LogUtil.error(e, "合肥余额解析失败: $rawValue")
            null
        }
    }

    fun parseXuanchengBalance(rawValue: String): Double? {
        return try {
            val match = AMOUNT_REGEX.find(rawValue)
            if (match != null) {
                val value = match.groupValues[1].toDoubleOrNull()
                return validateBalance(value)
            }
            val cleaned = rawValue.trim()
                .replace("￥", "")
                .replace("¥", "")
                .replace(" ", "")
                .trim()
            val value = cleaned.toDoubleOrNull()
            validateBalance(value)
        } catch (e: Exception) {
            LogUtil.error(e, "宣城余额解析失败: $rawValue")
            null
        }
    }

    private fun validateBalance(value: Double?): Double? {
        if (value == null) return null
        if (value.isNaN() || value.isInfinite()) return null
        if (value < -1000.0) return null
        return value
    }
}
