package com.hfut.schedule.logic.util.sys

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.ocr.TesseractUtils
import com.hfut.schedule.logic.util.ocr.TesseractUtils.moveDownloadedModel
import com.hfut.schedule.logic.enumeration.BroadcastAction
import com.hfut.schedule.receiver.UpdateReceiver
import java.io.File

object AppDownloadManager {
    enum class DownloadIds(val id : Long) {
        UPDATE(1),
        ML(2),
        PATCH(3)
    }

    val dlManager = MyApplication.context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun downloadManage(fileName: String, url: String, destinationDir: String, dlId: DownloadIds, onDownloadComplete: (Uri?) -> Unit) {
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
        SharedPrefs.saveLong("download_${dlId.id}", downloadId)

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
    fun downloadPatch(filename: String,activity: Activity) {
        PermissionSet.checkAndRequestNotificationPermission(activity)
        downloadManage(
            fileName = filename,
            url = "${MyApplication.GITEE_UPDATE_URL}releases/download/Android/$filename",
            dlId= DownloadIds.PATCH,
            destinationDir = Environment.DIRECTORY_DOWNLOADS
        ) { _ -> }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun update(version: String,activity: Activity) {
        PermissionSet.checkAndRequestNotificationPermission(activity)
        downloadManage(
            fileName = "聚在工大_${version}.apk",
            url = "${MyApplication.GITEE_UPDATE_URL}releases/download/Android/${version}.apk",
            dlId= DownloadIds.UPDATE,
            destinationDir = Environment.DIRECTORY_DOWNLOADS
        ) { uri ->
            if (uri != null) installApk()
        }
    }

    @SuppressLint("Range")
    fun getDownloadProgress(downloadId: Long): Int {
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = dlManager.query(query)

        if (cursor != null && cursor.moveToFirst()) {
            // 获取文件本地路径
            val fileUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
            val filePath = fileUri?.let { Uri.parse(it).path }  // 解析文件路径
            val file = filePath?.let { File(it) }

            // 如果文件不存在，直接返回 0
            if (file == null || !file.exists()) {
                cursor.close()
                return 0
            }

            val totalSize = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
            val downloadedSize = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
            cursor.close()
            if (totalSize > 0) {
                return (downloadedSize * 100L / totalSize).toInt()
            }
        }
        return 0
    }

    @JvmStatic
    fun isExistFile(fileName : String) : String? {
        // 判断内部存储的文件下载文件夹是否存在此文件
        // 获取 Download 目录
        val downloadDir = getDownloadDirectory() ?: return null
        // 拼接文件路径
        val patchFile = File(downloadDir, fileName)
        // 检查文件是否存在
        return if (patchFile.exists()) patchFile.absolutePath else null
    }

    @JvmStatic
    private fun getDownloadDirectory(): File? {
        // 获取设备内部存储的Download文件夹路径
        val downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (downloadDirectory.exists()) {
            return downloadDirectory
        }
        return null
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

    @JvmStatic
    fun getDownloadId(dlId : DownloadIds) : Long = SharedPrefs.prefs.getLong("download_${dlId.id}",-1)

    fun installApk() {
        val id = getDownloadId(DownloadIds.UPDATE)
        val uri = dlManager.getUriForDownloadedFile(id)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        MyApplication.context.startActivity(intent)
    }

    fun openDownload() {
        val intent = Intent()
        intent.action = DownloadManager.ACTION_VIEW_DOWNLOADS
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        try {
            MyApplication.context.startActivity(intent)
        } catch (e: Exception) {
            // 部分定制 ROM 可能不支持此 Intent，可以跳转到存储设置
            val storageIntent = Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS)
            try {
                MyApplication.context.startActivity(storageIntent)
            } catch (ex: Exception) {
                // 最后尝试打开文件管理器
                val fileManagerIntent = Intent(Intent.ACTION_VIEW)
                fileManagerIntent.data = Uri.parse("content://downloads/public_downloads")
                MyApplication.context.startActivity(fileManagerIntent)
            }
        }
    }

    enum class DownloadStatus {
        DOWNLOADING,STOPPED,WAITING,OK,FAILURE,UNKNOWN,NOT_FOUND
    }


    fun getDownloadStatus(downloadId: Long): DownloadStatus {
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor: Cursor? = dlManager.query(query)

        cursor?.use {
            if (it.moveToFirst()) {
                val statusIndex = it.getColumnIndex(DownloadManager.COLUMN_STATUS)
                if (statusIndex != -1) {
                    return when (it.getInt(statusIndex)) {
                        DownloadManager.STATUS_RUNNING -> DownloadStatus.DOWNLOADING
                        DownloadManager.STATUS_PAUSED -> DownloadStatus.STOPPED
                        DownloadManager.STATUS_PENDING -> DownloadStatus.WAITING
                        DownloadManager.STATUS_SUCCESSFUL -> DownloadStatus.OK
                        DownloadManager.STATUS_FAILED -> DownloadStatus.FAILURE
                        else -> DownloadStatus.UNKNOWN
                    }
                }
            }
        }
        return DownloadStatus.NOT_FOUND
    }

    @JvmStatic
    fun noticeInstall() : Int {
        val intent = Intent(MyApplication.context, UpdateReceiver::class.java).apply {
            action = BroadcastAction.INSTALL_APK.name
        }

        val pendingIntent = PendingIntent.getBroadcast(
            MyApplication.context,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return AppNotificationManager.sendNotification(
            channel = AppNotificationManager.AppNotificationChannel.DOWNLOAD_OK,
            content = "新版本准备就绪,点击安装",
            intent = pendingIntent
        )
    }
    var refused = false
}
