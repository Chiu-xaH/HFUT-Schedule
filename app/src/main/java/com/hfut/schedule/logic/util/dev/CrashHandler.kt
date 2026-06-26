package com.hfut.schedule.logic.util.dev

import android.os.Environment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.dev.ExceptionHelper.getKeyStackTraceDesc
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.xah.common.logic.util.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object CrashHandler : Thread.UncaughtExceptionHandler {

    private var defaultHandler: Thread.UncaughtExceptionHandler? = null
    var isLoggingEnabled by mutableStateOf(false)

    fun init() {
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    fun enableLogging() {
        init()
        isLoggingEnabled = true
    }

    fun disableLogging() {
        isLoggingEnabled = false
    }

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        if (isLoggingEnabled) {
            saveCrashLog(throwable)
        }
        defaultHandler?.uncaughtException(thread, throwable)
    }


    // 文件名不让用一些特殊字符
    private val formatterForFile = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
    private fun saveCrashLog(throwable: Throwable) {
        try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val dateTime = LocalDateTime.now()
            val filename = "${MyApplication.APP_NAME}_崩溃日志_${System.currentTimeMillis()}.log"
            val file = File(downloadsDir, filename)
            val log = """
                软件版本: ${AppVersion.getVersionName()} (${AppVersion.getVersionCode()})
                系统版本: ${AppVersion.sdkInt}
                时间: ${dateTime.format(DateTimeManager.formatterAll)}
                用户: ${getPersonInfo().studentId ?: "游客"}
                ${getKeyStackTraceDesc(throwable)}
                堆栈: ${throwable.stackTraceToString()}
            """.trimIndent()
            file.appendText(log) // 同步写文件
            showToast("已保存到Download/${filename}")
        } catch (e: Exception) {
            LogUtil.error(e)
        }
    }
}
