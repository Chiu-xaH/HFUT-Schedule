package com.hfut.schedule.logic.util.dev

import com.hfut.schedule.logic.util.other.AppVersion
import java.io.PrintWriter
import java.io.StringWriter

object ExceptionHelper {
    private val at = "at "

    @JvmStatic
    fun getKeyStackTrace(e: Throwable): KeyThrowable {
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

    data class KeyThrowable(
        val type : String,
        val cause : String?,
        val position : String?,
    )

}
