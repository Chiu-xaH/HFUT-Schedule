package com.hfut.schedule.logic.utils

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import java.io.File

class CrashHandler : Thread.UncaughtExceptionHandler {
    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
    private var isLoggingEnabled = false
    init {
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    // 启动日志捕获
    fun enableLogging() {
        isLoggingEnabled = true
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    // 停止日志捕获
    fun disableLogging() {
        isLoggingEnabled = false
        Thread.setDefaultUncaughtExceptionHandler(defaultHandler)
    }


    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        if(isLoggingEnabled) {
            // 将崩溃日志保存到SharedPreferences
            saveCrashLogToPrefs(throwable)
        }
        // 如果之前有默认处理器，交给系统处理
        defaultHandler?.uncaughtException(thread, throwable)
    }

    private fun saveCrashLogToPrefs(throwable: Throwable) {
        SharePrefs.Save("logs",GetDate.Date_yyyy_MM_dd + "*\n" + throwable.stackTraceToString())
    }
}
