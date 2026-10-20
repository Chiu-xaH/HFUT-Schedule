package com.hfut.schedule.logic.util.dev

import android.os.Environment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.dev.ExceptionHelper.getKeyStackTraceDesc
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.xah.common.logic.util.LogUtil
import kotlinx.coroutines.runBlocking
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object CrashHandler : Thread.UncaughtExceptionHandler {

    private var defaultHandler: Thread.UncaughtExceptionHandler? = null
    var enableSaveLogToFile by mutableStateOf(false)

    fun init() {
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        saveCrashLog(throwable)
        checkRoomMigration(throwable)
        enqueueCrash(throwable)
        defaultHandler?.uncaughtException(thread, throwable)
    }

    // crash日志放置到待发送区，崩溃超过两次，下次启动时打开特定页面，允许用户选择是否上报回传服务端或者反馈给我
    private fun enqueueCrash(throwable: Throwable) {
        // TODO
    }

    // 如果后续需要根据堆栈信息作相应操作，可改成统一做一次遍历。
    // 如果是room升级问题，禁用数据库
    private fun isRoomMigrationError(throwable: Throwable): Boolean {
        var current: Throwable? = throwable
        while (current != null) {
            if (current.message?.contains("Migration didn't properly handle:") == true) {
                return true
            }
            current = current.cause
        }
        return false
    }
    private fun checkRoomMigration(throwable: Throwable) {
        if(isRoomMigrationError(throwable)) {
            // 必须同步写回
            runBlocking {
                DataStoreManager.saveEnableDataStore(false)
            }
        }
    }

    // 文件名不让用一些特殊字符
    private val formatterForFile = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
    private fun saveCrashLog(throwable: Throwable) {
        if (!enableSaveLogToFile) {
            return
        }
        try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val dateTime = LocalDateTime.now()
            val filename = "${MyApplication.APP_NAME}_崩溃日志_${System.currentTimeMillis()}.log"
            val file = File(downloadsDir, filename)
            val log = """
                软件版本: ${AppVersion.getVersionName()} (${AppVersion.getVersionCode()})
                系统版本: ${AppVersion.sdkInt}
                时间: ${dateTime.format(DateTimeManager.formatterAll)}
                用户: ${getPersonInfo().getStudentIdFinally() ?: "游客"}
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
