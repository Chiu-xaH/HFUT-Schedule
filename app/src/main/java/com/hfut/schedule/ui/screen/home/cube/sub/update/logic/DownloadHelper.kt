package com.hfut.schedule.ui.screen.home.cube.sub.update.logic

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import com.xah.common.logic.util.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File

sealed class DownloadResult {
    data class Downloaded(val file : File) : DownloadResult()
    data class Progress(val downloadId: Long, val progress: Int) : DownloadResult()
    data class Success(val downloadId: Long, val file: File, val uri: Uri) : DownloadResult()
    data class Failed(val downloadId: Long, val reason: String?) : DownloadResult()
    object Prepare : DownloadResult()
}

fun downloadFile(
    context: Context,
    url: String,
    fileName: String,
    delayTimesLong: Long = 1000L,
    requestBuilder: (DownloadManager.Request) -> DownloadManager.Request = { it },
    customDownloadId: Long? = null
): Flow<DownloadResult> = flow {
    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val destDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    if (!destDir.exists()) {
        destDir.mkdirs()
    }

    val destFile = File(destDir, fileName)

    if (destFile.exists()) {
        // 下载过
        emit(DownloadResult.Downloaded(destFile))
        return@flow
    }

    var request = DownloadManager.Request(url.toUri())
        .setTitle(fileName)
        .setDescription("下载 $fileName")
        .setDestinationUri(Uri.fromFile(destFile))
        .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        .addRequestHeader("Range", "bytes=0-")

    request = requestBuilder(request)

    val downloadId = customDownloadId ?: downloadManager.enqueue(request)

    val query = DownloadManager.Query().setFilterById(downloadId)

    var downloading = true
    while (downloading) {
        val cursor = downloadManager.query(query)
        if (cursor != null && cursor.moveToFirst()) {
            val bytesDownloaded = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
            val bytesTotal = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

            if (bytesTotal > 0) {
                val progress = (bytesDownloaded * 100 / bytesTotal).toInt()
                emit(DownloadResult.Progress(downloadId, progress))
            }

            when (cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))) {
                DownloadManager.STATUS_SUCCESSFUL -> {
                    downloading = false
                    emit(
                        DownloadResult.Success(
                            downloadId = downloadId,
                            file = destFile,
                            uri = Uri.fromFile(destFile)
                        )
                    )
                }

                DownloadManager.STATUS_FAILED -> {
                    downloading = false
                    cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON)).let { reason ->
                        val msg = when (reason) {
                            DownloadManager.ERROR_CANNOT_RESUME -> "不能恢复下载（可能是服务器不支持断点续传）"
                            DownloadManager.ERROR_DEVICE_NOT_FOUND -> "设备存储不可用"
                            DownloadManager.ERROR_FILE_ERROR -> "文件系统错误"
                            DownloadManager.ERROR_HTTP_DATA_ERROR -> "HTTP 数据错误（网络或服务器断开）"
                            DownloadManager.ERROR_INSUFFICIENT_SPACE -> "存储空间不足"
                            DownloadManager.ERROR_TOO_MANY_REDIRECTS -> "重定向次数太多"
                            DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> "无法处理的 HTTP 响应码"
                            DownloadManager.ERROR_UNKNOWN -> "未知错误"
                            else -> "原因: $reason"
                        }
                        LogUtil.debug("下载失败: $msg")
//                        Log.e("Download", "下载失败: $msg")
                        emit(DownloadResult.Failed(downloadId, msg))
                    }
                }
            }
        }
        cursor?.close()
        delay(delayTimesLong)
    }
}.flowOn(Dispatchers.IO)
