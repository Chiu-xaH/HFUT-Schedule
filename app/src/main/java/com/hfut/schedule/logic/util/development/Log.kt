package com.hfut.schedule.logic.util.development

import com.hfut.schedule.logic.util.other.AppVersion
import java.io.PrintWriter
import java.io.StringWriter

fun getExceptionDetail(e: Throwable): String {
    val sw = StringWriter()
    val pw = PrintWriter(sw)
    e.printStackTrace(pw)
    return sw.toString()
}

fun getKeyStackTrace(e: Throwable): String {
    val appPackage = AppVersion.appPackageName
    val sw = StringWriter()
    val pw = PrintWriter(sw)
    e.printStackTrace(pw)
    val lines = sw.toString().lines()

    val firstLine = lines.firstOrNull()?.trim() ?: "Unknown Exception"
    val appStackLine = lines.firstOrNull { it.trim().startsWith("at $appPackage") }?.trim()

    return if (appStackLine != null) {
        "原因: $firstLine\n位置: $appStackLine"
    } else {
        firstLine
    }
}


