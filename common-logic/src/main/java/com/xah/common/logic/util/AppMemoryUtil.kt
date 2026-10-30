package com.xah.common.logic.util

import java.io.File

object AppMemoryUtil {

    fun getRssMb() = getProcStatus("VmRSS")?.div(1024L)

    // 单位kB
    private fun getProcStatus(field: String): Long? = try {
        File("/proc/self/status").useLines { lines ->
            lines.firstOrNull { it.startsWith("$field:") }
                ?.trim()
                ?.split("\\s+".toRegex())
                ?.getOrNull(1)
                ?.toLong()
        }
    } catch (e: Exception) {
        LogUtil.error(e)
        null
    }
}
