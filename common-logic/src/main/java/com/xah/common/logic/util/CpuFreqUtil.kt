package com.xah.common.logic.util

import java.io.File

object CpuFreqUtil {
    private const val CPUINFO_MAX_FREQ = "cpuinfo_max_freq"
    private const val SCALING_CUR_FREQ = "scaling_cur_freq"

    // 硬件最大CPU频率 单位kHz
    fun getCpuMaxFreq() = allCores().mapNotNull { readFreq(it, CPUINFO_MAX_FREQ) }.maxOrNull()

    // 当前最大CPU频率 单位kHz
    fun getCurrentCpuMaxFreq() = allCores().mapNotNull { readFreq(it, SCALING_CUR_FREQ) }.maxOrNull()

    private fun allCores() = (0 until Runtime.getRuntime().availableProcessors()).toList()

    private fun readFreq(coreIndex: Int, fileName: String): Long? {
        return try {
            File("/sys/devices/system/cpu/cpu$coreIndex/cpufreq/$fileName")
                .readText()
                .trim()
                .toLong()
        } catch (e: Exception) {
            LogUtil.error(e)
            null
        }
    }
}