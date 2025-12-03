package com.hfut.schedule.logic.util.development

import android.os.Environment
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.logic.util.sys.Starter.openDownloadFolder
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import java.io.File

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
        // 交给系统默认处理
        defaultHandler?.uncaughtException(thread, throwable)
    }

    private fun saveCrashLog(throwable: Throwable) {
        try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val timestamp = System.currentTimeMillis() / 1000 // 秒级时间戳
            val filename = "${MyApplication.APP_NAME}_崩溃日志_${timestamp}.log"
            val file = File(downloadsDir, filename)
            val log = """
                软件版本 ${AppVersion.getVersionName()}(${AppVersion.getVersionCode()})
                系统版本 ${AppVersion.sdkInt}
                时间 ${DateTimeManager.Date_yyyy_MM_dd + "T" + DateTimeManager.Time_HH_MM_SS}
                用户 ${getPersonInfo().studentId ?: "游客"}
                ${getKeyStackTrace(throwable)}
                具体信息 ${throwable.stackTraceToString()}
            """.trimIndent()
            file.appendText(log) // 同步写文件
            showToast("已保存到Download/${filename}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
