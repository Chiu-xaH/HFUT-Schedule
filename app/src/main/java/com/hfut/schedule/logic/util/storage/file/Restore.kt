package com.hfut.schedule.logic.util.storage.file

import android.content.Context
import com.hfut.schedule.logic.util.sys.showToast
import java.io.File

suspend fun restoreAndExit(context: Context, backupFile: File) {
    // 解压并覆盖数据
    restoreAppData(context, backupFile)
    showToast("恢复完成，将杀死App")
    // 确保没有任何机会写回旧缓存
    killApp()
}

suspend fun restoreAppData(context: Context,backupFile: File) {
    showToast("正在开发")
}

fun killApp() {
    android.os.Process.killProcess(android.os.Process.myPid())
    kotlin.system.exitProcess(0)
}