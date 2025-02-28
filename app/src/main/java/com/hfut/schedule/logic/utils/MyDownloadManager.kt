package com.hfut.schedule.logic.utils

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.utils.ocr.TesseractUtils
import com.hfut.schedule.logic.utils.ocr.TesseractUtils.moveDownloadedModel
import com.hfut.schedule.ui.activity.home.cube.items.subitems.update.installApk
import java.io.File

object MyDownloadManager {
    enum class DownloadIds(val id : Long) {
        UPDATE(1),
        ML(2)
    }
    val dlManager = MyApplication.context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun downloadManage(fileName: String, url: String, destinationDir: String,dlId: DownloadIds, onDownloadComplete: (Uri?) -> Unit) {
        val request = DownloadManager.Request(Uri.parse(url))
        request.setTitle("下载 $fileName 中")
        request.setDescription("请等待...")

        // 设置目标下载路径
        val fileUri = Uri.fromFile(File(Environment.getExternalStoragePublicDirectory(destinationDir), fileName))
        request.setDestinationUri(fileUri)

        // 设置下载通知栏可见
        request.setNotificationVisibility(
            DownloadManager.Request.VISIBILITY_VISIBLE or DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
        )

        // 获取 DownloadManager
//        val downloadManager = MyApplication.context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = dlManager.enqueue(request)

        // 存储下载任务 ID
        SharePrefs.saveLong("download_${dlId.id}", downloadId)

        // 注册下载完成监听
        MyApplication.context.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                    val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                    if (id == downloadId) {
                        val uri = dlManager.getUriForDownloadedFile(id)
                        onDownloadComplete(uri)
                    }
                }
            }
        }, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_NOT_EXPORTED)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun update(version: String) {
        downloadManage(
            fileName = "聚在工大_${version}.apk",
            url = "${MyApplication.UpdateURL}releases/download/Android/${version}.apk",
            dlId= DownloadIds.UPDATE,
            destinationDir = Environment.DIRECTORY_DOWNLOADS
        ) { uri ->
            if (uri != null) installApk(uri)
        }
    }

    @SuppressLint("Range")
    fun getDownloadProgress(downloadId: Long): Int {
//        val downloadManager = MyApplication.context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = dlManager.query(query)

        if (cursor != null && cursor.moveToFirst()) {
            val totalSize = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
            val downloadedSize = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
            cursor.close()
            if (totalSize > 0) {
                return (downloadedSize * 100L / totalSize).toInt()
            }
        }
        return 0
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun downloadMl() {
        val filename = TesseractUtils.filename
        downloadManage(
            fileName = filename,
            url = "https://gitee.com/chiu-xah/HFUT-Schedule/releases/download/Android/eng.traineddata",
            destinationDir = Environment.DIRECTORY_DOWNLOADS,
            dlId = DownloadIds.ML
        ) { uri ->
            if (uri != null) {
                moveDownloadedModel()
            }
        }
    }
    fun getDownloadId(dlId : DownloadIds) : Long {
        return SharePrefs.prefs.getLong("download_${dlId.id}",-1)
    }
}
