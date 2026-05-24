package com.hfut.schedule.logic.util.dev

import android.os.Environment
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.dev.ExceptionHelper.getKeyStackTraceDesc
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.xah.shared.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime

suspend fun saveErrorLog() : Int = withContext(Dispatchers.IO) {
    try {
        val list = LogUtil.getCachedLogs()
        if(list.isEmpty()) {
            showToast("暂无错误日志，请操作后再来")
            return@withContext 0
        }
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val dateTime = LocalDateTime.now()
        val filename = "${MyApplication.APP_NAME}_错误日志_${System.currentTimeMillis()}.log"
        val file = File(downloadsDir, filename)

        val result = StringBuilder()

        val head = """
                软件版本: ${AppVersion.getVersionName()} (${AppVersion.getVersionCode()})
                系统版本: ${AppVersion.sdkInt}
                时间: ${dateTime.format(DateTimeManager.formatterAll)}
                用户: ${getPersonInfo().studentId ?: "游客"}
                
            """.trimIndent()
        result.append(head)
        list.forEachIndexed { index, entry ->
            val throwable = entry.throwable
            val log = buildString {
                appendLine("--[${index + 1}]------------------------------------------------")
                appendLine(getKeyStackTraceDesc(throwable))
                appendLine("时间: ${entry.timestamp.format(DateTimeManager.formatterAll)}")
                appendLine("堆栈: ${throwable.stackTraceToString()}")
                appendLine()
            }
            result.append(log)
        }
        // 同步写文件
        file.appendText(result.toString())
        showToast("已保存到Download/${filename}")
        LogUtil.clearCache()
        return@withContext list.size
    } catch (e: Exception) {
        LogUtil.error(e)
        return@withContext 0
    }
}