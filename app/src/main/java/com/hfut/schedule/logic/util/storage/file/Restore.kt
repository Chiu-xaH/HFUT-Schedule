package com.hfut.schedule.logic.util.storage.file

import android.app.Activity
import android.content.Context
import android.os.Environment
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.sys.PermissionSet
import com.hfut.schedule.logic.util.sys.showToast
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

// 打包整个 dataDir到下载目录以"聚在工大备份_时间戳.zip"命名
fun backupData(
    activity: Activity,
    context: Context
): Boolean {
    val fileName = "${MyApplication.APP_NAME}_备份_${System.currentTimeMillis()}.zip"

    return try {
        // ---- Android 6–9（旧版）使用外部存储目录，需要权限 ----
        PermissionSet.checkAndRequestStoragePermission(activity)

        val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadDir.exists()) downloadDir.mkdirs()

        val outputFile = File(downloadDir, fileName)

        ZipOutputStream(BufferedOutputStream(FileOutputStream(outputFile))).use { zos ->
            val root = context.dataDir
            val rootLen = root.absolutePath.length + 1
            zipDirectorySafe(root, rootLen, zos)
        }

        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

private val EXCLUDE_DIRS = setOf(
    "cache",
    "app_webview",
    "app_textures",
    "code_cache",
    "no_backup"
)

// 递归打包
private fun zipDirectorySafe(
    file: File,
    rootPathLength: Int,
    zos: ZipOutputStream
) {
    if (!file.exists()) return

    // 获取相对路径
    val relativePath = file.absolutePath.drop(file.parentFile?.absolutePath?.length?.plus(1) ?: 0)
        .split(File.separator)[0]  // 根目录下一级文件夹名

    // 如果是要排除的目录，直接跳过
    if (EXCLUDE_DIRS.contains(relativePath)) return

    val fullPath = file.absolutePath
    val entryName = fullPath.drop(rootPathLength)

    if (file.isDirectory) {
        if (entryName.isNotEmpty()) {
            zos.putNextEntry(ZipEntry("$entryName/"))
            zos.closeEntry()
        }
        file.listFiles()?.forEach { child ->
            zipDirectorySafe(child, rootPathLength, zos)
        }
    } else {
        FileInputStream(file).use { fis ->
            zos.putNextEntry(ZipEntry(entryName))
            fis.copyTo(zos, 1024)
            zos.closeEntry()
        }
    }
}


fun killApp() {
    android.os.Process.killProcess(android.os.Process.myPid())
    kotlin.system.exitProcess(0)
}
// 恢复 传入文件，然后killApp
fun restoreData(
    activity: Activity,
    context: Context,
    backupFile: File,
) {
    PermissionSet.checkAndRequestStoragePermission(activity)
    try {
        if (!backupFile.exists() || !backupFile.canRead()) {
            showToast("空文件")
            return
        }

        // 检查是否是合法 ZIP
        ZipFile(backupFile).use { zip ->
            if (!zip.entries().hasMoreElements()) {
                showToast("空文件")
                return
            }
        }

        val dataDir = context.dataDir

        // 清空 dataDir
        safeClearDataDir(dataDir)

        // 解压
        ZipInputStream(FileInputStream(backupFile)).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {

                // 去掉前导 /
                val safeName = entry.name.removePrefix("/")

                // 构造输出文件
                val outFile = File(dataDir, safeName)

                // 安全校验（防止路径穿越）
                val canonicalPath = outFile.canonicalPath
                val canonicalDataDir = dataDir.canonicalPath
                if (!canonicalPath.startsWith(canonicalDataDir)) {
                    throw SecurityException("非法路径: ${entry.name}")
                }

                if (entry.isDirectory) {
                    outFile.mkdirs()
                } else {
                    outFile.parentFile?.mkdirs()
                    FileOutputStream(outFile).use { fos ->
                        zis.copyTo(fos)
                    }
                }

                zis.closeEntry()
                entry = zis.nextEntry
            }
        }

        showToast("恢复完成，杀停App")
        killApp()
    } catch (e: Exception) {
        e.printStackTrace()
        showToast("恢复失败")
    }
}

private fun safeClearDataDir(dataDir: File) {
    dataDir.listFiles()?.forEach { file ->
        // 不删 cache、app_webview、app_textures、code_cache
        if (EXCLUDE_DIRS.contains(file.name)) return@forEach
        file.deleteRecursively()
    }
}
