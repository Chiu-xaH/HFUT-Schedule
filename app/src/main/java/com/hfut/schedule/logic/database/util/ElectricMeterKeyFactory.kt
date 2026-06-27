package com.hfut.schedule.logic.database.util

object ElectricMeterKeyFactory {

    fun hefei(buildingNumber: String, roomNumber: String): String {
        require(buildingNumber.isNotBlank()) { "buildingNumber不能为空" }
        require(roomNumber.isNotBlank()) { "roomNumber不能为空" }
        return "HEFEI:${buildingNumber.trim()}:${roomNumber.trim()}"
    }

    fun xuancheng(input: String): String {
        require(input.isNotBlank()) { "input不能为空" }
        return "XUANCHENG:${input.trim()}"
    }

    fun isValid(key: String): Boolean {
        return when {
            key.startsWith("HEFEI:") -> {
                val parts = key.split(":")
                parts.size == 3 &&
                    parts[1].isNotBlank() &&
                    parts[2].isNotBlank()
            }
            key.startsWith("XUANCHENG:") -> {
                val input = key.removePrefix("XUANCHENG:")
                input.isNotBlank()
            }
            else -> false
        }
    }
}
