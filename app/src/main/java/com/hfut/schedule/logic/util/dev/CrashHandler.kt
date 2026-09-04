package com.hfut.schedule.logic.util.dev

import android.os.Environment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.model.supabase.SupabaseUserTrackRequest
import com.hfut.schedule.logic.util.dev.ExceptionHelper.KeyThrowable
import com.hfut.schedule.logic.util.dev.ExceptionHelper.getKeyStackTraceDesc
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.xah.common.logic.util.LogUtil
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 应用崩溃时的处理机制，每次崩溃都会上报其崩溃日志及相关信息，帮助开发者更好地发现问题；当应用崩溃后再次启动将唤起修复页面，提供日志导出、排查措施以及反馈渠道；
 */
object CrashHandler : Thread.UncaughtExceptionHandler {

    private var defaultHandler: Thread.UncaughtExceptionHandler? = null
    var isLoggingEnabled by mutableStateOf(false)
        private set

    private fun init() {
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
            createCrashLog(throwable)?.let { crashLog ->
                saveCrashLogFile(crashLog)
            }
        }
        defaultHandler?.uncaughtException(thread, throwable)
    }


    // 文件名不让用一些特殊字符
    private val formatterForFile = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
    private fun saveCrashLogFile(crashLog : String) {
        try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val filename = "${MyApplication.APP_NAME}_崩溃日志_${System.currentTimeMillis()}.log"
            val file = File(downloadsDir, filename)
            file.appendText(crashLog) // 同步写文件
            showToast("已保存到Download/${filename}")
        } catch (e: Exception) {
            LogUtil.error(e)
        }
    }

    fun uploadCrashLog() {
        // 在修复页面将上次的崩溃日志回传回去
    }

    private fun saveCrashLog(crashLog: String) {
        //MAX_CRASH_TIME
    }

    private fun createCrashLog(throwable: Throwable) : String? {
        try {
            val dateTime = LocalDateTime.now()
            val log = """
                软件版本: ${AppVersion.getVersionName()} (${AppVersion.getVersionCode()})
                系统版本: ${AppVersion.sdkInt}
                时间: ${dateTime.format(DateTimeManager.formatterAll)}
                用户: ${getPersonInfo().getStudentIdFinally() ?: "游客"}
                ${getKeyStackTraceDesc(throwable)}
                堆栈: ${throwable.stackTraceToString()}
            """.trimIndent()
            return log
        } catch (e: Exception) {
            LogUtil.error(e)
            return  null
        }
    }

    // 要回传的信息 发生时间，
    data class CrashTrackRequest(
        val appVersionCode : Int,
        val appVersionName : String,
        val appAbi : String,
        val timestamp : Long,
        val studentId : String?,
        val systemVersion : Int,
        val deviceName : String,
        val isHarmonyNext : Boolean,
        val throwableType : String,
        val throwableCause : String?,
        val throwablePosition : String?,
    )

}
