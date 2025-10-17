package com.hfut.schedule.logic.util.storage.file

import android.content.Context
import android.os.Environment
import android.webkit.WebStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

suspend fun cleanCache(context : Context): Double = withContext(Dispatchers.IO) {
    var totalDeletedBytes = 0L

    with(context) {
        async {
            launch {
                // 删除 /Android/data/包名/files/Download
                val filesDownloadDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                filesDownloadDir?.listFiles()?.forEach {
                    totalDeletedBytes += getFileSize(it)
                    it.deleteRecursively()
                }
            }
            launch {
                // 编译缓存
                codeCacheDir.listFiles()?.forEach {
                    totalDeletedBytes += getFileSize(it)
                    it.deleteRecursively()
                }
            }
            launch {
                // 不随备份上传的文件
                noBackupFilesDir.listFiles()?.forEach {
                    totalDeletedBytes += getFileSize(it)
                    it.deleteRecursively()
                }
            }
            launch {
                // 删除 /Android/data/包名/cache
                cacheDir.listFiles()?.forEach {
                    totalDeletedBytes += getFileSize(it)
                    it.deleteRecursively()
                }
            }
            launch() {
                // WebView缓存
                WebStorage.getInstance().deleteAllData()
            }
            launch {
                // 删除 内部存储 Download 中 "聚在工大_" 开头的 .apk 文件
                val publicDownloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                publicDownloadDir?.listFiles()?.forEach {
                    if (it.name.startsWith("聚在工大_") && it.name.endsWith(".apk")) {
                        totalDeletedBytes += it.length()
                        it.delete()
                    }
                }
            }
        }.await()
        // 转换为 MB，保留两位小数
        return@withContext (totalDeletedBytes.toDouble() / (1024 * 1024)).let {
            String.format("%.2f", it).toDouble()
        }
    }
}

// 工具方法：递归获取文件或文件夹大小
fun getFileSize(file: File): Long {
    return if (file.isFile) {
        file.length()
    } else {
        file.listFiles()?.sumOf { getFileSize(it) } ?: 0L
    }
}