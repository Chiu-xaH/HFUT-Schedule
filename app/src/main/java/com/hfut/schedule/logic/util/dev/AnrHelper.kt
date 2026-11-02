package com.hfut.schedule.logic.util.dev

import android.app.ActivityManager
import android.app.Application
import android.app.ApplicationExitInfo
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.model.anr.AnrDiagnosticException
import com.hfut.schedule.logic.model.anr.AnrReport
import com.hfut.schedule.logic.util.other.AppVersion
import com.xah.common.logic.util.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AnrHelper {

    private const val PREFS_NAME = "anr_exit_info"
    private const val KEY_LAST_TIMESTAMP = "last_anr_timestamp"
    private const val MAX_EXIT_REASONS = 10
    private const val MAX_TRACE_CHARS = 256 * 1024

    @RequiresApi(Build.VERSION_CODES.R)
    private suspend fun collect(): List<AnrReport> = withContext(Dispatchers.IO) {
        if (AppVersion.sdkInt < Build.VERSION_CODES.R) {
            return@withContext emptyList()
        }
        if (Application.getProcessName() != AppVersion.appPackageName) {
            return@withContext emptyList()
        }

        return@withContext try {
            val prefs = MyApplication.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val lastTimestamp = prefs.getLong(KEY_LAST_TIMESTAMP, 0L)
            val activityManager = MyApplication.context.getSystemService(ActivityManager::class.java) ?: return@withContext emptyList()
            val anrRecords = activityManager
                .getHistoricalProcessExitReasons(MyApplication.context.packageName, 0, MAX_EXIT_REASONS)
                .asSequence()
                .filter { it.reason == ApplicationExitInfo.REASON_ANR }
                .filter { it.timestamp > lastTimestamp }
                .sortedBy { it.timestamp }
                .toList()

            if (anrRecords.isEmpty()) {
                return@withContext emptyList()
            }

            val reports = anrRecords.map { exitInfo ->
                val traceText = try {
                    readTrace(exitInfo)
                } catch (e: Exception) {
                    LogUtil.error(e)
                    null
                }
                AnrReport(
                    occurredAt = exitInfo.timestamp,
                    processName = exitInfo.processName,
                    description = exitInfo.description,
                    traceText = traceText
                )
            }

            val saved = prefs.edit()
                .putLong(KEY_LAST_TIMESTAMP, anrRecords.maxOf { it.timestamp })
                .commit()
            reports
        } catch (e: Exception) {
            LogUtil.error(e)
            return@withContext emptyList()
        }
    }

    suspend fun logAnrTraces() {
        collect().forEach { report ->
            LogUtil.error(AnrDiagnosticException(report))
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun readTrace(exitInfo: ApplicationExitInfo): String? {
        return exitInfo.traceInputStream?.bufferedReader()?.use { reader ->
            val buffer = CharArray(MAX_TRACE_CHARS + 1)
            var total = 0
            while (total < buffer.size) {
                val count = reader.read(buffer, total, buffer.size - total)
                if (count == -1) {
                    break
                }
                total += count
            }

            buildString {
                append(buffer, 0, minOf(total, MAX_TRACE_CHARS))
                if (total > MAX_TRACE_CHARS) {
                    appendLine()
                    append("... trace 已截断")
                }
            }
        }
    }
}

