package com.hfut.schedule.logic.util.dev

import com.hfut.schedule.logic.util.other.AppVersion
import java.io.PrintWriter
import java.io.StringWriter

object ExceptionHelper {
    private val at = "at "

    @JvmStatic
    fun getKeyStackTrace(e: Throwable): KeyThrowable {
        if (e is AnrDiagnosticException) {
            return KeyThrowable(
                type = "系统 ANR",
                cause = e.message,
                position = "进程: ${e.report.processName}"
            )
        }

        val appPackage = AppVersion.appPackageName
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        e.printStackTrace(pw)
        val lines = sw.toString().lines()
        val appStackLine = lines.firstOrNull { it.trim().startsWith("$at$appPackage") }?.trim()?.substringAfter(at)

        val firstLine = lines.firstOrNull()?.trim() ?: "Unknown Exception"
        val type = firstLine.substringBefore(": ")
        var cause : String? = firstLine.substringAfter(": ")
        if(type == cause) {
            cause = null
        }

        return KeyThrowable(type = type, cause = cause, position = appStackLine)
    }

    @JvmStatic
    fun getKeyStackTraceDesc(e: Throwable): String {
        val data = getKeyStackTrace(e)
        val firstLine = data.type
        val appStackLine = data.position

        return "类型: ${firstLine}\n原因: ${data.cause ?: "--"}\n位置: ${appStackLine?:"--"}"
    }

    @JvmStatic
    fun getDiagnosticText(e: Throwable): String = buildString {
        append(e.stackTraceToString())
        if (e is AnrDiagnosticException) {
            appendLine()
            appendLine()
            appendLine("系统 ANR Trace:")
            append(e.report.traceText ?: "不可用")
        }
    }

    data class KeyThrowable(
        val type : String,
        val cause : String?,
        val position : String?,
    )

}
